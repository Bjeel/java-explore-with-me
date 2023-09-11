package ru.practicum.main_service.compilation.model;

import lombok.*;
import lombok.experimental.FieldDefaults;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;
import ru.practicum.main_service.MainCommonUtils;
import ru.practicum.main_service.event.model.Event;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table(name = "compilations", schema = "public")
@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Compilation {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  Long id;

  @Column(unique = true)
  @Size(max = MainCommonUtils.MAX_LENGTH_TITLE)
  @NotBlank
  String title;

  @NotNull
  Boolean pinned;

  @ManyToMany
  @JoinTable(name = "compilations_events",
    joinColumns = @JoinColumn(name = "compilation_id", referencedColumnName = "id"),
    inverseJoinColumns = @JoinColumn(name = "event_id", referencedColumnName = "id"))
  @OnDelete(action = OnDeleteAction.CASCADE)
  List<Event> events;
}
