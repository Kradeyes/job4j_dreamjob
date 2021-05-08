package ru.job4j.dream.store;

import ru.job4j.dream.model.User;
import java.util.Collection;

public interface UserStore {
    Collection<User> findAllUsers();
    void save(User user);
    User findUserById(int id);
    User findUserByEmail(String email);
    void delete(int id);
}
