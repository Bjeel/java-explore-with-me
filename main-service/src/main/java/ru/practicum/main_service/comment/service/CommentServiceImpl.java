package ru.practicum.main_service.comment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.main_service.comment.dto.CommentDto;
import ru.practicum.main_service.comment.dto.NewCommentDto;
import ru.practicum.main_service.comment.mapper.CommentMapper;
import ru.practicum.main_service.comment.model.Comment;
import ru.practicum.main_service.comment.repository.CommentRepository;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.service.EventService;
import ru.practicum.main_service.exception.ForbiddenException;
import ru.practicum.main_service.exception.NotFoundException;
import ru.practicum.main_service.user.model.User;
import ru.practicum.main_service.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class CommentServiceImpl implements CommentService {
  private final UserService userService;
  private final EventService eventService;
  private final CommentRepository commentRepository;
  private final CommentMapper commentMapper;

  @Override
  public List<CommentDto> getCommentsByAdmin(Pageable pageable) {
    log.info("Get all comments with pagination {}", pageable);

    return commentMapper.toCommentDtoList(commentRepository.findAll(pageable).toList());
  }

  @Override
  @Transactional
  public void deleteByAdmin(Long commentId) {
    log.info("Delete comment {}", commentId);

    commentRepository.deleteById(commentId);
  }

  @Override
  public List<CommentDto> getCommentsByPrivate(Long userId, Long eventId, Pageable pageable) {
    log.info("Get all user's comments from user by id {} to event with id {} an pagination {}",
      userId, eventId, pageable);

    userService.getUserById(userId);

    List<Comment> comments;
    if (eventId != null) {
      eventService.getEventById(eventId);

      comments = commentRepository.findAllByAuthorIdAndEventId(userId, eventId);
    } else {
      comments = commentRepository.findAllByAuthorId(userId);
    }

    return commentMapper.toCommentDtoList(comments);
  }

  @Override
  @Transactional
  public CommentDto createByPrivate(Long userId, Long eventId, NewCommentDto newCommentDto) {
    log.info("Create comment for event with id {} from userwith id {} with next fields {}",
      eventId, userId, newCommentDto);

    User user = userService.getUserById(userId);
    Event event = eventService.getEventById(eventId);

    if (!event.getState().equals(EventState.PUBLISHED)) {
      log.error("Can't create comment for unpublished event");
      throw new ForbiddenException("Can't create comment for unpublished event");
    }

    Comment comment = Comment.builder()
      .text(newCommentDto.getText())
      .author(user)
      .event(event)
      .createdOn(LocalDateTime.now())
      .build();

    return commentMapper.toCommentDto(commentRepository.save(comment));
  }

  @Override
  @Transactional
  public CommentDto patchByPrivate(Long userId, Long commentId, NewCommentDto newCommentDto) {
    log.info("Update comment with id {} from user id {} and new fields {}", commentId, userId, newCommentDto);

    userService.getUserById(userId);

    Comment commentFromRepository = getCommentById(commentId);

    checkUserIsOwner(userId, commentFromRepository.getAuthor().getId());

    commentFromRepository.setText(newCommentDto.getText());
    commentFromRepository.setEditedOn(LocalDateTime.now());

    return commentMapper.toCommentDto(commentFromRepository);
  }

  @Override
  @Transactional
  public void deleteByPrivate(Long userId, Long commentId) {
    log.info("Delete comment id {} from user id {}", commentId, userId);

    userService.getUserById(userId);

    checkUserIsOwner(userId, getCommentById(commentId).getAuthor().getId());

    commentRepository.deleteById(commentId);
  }

  @Override
  public List<CommentDto> getCommentsByPublic(Long eventId, Pageable pageable) {
    log.info("Get all published comments for event {} with pagination {}", eventId, pageable);

    eventService.getEventById(eventId);

    return commentMapper.toCommentDtoList(commentRepository.findAllByEventId(eventId, pageable));
  }

  @Override
  public CommentDto getCommentByPublic(Long commentId) {
    log.info("Get comment с id {}", commentId);

    return commentMapper.toCommentDto(getCommentById(commentId));
  }

  private Comment getCommentById(Long commentId) {
    return commentRepository.findById(commentId)
      .orElseThrow(() -> {
        log.error("Comment not found");
        return new NotFoundException("Comment not found");
      });
  }

  private void checkUserIsOwner(Long id, Long userId) {
    if (!Objects.equals(id, userId)) {
      log.error("User is not a owner");
      throw new ForbiddenException("User is not a owner");
    }
  }
}
