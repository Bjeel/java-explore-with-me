package ru.practicum.stats_client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.stats_common.StatsCommonUtils;
import ru.practicum.stats_common.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class StatsClient extends BaseClient {
  @Autowired
  public StatsClient(@Value("${stats-server.url}") String serverUrl, RestTemplateBuilder builder) {
    super(builder
      .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
      .requestFactory(HttpComponentsClientHttpRequestFactory::new)
      .build()
    );
  }

  public ResponseEntity<Object> addHit(String appName, String uri, String ip, LocalDateTime timestamp) {
    log.info("Отправка запроса на регистрацию обращения к appName = {}, uri = {}, ip = {}, timestamp = {}",
      appName, uri, ip, timestamp);

    EndpointHit endpointHit = EndpointHit.builder()
      .app(appName)
      .uri(uri)
      .ip(ip)
      .timestamp(timestamp.format(StatsCommonUtils.DT_FORMATTER))
      .build();
    return post(StatsCommonUtils.HIT_ENDPOINT, endpointHit);
  }

  public ResponseEntity<Object> getStats(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
    log.info("Отправка запроса на получение статистики по параметрам start = {}, end = {}, uris = {}, unique = {}",
      start, end, uris, unique);

    if (start == null || end == null || start.isAfter(end)) {
      throw new IllegalArgumentException("Недопустимый временной промежуток.");
    }

    StringBuilder uriBuilder = new StringBuilder(StatsCommonUtils.STATS_ENDPOINT + "?start={start}&end={end}");
    Map<String, Object> parameters = Map.of(
      "start", start.format(StatsCommonUtils.DT_FORMATTER),
      "end", end.format(StatsCommonUtils.DT_FORMATTER)
    );

    if (uris != null && !uris.isEmpty()) {
      for (String uri : uris) {
        uriBuilder.append("&uris=").append(uri);
      }
    }
    if (unique != null) {
      uriBuilder.append("&unique=").append(unique);
    }

    return get(uriBuilder.toString(), parameters);
  }
}
