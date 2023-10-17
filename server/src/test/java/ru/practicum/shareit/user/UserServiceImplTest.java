package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.EntityGetter;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

class UserServiceImplTest {

    private UserService userService;
    private UserRepository userRepository;
    private EntityGetter entityGetter;
    private User user;
    private UserDto userDto;

    @BeforeEach
    void init() {
        userRepository = mock(UserRepository.class);
        entityGetter = mock(EntityGetter.class);
        userService = new UserServiceImpl(userRepository, entityGetter);
        userDto = new UserDto(1L, "user", "user@ya.ru");
        user = new User(1L, "user", "user@ya.ru");
    }

    @Test
    void should_create_user() {
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserDto returnedUserDto = userService.create(userDto);
        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());
    }

    @Test
    void should_update_user() {
        userDto.setName("updatedName");
        userDto.setEmail("updatedEmail@ya.ru");
        when(entityGetter.getUser(anyLong())).thenReturn(user);
        UserDto returnedUserDto = userService.update(userDto);
        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());
    }

    @Test
    void should_update_user_without_updated_data() {
        userDto.setName(null);
        userDto.setEmail(null);
        when(entityGetter.getUser(anyLong())).thenReturn(user);
        UserDto returnedUserDto = userService.update(userDto);
        assertEquals(toUserDto(user).getId(), returnedUserDto.getId());
        assertEquals(toUserDto(user).getName(), returnedUserDto.getName());
        assertEquals(toUserDto(user).getEmail(), returnedUserDto.getEmail());
    }

    @Test
    void should_not_update_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.update(userDto));
    }

    @Test
    void should_get_all_users() {
        List<User> result = List.of(user);
        when(userRepository.findAll()).thenReturn(result);
        List<UserDto> returnedResult = userService.getAll();
        assertEquals(1, returnedResult.size());
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void should_get_user_by_id() {
        when(entityGetter.getUser(anyLong())).thenReturn(user);
        UserDto returnedUserDto = userService.get(user.getId());
        assertEquals(userDto.getId(), returnedUserDto.getId());
        assertEquals(userDto.getName(), returnedUserDto.getName());
        assertEquals(userDto.getEmail(), returnedUserDto.getEmail());
    }

    @Test
    void should_not_get_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.get(user.getId()));
    }

    @Test
    void should_delete_user_by_id() {
        when(entityGetter.getUser(anyLong())).thenReturn(user);
        userService.delete(user.getId());
        verify(userRepository, times(1)).delete(user);
    }

    @Test
    void should_not_delete_not_existing_user() {
        when(entityGetter.getUser(anyLong())).thenThrow(NotFoundException.class);
        assertThrows(NotFoundException.class, () -> userService.delete(user.getId()));
        verify(userRepository, times(0)).delete(user);
    }
}