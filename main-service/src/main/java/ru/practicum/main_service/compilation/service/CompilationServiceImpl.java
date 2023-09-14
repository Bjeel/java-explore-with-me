package ru.practicum.main_service.compilation.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.compilation.dto.CompilationDto;
import ru.practicum.main_service.compilation.dto.NewCompilationDto;
import ru.practicum.main_service.compilation.dto.UpdateCompilationRequest;
import ru.practicum.main_service.compilation.mapper.CompilationMapper;
import ru.practicum.main_service.compilation.model.Compilation;
import ru.practicum.main_service.compilation.repository.CompilationRepository;
import ru.practicum.main_service.event.dto.EventShortDto;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.NotFoundException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CompilationServiceImpl implements CompilationService {
  private final EventService eventService;
  private final CompilationRepository compilationRepository;
  private final CompilationMapper compilationMapper;

  @Override
  @Transactional
  public CompilationDto create(NewCompilationDto newCompilationDto) {
    log.info("Create new compilation {}", newCompilationDto);

    List<Event> events = new ArrayList<>();

    if (!newCompilationDto.getEvents().isEmpty()) {
      events = eventService.getEventsByIds(newCompilationDto.getEvents());
      checkSize(events, newCompilationDto.getEvents());
    }

    Compilation compilation = compilationRepository.save(compilationMapper.newDtoToCompilation(newCompilationDto, events));

    return getById(compilation.getId());
  }

  @Override
  @Transactional
  public CompilationDto patch(Long compId, UpdateCompilationRequest updateCompilationRequest) {
    log.info("Update compilation id: {}, updated fields: {}", compId, updateCompilationRequest);

    Compilation compilation = getCompilationById(compId);

    if (updateCompilationRequest.getTitle() != null && !updateCompilationRequest.getTitle().isBlank()) {
      compilation.setTitle(updateCompilationRequest.getTitle());
    }

    if (updateCompilationRequest.getPinned() != null) {
      compilation.setPinned(updateCompilationRequest.getPinned());
    }

    if (updateCompilationRequest.getEvents() != null) {
      List<Event> events = eventService.getEventsByIds(updateCompilationRequest.getEvents());

      checkSize(events, updateCompilationRequest.getEvents());

      compilation.setEvents(events);
    }

    return getById(compId);
  }

  @Override
  @Transactional
  public void deleteById(Long compId) {
    log.info("Delete compilation by id {}", compId);

    getCompilationById(compId);

    compilationRepository.deleteById(compId);
  }

  @Override
  public List<CompilationDto> getAll(Boolean pinned, Pageable pageable) {
    log.info("Get all compilation: pinned = {}, pageable = {}", pinned, pageable);

    List<Compilation> compilations;

    if (pinned == null) {
      compilations = compilationRepository.findAll(pageable).toList();
    } else {
      compilations = compilationRepository.findAllByPinned(pinned, pageable);
    }

    Set<Event> uniqueEvents = new HashSet<>();
    compilations.forEach(compilation -> uniqueEvents.addAll(compilation.getEvents()));

    Map<Long, EventShortDto> eventsShortDto = new HashMap<>();
    eventService.toEventsShortDto(new ArrayList<>(uniqueEvents))
      .forEach(event -> eventsShortDto.put(event.getId(), event));

    List<CompilationDto> result = new ArrayList<>();
    compilations.forEach(compilation -> {
      List<EventShortDto> compEventsShortDto = new ArrayList<>();
      compilation.getEvents()
        .forEach(event -> compEventsShortDto.add(eventsShortDto.get(event.getId())));
      result.add(compilationMapper.toCompilationDto(compilation, compEventsShortDto));
    });

    return result;
  }

  @Override
  public CompilationDto getById(Long compId) {
    log.info("Get compilation by id {}", compId);

    Compilation compilation = getCompilationById(compId);

    List<EventShortDto> eventsShortDto = eventService.toEventsShortDto(compilation.getEvents());

    return compilationMapper.toCompilationDto(compilation, eventsShortDto);
  }

  private Compilation getCompilationById(Long compId) {
    return compilationRepository.findById(compId)
      .orElseThrow(() -> {
        log.error("Compilation not found");
        return new NotFoundException("Compilation not found");
      });
  }

  private void checkSize(List<Event> events, List<Long> eventsIdToUpdate) {
    if (events.size() != eventsIdToUpdate.size()) {
      log.error("Events not found");
      throw new NotFoundException("Events not found");
    }
  }
}
