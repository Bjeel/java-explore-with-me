package ru.practicum.main_service.category.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main_service.MainCommonUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryDto {
  Long id;

  @NotBlank
  @Size(max = MainCommonUtils.MAX_LENGTH_NAME)
  String name;
}
