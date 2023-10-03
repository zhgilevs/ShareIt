package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.utils.EntityGetter;

import java.util.List;
import java.util.stream.Collectors;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final EntityGetter entityGetter;

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userRepository.save(toUser(userDto));
        log.info("User with ID: '" + user.getId() + "' successfully created");
        return toUserDto(user);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public UserDto update(UserDto userDto) {
        Long userId = userDto.getId();
        User user = entityGetter.getUser(userId);
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        log.info("User with ID: '" + userId + "' successfully updated");
        return toUserDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        List<UserDto> result = userRepository.findAll().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
        log.info("List of users successfully received");
        return result;
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class, readOnly = true)
    public UserDto get(Long id) {
        User user = entityGetter.getUser(id);
        log.info("User with ID: '" + id + "' successfully received");
        return toUserDto(user);
    }

    @Override
    @Transactional(rollbackFor = NotFoundException.class)
    public void delete(Long id) {
        User user = entityGetter.getUser(id);
        userRepository.delete(user);
        log.info("User with ID: '" + id + "' successfully removed");
    }
}