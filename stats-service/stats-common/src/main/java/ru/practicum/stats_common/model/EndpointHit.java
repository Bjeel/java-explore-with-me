package ru.practicum.stats_common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotBlank;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EndpointHit {
  @NotBlank
  String app;

  @NotBlank
  String uri;

  @NotBlank
  String ip;

  @NotBlank
  String timestamp;
}
