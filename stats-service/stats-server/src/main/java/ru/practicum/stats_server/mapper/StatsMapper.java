package ru.practicum.stats_server.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.stats_common.model.EndpointHit;
import ru.practicum.stats_common.model.ViewStats;
import ru.practicum.stats_server.model.Stats;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface StatsMapper {
  @Mapping(target = "timestamp", expression = "java(timestamp)")
  Stats toStats(EndpointHit endpointHit, LocalDateTime timestamp);

  @Mapping(target = "app", source = "app")
  @Mapping(target = "uri", source = "app")
  @Mapping(target = "hits", constant = "1L")
  ViewStats toViewStats(Stats stats);
}
