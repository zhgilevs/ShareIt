package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.user.dto.UserDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
class UserControllerTest {

    @Autowired
    ObjectMapper mapper;
    @MockBean
    UserService userService;
    @Autowired
    MockMvc mvc;
    UserDto userDto;

    @BeforeEach
    void init() {
        userDto = new UserDto(1L, "user", "user@ya.ru");
    }

    @Test
    void should_create_user() throws Exception {
        when(userService.create(any(UserDto.class))).thenReturn(userDto);
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
        verify(userService, times(1)).create(any(UserDto.class));
    }

    @Test
    void should_not_create_user_with_empty_name() throws Exception {
        UserDto userDtoWithEmptyName = new UserDto(1L, "", "user@ya.ru");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoWithEmptyName))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserDto.class));
    }

    @Test
    void should_not_create_user_with_wrong_email() throws Exception {
        UserDto userDtoWithWrongEmail = new UserDto(1L, "user", "user?user.name");
        mvc.perform(post("/users")
                        .content(mapper.writeValueAsString(userDtoWithWrongEmail))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
        verify(userService, times(0)).create(any(UserDto.class));
    }

    @Test
    void should_update_user() throws Exception {
        UserDto updatedUser = new UserDto(1L, "updatedName", "update@ya.ru");
        when(userService.update(userDto)).thenReturn(updatedUser);
        mvc.perform(patch("/users/1")
                        .content(mapper.writeValueAsString(userDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(updatedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(updatedUser.getName()), String.class))
                .andExpect(jsonPath("$.email", is(updatedUser.getEmail()), String.class));
        verify(userService, times(1)).update(any(UserDto.class));
    }

    @Test
    void should_get_all_of_users() throws Exception {
        when(userService.getAll()).thenReturn(List.of(userDto));
        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)));
        verify(userService, times(1)).getAll();
    }

    @Test
    void should_get_user() throws Exception {
        when(userService.get(anyLong())).thenReturn(userDto);
        mvc.perform(get("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(userDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(userDto.getName()), String.class))
                .andExpect(jsonPath("$.email", is(userDto.getEmail()), String.class));
        verify(userService, times(1)).get(anyLong());
    }

    @Test
    void should_delete_user() throws Exception {
        mvc.perform(delete("/users/" + userDto.getId())
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        verify(userService, times(1)).delete(anyLong());
    }
}