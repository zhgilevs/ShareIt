package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BookingInfoDto {
    private long id;
    private long bookerId;
}