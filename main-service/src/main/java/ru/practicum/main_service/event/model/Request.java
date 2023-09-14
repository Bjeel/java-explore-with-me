package ru.practicum.main_service.event.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.main_service.event.enums.RequestStatus;
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
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "requests", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Request {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "event_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  Event event;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "requester_id", referencedColumnName = "id")
  @OnDelete(action = OnDeleteAction.CASCADE)
  User requester;

  @NotNull
  LocalDateTime created;

  @NotNull
  @Enumerated(EnumType.STRING)
  RequestStatus status;
}
