package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
class ItemRequestControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    MockMvc mvc;
    ItemRequestDto itemRequestDto;
    LocalDateTime created;

    @BeforeEach
    void init() {
        created = LocalDateTime.now();
        itemRequestDto = new ItemRequestDto(1L, "Нужна дрель", created, new ArrayList<>());
    }

    @Test
    void should_create_item_request() throws Exception {
        when(itemRequestService.create(anyLong(), any(ItemRequestDto.class))).thenReturn(itemRequestDto);
        mvc.perform(post("/requests")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));
        verify(itemRequestService, times(1)).create(anyLong(), any(ItemRequestDto.class));
    }

    @Test
    void should_get_all_by_user_id() throws Exception {
        List<ItemRequestDto> result = List.of(itemRequestDto);
        when(itemRequestService.getAllByUserId(anyLong())).thenReturn(result);
        mvc.perform(get("/requests")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemRequestService, times(1)).getAllByUserId(anyLong());
    }

    @Test
    void should_get_by_request_id() throws Exception {
        when(itemRequestService.get(anyLong(), anyLong())).thenReturn(itemRequestDto);
        mvc.perform(get("/requests/1")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription()), String.class))
                .andExpect(jsonPath("$.created", is(created.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)), String.class));
        verify(itemRequestService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void should_get_all_requests() throws Exception {
        List<ItemRequestDto> result = List.of(itemRequestDto);
        when(itemRequestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(result);
        mvc.perform(get("/requests/all")
                        .header(USER_HEADER, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }
}