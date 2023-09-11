package ru.practicum.main_service.event.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;

import java.util.List;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EventRequestStatusUpdateResult {
  List<ParticipationRequestDto> confirmedRequests;
  List<ParticipationRequestDto> rejectedRequests;
}
