package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.OnCreate;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    long id;

    @NotBlank(groups = OnCreate.class)
    String text;

    String authorName;
    LocalDateTime created;
}