package ru.practicum.stats_common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@Builder
@AllArgsConstructor
public class ViewStats {
  @NotBlank
  String app;
  @NotBlank
  String uri;

  @NotNull
  Long hits;
}
