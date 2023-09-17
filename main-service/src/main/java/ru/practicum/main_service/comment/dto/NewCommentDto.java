package ru.practicum.main_service.comment.dto;

import lombok.*;
import lombok.experimental.FieldDefaults;
import ru.practicum.main_service.MainCommonUtils;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@FieldDefaults(level = AccessLevel.PRIVATE)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewCommentDto {
    @NotBlank
    @Size(min = MainCommonUtils.MIN_LENGTH_COMMENT, max = MainCommonUtils.MAX_LENGTH_COMMENT)
    String text;
}
