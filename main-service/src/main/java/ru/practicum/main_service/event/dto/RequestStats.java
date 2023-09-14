package ru.practicum.main_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RequestStats {
  Long eventId;
  Long confirmedRequests;
}
