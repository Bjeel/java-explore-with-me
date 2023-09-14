package ru.practicum.main_service.event.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateRequest;
import ru.practicum.main_service.event.dto.EventRequestStatusUpdateResult;
import ru.practicum.main_service.event.dto.ParticipationRequestDto;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.enums.RequestStatus;
import ru.practicum.main_service.event.enums.RequestStatusAction;
import ru.practicum.main_service.event.mapper.RequestMapper;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.model.Request;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class RequestServiceImpl implements RequestService {
  private final UserService userService;
  private final EventService eventService;
  private final StatsService statsService;
  private final RequestRepository requestRepository;
  private final RequestMapper requestMapper;

  @Override
  public List<ParticipationRequestDto> getEventRequestsByRequester(Long userId) {
    log.info("Get participation into event by id {}", userId);

    userService.getUserById(userId);

    return toParticipationRequestsDto(requestRepository.findAllByRequesterId(userId));
  }

  @Override
  @Transactional
  public ParticipationRequestDto createEventRequest(Long userId, Long eventId) {
    log.info("Create participation for event by id {} from user id {}", eventId, userId);

    User user = userService.getUserById(userId);
    Event event = eventService.getEventById(eventId);

    if (Objects.equals(event.getInitiator().getId(), userId)) {
      log.error("Can't create participant on own event");
      throw new ForbiddenException("Can't create participant on own event");
    }

    if (!event.getState().equals(EventState.PUBLISHED)) {
      log.error("Can't create participant on unpublished event");
      throw new ForbiddenException("Can't create participant on unpublished event");
    }

    Optional<Request> oldRequest = requestRepository.findByEventIdAndRequesterId(eventId, userId);

    if (oldRequest.isPresent()) {
      log.error("Can't create repeated request");
      throw new ForbiddenException("Can't create repeated request");
    }

    checkIsNewLimitGreaterOld(
      statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) + 1,
      event.getParticipantLimit()
    );

    Request newRequest = Request.builder()
      .event(event)
      .requester(user)
      .created(LocalDateTime.now())
      .build();

    if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
      newRequest.setStatus(RequestStatus.CONFIRMED);
    } else {
      newRequest.setStatus(RequestStatus.PENDING);
    }

    return requestMapper.toParticipationRequestDto(requestRepository.save(newRequest));
  }

  @Override
  @Transactional
  public ParticipationRequestDto cancelEventRequest(Long userId, Long requestId) {
    log.info("Reject request id {} for user id {}", requestId, userId);

    userService.getUserById(userId);

    Request request = requestRepository.findById(requestId)
      .orElseThrow(() -> {
        log.error("Request not found");
        return new NotFoundException("Request not found");
      });

    checkUserIsOwner(request.getRequester().getId(), userId);

    request.setStatus(RequestStatus.CANCELED);

    return requestMapper.toParticipationRequestDto(requestRepository.save(request));
  }

  @Override
  public List<ParticipationRequestDto> getEventRequestsByEventOwner(Long userId, Long eventId) {
    log.info("Get event's participants list id {} owner id {}", eventId, userId);

    userService.getUserById(userId);
    Event event = eventService.getEventById(eventId);

    checkUserIsOwner(event.getInitiator().getId(), userId);

    return toParticipationRequestsDto(requestRepository.findAllByEventId(eventId));
  }

  @Override
  @Transactional
  public EventRequestStatusUpdateResult patchEventRequestsByEventOwner(
    Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
    log.info("Update event's requests id {} owner id {} with new fields {}",
      eventId, userId, eventRequestStatusUpdateRequest);

    userService.getUserById(userId);
    Event event = eventService.getEventById(eventId);

    checkUserIsOwner(event.getInitiator().getId(), userId);

    if (!event.getRequestModeration() ||
      event.getParticipantLimit() == 0 ||
      eventRequestStatusUpdateRequest.getRequestIds().isEmpty()) {
      return new EventRequestStatusUpdateResult(List.of(), List.of());
    }

    List<Request> confirmedList = new ArrayList<>();
    List<Request> rejectedList = new ArrayList<>();

    List<Request> requests = requestRepository.findAllByIdIn(eventRequestStatusUpdateRequest.getRequestIds());

    if (requests.size() != eventRequestStatusUpdateRequest.getRequestIds().size()) {
      log.error("Have not existing events");
      throw new NotFoundException("Have not existing events");
    }

    if (!requests.stream()
      .map(Request::getStatus)
      .allMatch(RequestStatus.PENDING::equals)) {
      log.error("Can't change request with status PENDING");
      throw new ForbiddenException("Can't change request with status PENDING");
    }

    if (eventRequestStatusUpdateRequest.getStatus().equals(RequestStatusAction.REJECTED)) {
      rejectedList.addAll(changeStatusAndSave(requests, RequestStatus.REJECTED));
    } else {
      Long newConfirmedRequests = statsService.getConfirmedRequests(List.of(event)).getOrDefault(eventId, 0L) +
        eventRequestStatusUpdateRequest.getRequestIds().size();

      checkIsNewLimitGreaterOld(newConfirmedRequests, event.getParticipantLimit());

      confirmedList.addAll(changeStatusAndSave(requests, RequestStatus.CONFIRMED));

      if (newConfirmedRequests >= event.getParticipantLimit()) {
        rejectedList.addAll(changeStatusAndSave(
          requestRepository.findAllByEventIdAndStatus(eventId, RequestStatus.PENDING),
          RequestStatus.REJECTED)
        );
      }
    }

    return new EventRequestStatusUpdateResult(toParticipationRequestsDto(confirmedList),
      toParticipationRequestsDto(rejectedList));
  }

  private List<ParticipationRequestDto> toParticipationRequestsDto(List<Request> requests) {
    return requests.stream()
      .map(requestMapper::toParticipationRequestDto)
      .collect(Collectors.toList());
  }

  private List<Request> changeStatusAndSave(List<Request> requests, RequestStatus status) {
    requests.forEach(request -> request.setStatus(status));
    return requestRepository.saveAll(requests);
  }

  private void checkIsNewLimitGreaterOld(Long newLimit, Integer eventParticipantLimit) {
    if (eventParticipantLimit != 0 && (newLimit > eventParticipantLimit)) {
      log.error("Requests limit: {}", eventParticipantLimit);
      throw new ForbiddenException(String.format("Requests limit: %d",
        eventParticipantLimit));
    }
  }

  private void checkUserIsOwner(Long id, Long userId) {
    if (!Objects.equals(id, userId)) {
      log.error("User is not owner");
      throw new ForbiddenException("User is not owner");
    }
  }
}
