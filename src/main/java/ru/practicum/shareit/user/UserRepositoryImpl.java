package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.EmailDuplicateException;

import java.util.*;

@Component
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private Long id = 0L;
    private final Map<Long, User> users = new HashMap<>();

    public User create(User user) {
        checkUserEmail(user);
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("User with ID: '" + user.getId() + "' successfully created");
        return user;
    }

    public Optional<User> update(User user) {
        User u = users.get(user.getId());
        if (user.getEmail() != null) {
            checkUserEmail(user);
            u.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            u.setName(user.getName());
        }
        log.info("User with ID: '" + u.getId() + "' successfully updated");
        return Optional.of(u);
    }

    @Override
    public List<User> getAll() {
        log.info("All users successfully received");
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> get(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public void delete(Long id) {
        log.info("User with ID: '" + id + "' successfully removed");
        users.remove(id);
    }

    @Override
    public boolean isUserExist(Long id) {
        return users.containsKey(id);
    }

    private void checkUserEmail(User user) {
        String email = user.getEmail();
        for (User u : users.values()) {
            if (u.getEmail().equals(email) && !Objects.equals(u.getId(), user.getId())) {
                log.error("Email: '" + u.getEmail() + "' already exists");
                throw new EmailDuplicateException("Email already exists");
            }
        }
    }

    private Long generateId() {
        return ++id;
    }
}