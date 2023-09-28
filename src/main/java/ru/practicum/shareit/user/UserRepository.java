package ru.practicum.shareit.user;

import java.util.List;
import java.util.Optional;

public interface UserRepository {

    User create(User user);

    Optional<User> update(User user);

    List<User> getAll();

    Optional<User> get(Long id);

    void delete(Long id);

    boolean isUserExist(Long id);
}