package ru.practicum.hit;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "hits")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Hit {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @NotBlank
  String app;

  @NotBlank
  String uri;

  @NotBlank
  String ip;

  @NotBlank
  @FutureOrPresent
  LocalDateTime timestamp;
}
