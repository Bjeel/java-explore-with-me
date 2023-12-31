package ru.practicum.stats_server.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.stats_common.StatsCommonUtils;
import ru.practicum.stats_common.model.EndpointHit;
import ru.practicum.stats_common.model.ViewStats;
import ru.practicum.stats_server.service.StatsService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class StatsController {
  private final StatsService statsService;

  @PostMapping(StatsCommonUtils.HIT_ENDPOINT)
  @ResponseStatus(HttpStatus.CREATED)
  public ViewStats addHit(@Valid @RequestBody EndpointHit endpointHit) {
    return statsService.addHit(endpointHit);
  }

  @GetMapping(StatsCommonUtils.STATS_ENDPOINT)
  public List<ViewStats> getStats(@RequestParam @DateTimeFormat(pattern = StatsCommonUtils.DT_FORMAT) LocalDateTime start,
                                  @RequestParam @DateTimeFormat(pattern = StatsCommonUtils.DT_FORMAT) LocalDateTime end,
                                  @RequestParam(required = false) List<String> uris,
                                  @RequestParam(defaultValue = "false") Boolean unique) {
    if (start.isAfter(end)) {
      log.error("Недопустимый временной промежуток.");
      throw new IllegalArgumentException("Недопустимый временной промежуток.");
    }

    return statsService.getStats(start, end, uris, unique);
  }
}
