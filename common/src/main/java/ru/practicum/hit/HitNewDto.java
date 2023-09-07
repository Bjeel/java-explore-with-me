package ru.practicum.hit;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Builder
@Data
public class HitNewDto {
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
