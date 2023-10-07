package ru.practicum.shareit.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class BookingResponseDto {

    private long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Status status;
    private UserDto booker;
    private ItemDto item;
}