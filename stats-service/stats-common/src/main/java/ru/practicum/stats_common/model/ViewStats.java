package ru.practicum.stats_common.model;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ViewStats {
  String app;
  String uri;
  Long hits;
}
