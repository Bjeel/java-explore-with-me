package ru.practicum.hit;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;

@Builder
@Data
public class HitDto {
  @NotBlank
  String app;

  @NotBlank
  String uri;

  @NotNull
  @PositiveOrZero
  Long hits;
}
