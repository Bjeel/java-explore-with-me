package ru.practicum.stats.stats;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping()
@AllArgsConstructor
@Slf4j
public class StatsController {
  @GetMapping("/stats")
  @ResponseStatus(HttpStatus.OK)
  public List<Object> findAll() {
    return List.of("123");
  }
}
