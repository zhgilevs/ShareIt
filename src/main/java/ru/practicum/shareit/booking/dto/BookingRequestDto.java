package ru.practicum.shareit.booking.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.utils.OnCreate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingRequestDto {

    @NotNull(groups = OnCreate.class)
    private long itemId;

    @NotNull(groups = OnCreate.class)
    @FutureOrPresent(groups = OnCreate.class)
    private LocalDateTime start;

    @NotNull(groups = OnCreate.class)
    @Future(groups = OnCreate.class)
    private LocalDateTime end;
}