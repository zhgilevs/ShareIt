package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingInfoDto;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInfoDto;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
class ItemControllerTest {

    private static final String USER_HEADER = "X-Sharer-User-Id";
    @Autowired
    ObjectMapper mapper;
    @MockBean
    ItemService itemService;
    @Autowired
    MockMvc mvc;
    ItemDto itemDto;
    ItemInfoDto itemInfoDto;

    @BeforeEach
    void init() {
        itemDto = new ItemDto(1L, "Дрель", "Обычная дрель", true, null);
        itemInfoDto = new ItemInfoDto(1L, "Дрель", "Обычная дрель", true,
                new BookingInfoDto(1L, 1L),
                new BookingInfoDto(2L, 2L),
                new ArrayList<>());
    }

    @Test
    void should_create_item() throws Exception {
        when(itemService.create(anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        mvc.perform(post("/items")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        verify(itemService, times(1)).create(anyLong(), any(ItemDto.class));
    }

    @Test
    void should_update_item() throws Exception {
        when(itemService.update(anyLong(), anyLong(), any(ItemDto.class))).thenReturn(itemDto);
        mvc.perform(patch("/items/1")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable()), Boolean.class));
        verify(itemService, times(1)).update(anyLong(), anyLong(), any(ItemDto.class));
    }

    @Test
    void should_get_all_items() throws Exception {
        List<ItemInfoDto> result = List.of(itemInfoDto);
        when(itemService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(result);
        mvc.perform(get("/items")
                        .header(USER_HEADER, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(itemService, times(1)).getAll(anyLong(), anyInt(), anyInt());
    }

    @Test
    void should_get_item_by_id() throws Exception {
        when(itemService.get(anyLong(), anyLong())).thenReturn(itemInfoDto);
        mvc.perform(get("/items/1")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(itemInfoDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemInfoDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemInfoDto.getName()), String.class))
                .andExpect(jsonPath("$.description", is(itemInfoDto.getDescription()), String.class))
                .andExpect(jsonPath("$.available", is(itemInfoDto.getAvailable()), Boolean.class))
                .andExpect(jsonPath("$.lastBooking.id", is(itemInfoDto.getLastBooking().getId()), Long.class))
                .andExpect(jsonPath("$.lastBooking.bookerId", is(itemInfoDto.getLastBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.id", is(itemInfoDto.getNextBooking().getId()), Long.class))
                .andExpect(jsonPath("$.nextBooking.bookerId", is(itemInfoDto.getNextBooking().getBookerId()), Long.class))
                .andExpect(jsonPath("$.comments", hasSize(0)));
        verify(itemService, times(1)).get(anyLong(), anyLong());
    }

    @Test
    void should_search_item_by_text() throws Exception {
        List<ItemDto> result = List.of(itemDto);
        when(itemService.search(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(result);
        mvc.perform(get("/items/search")
                        .header(USER_HEADER, 1)
                        .param("from", "0")
                        .param("size", "10")
                        .param("text", "Дрель")
                        .content(mapper.writeValueAsString(result))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
    }

    @Test
    void should_create_comment_for_item() throws Exception {
        LocalDateTime now = LocalDateTime.now();
        CommentDto commentDto = new CommentDto(1L, "Хорошая дрель", "user", now);
        when(itemService.createComment(anyLong(), anyLong(), any(CommentDto.class))).thenReturn(commentDto);
        mvc.perform(post("/items/1/comment")
                        .header(USER_HEADER, 1)
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText()), String.class))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName()), String.class));
    }
}