package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ItemDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}