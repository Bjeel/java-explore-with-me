package ru.practicum.main_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import javax.validation.constraints.NotNull;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class LocationDto {
  @NotNull
  Float lat;

  @NotNull
  Float lon;
}
