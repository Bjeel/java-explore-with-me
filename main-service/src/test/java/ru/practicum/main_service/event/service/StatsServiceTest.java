package ru.practicum.main_service.event.service;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import ru.practicum.main_service.event.dto.RequestStats;
import ru.practicum.main_service.event.model.Event;
import ru.practicum.main_service.event.repository.RequestRepository;
import ru.practicum.stats_client.StatsClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class StatsServiceTest {
  @Mock
  private StatsClient statsClient;

  @Mock
  private RequestRepository requestRepository;

  @InjectMocks
  private StatsServiceImpl statsService;
  private final Event event1 = Event.builder()
    .id(1L)
    .createdOn(LocalDateTime.now().minusDays(5))
    .publishedOn(LocalDateTime.now().minusHours(3))
    .build();
  private final Event event2 = Event.builder()
    .id(2L)
    .createdOn(LocalDateTime.now().minusDays(1))
    .publishedOn(LocalDateTime.now().minusDays(3))
    .build();
  private final Event event3 = Event.builder()
    .id(3L)
    .createdOn(LocalDateTime.now().minusDays(1))
    .publishedOn(null)
    .build();
  private final RequestStats requestStats1 = RequestStats.builder()
    .eventId(1L)
    .confirmedRequests(40L)
    .build();
  private final RequestStats requestStats2 = RequestStats.builder()
    .eventId(2L)
    .confirmedRequests(16L)
    .build();

  @Nested
  class AddHit {
    @Test
    public void shouldAdd() {
      statsService.addHit(new MockHttpServletRequest());

      verify(statsClient, times(1)).addHit(any(), any(), any(), any());
    }
  }

  @Nested
  class GetConfirmedRequests {
    @Test
    public void shouldGet() {
      when(requestRepository.getConfirmedRequests(List.of(event1.getId(), event2.getId())))
        .thenReturn(List.of(requestStats1, requestStats2));

      Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event1, event2, event3));

      assertEquals(2, confirmedRequests.values().size());
      assertEquals(requestStats1.getConfirmedRequests(), confirmedRequests.get(event1.getId()));
      assertEquals(requestStats2.getConfirmedRequests(), confirmedRequests.get(event2.getId()));

      verify(requestRepository, times(1)).getConfirmedRequests(any());
    }

    @Test
    public void shouldGetEmpty() {
      Map<Long, Long> confirmedRequests = statsService.getConfirmedRequests(List.of(event3));

      assertTrue(confirmedRequests.values().isEmpty());

      verify(statsClient, never()).getStats(any(), any(), any(), any());
    }
  }
}
