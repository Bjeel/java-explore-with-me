package ru.practicum.main_service.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.main_service.MainCommonUtils;
import ru.practicum.main_service.category.model.Category;
import ru.practicum.main_service.event.enums.EventState;
import ru.practicum.main_service.user.model.User;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "events", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Size(max = MainCommonUtils.MAX_LENGTH_TITLE)
  @NotBlank
  String title;

  @Size(max = MainCommonUtils.MAX_LENGTH_ANNOTATION)
  @NotBlank
  String annotation;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id", referencedColumnName = "id")
  Category category;

  @Size(max = MainCommonUtils.MAX_LENGTH_DESCRIPTION)
  @NotBlank
  String description;

  @NotNull
  Boolean paid;

  @NotNull
  Integer participantLimit;

  @NotNull
  LocalDateTime eventDate;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "location_id", referencedColumnName = "id")
  Location location;

  @NotNull
  LocalDateTime createdOn;

  @NotNull
  @Enumerated(EnumType.STRING)
  EventState state;

  LocalDateTime publishedOn;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "user_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  User initiator;

  @NotNull
  Boolean requestModeration;
}
