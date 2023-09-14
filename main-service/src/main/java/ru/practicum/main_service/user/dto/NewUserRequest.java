package ru.practicum.main_service.user.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main_service.MainCommonUtils;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class NewUserRequest {
  @Email
  @NotBlank
  @Size(min = MainCommonUtils.MIN_LENGTH_EMAIL, max = MainCommonUtils.MAX_LENGTH_EMAIL)
  String email;

  @NotBlank
  @Size(min = MainCommonUtils.MIN_LENGTH_NAME, max = MainCommonUtils.MAX_LENGTH_USER_NAME)
  String name;
}
