package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.UserMapper.toUser;
import static ru.practicum.shareit.user.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto create(UserDto userDto) {
        User user = userRepository.create(toUser(userDto));
        return toUserDto(user);
    }

    @Override
    public UserDto update(UserDto userDto) {
        User user = userRepository.update(toUser(userDto))
                .orElseThrow(() -> new NotFoundException("User with ID: '" + userDto.getId() + "' doesn't exist"));
        return toUserDto(user);
    }

    @Override
    public List<UserDto> getAll() {
        return userRepository.getAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto get(Long id) {
        User user = userRepository.get(id)
                .orElseThrow(() -> new NotFoundException("User with ID: '" + id + "' doesn't exist"));
        return toUserDto(user);
    }

    @Override
    public void delete(Long id) {
        if (userRepository.isUserExist(id)) {
            userRepository.delete(id);
        } else {
            throw new NotFoundException("User with ID: '" + id + "' doesn't exist");
        }
    }
}