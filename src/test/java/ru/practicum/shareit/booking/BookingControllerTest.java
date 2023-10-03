package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingRequestDto;
import ru.practicum.shareit.booking.dto.BookingResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BookingService bookingService;
    @Autowired
    MockMvc mvc;
    BookingRequestDto bookingRequestDto;
    BookingResponseDto bookingResponseDto;
    LocalDateTime start;
    LocalDateTime end;

    @BeforeEach
    void init() {
        start = LocalDateTime.now().plusHours(1);
        end = LocalDateTime.now().plusHours(11);
        bookingRequestDto = new BookingRequestDto(1L, start, end);
        bookingResponseDto = new BookingResponseDto(1L, start, end, Status.WAITING,
                new UserDto(1L, "booker", "booker@ya.ru"),
                new ItemDto(1L, "Дрель", "Обычная дрель", true, 1L));
    }

    @Test
    void should_create_booking() throws Exception {
        when(bookingService.create(anyLong(), any(BookingRequestDto.class))).thenReturn(bookingResponseDto);
        mvc.perform(post("/bookings")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getEmail()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.item.description", is(bookingResponseDto.getItem().getDescription()), String.class))
                .andExpect(jsonPath("$.item.available", is(bookingResponseDto.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingResponseDto.getItem().getRequestId()), Long.class));
        verify(bookingService, times(1)).create(anyLong(), any(BookingRequestDto.class));
    }

    @Test
    void should_update_status_of_booking() throws Exception {
        when(bookingService.updateStatus(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingResponseDto);
        mvc.perform(patch("/bookings/1")
                        .header(USER_HEADER, 1)
                        .param("approved", "false")
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getEmail()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.item.description", is(bookingResponseDto.getItem().getDescription()), String.class))
                .andExpect(jsonPath("$.item.available", is(bookingResponseDto.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingResponseDto.getItem().getRequestId()), Long.class));
        verify(bookingService, times(1)).updateStatus(anyLong(), anyLong(), anyBoolean());
    }

    @Test
    void should_get_booking_by_id() throws Exception {
        when(bookingService.get(anyLong(), anyLong())).thenReturn(bookingResponseDto);
        mvc.perform(get("/bookings/1")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(bookingRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingResponseDto.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(start.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.end", is(end.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class))
                .andExpect(jsonPath("$.status", is(bookingResponseDto.getStatus().toString()), String.class))
                .andExpect(jsonPath("$.booker.id", is(bookingResponseDto.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.booker.name", is(bookingResponseDto.getBooker().getName()), String.class))
                .andExpect(jsonPath("$.booker.email", is(bookingResponseDto.getBooker().getEmail()), String.class))
                .andExpect(jsonPath("$.item.id", is(bookingResponseDto.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(bookingResponseDto.getItem().getName()), String.class))
                .andExpect(jsonPath("$.item.description", is(bookingResponseDto.getItem().getDescription()), String.class))
                .andExpect(jsonPath("$.item.available", is(bookingResponseDto.getItem().getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.item.requestId", is(bookingResponseDto.getItem().getRequestId()), Long.class));
        verify(bookingService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void should_get_by_booker_id() throws Exception {
        List<BookingResponseDto> result = List.of(bookingResponseDto);
        when(bookingService.getByBookerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(result);
        mvc.perform(get("/bookings")
                        .header(USER_HEADER, 1)
                        .param("state", "past")
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1)).getByBookerId(anyLong(), anyString(), anyInt(), anyInt());
    }

    @Test
    void should_get_by_owner_id() throws Exception {
        List<BookingResponseDto> result = List.of(bookingResponseDto);
        when(bookingService.getByOwnerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(result);
        mvc.perform(get("/bookings/owner")
                        .header(USER_HEADER, 1)
                        .param("state", "past")
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(bookingService, times(1)).getByOwnerId(anyLong(), anyString(), anyInt(), anyInt());
    }
}