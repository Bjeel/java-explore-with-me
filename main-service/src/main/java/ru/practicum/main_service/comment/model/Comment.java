package ru.practicum.main_service.comment.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.main_service.MainCommonUtils;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Comment {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @NotNull
  @Size(max = MainCommonUtils.MAX_LENGTH_COMMENT)
  String text;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "author_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  User author;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  Event event;

  @NotNull
  LocalDateTime createdOn;

  LocalDateTime editedOn;
}
