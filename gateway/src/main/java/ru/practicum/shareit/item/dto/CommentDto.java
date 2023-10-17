package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.common.OnCreate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class CommentDto {

    long id;

    @NotBlank(groups = OnCreate.class)
    String text;

    String authorName;
    LocalDateTime created;
}