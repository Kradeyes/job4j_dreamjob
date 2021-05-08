package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.job4j.dream.model.User;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Properties;

public class PsqlUserStore implements UserStore {
    private final BasicDataSource pool = new BasicDataSource();
    private static final Logger LOG = LoggerFactory.getLogger(PsqlUserStore.class.getName());

    private PsqlUserStore() {
        Properties cfg = new Properties();
        try (BufferedReader io = new BufferedReader(
                new FileReader("db.properties")
        )) {
            cfg.load(io);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        try {
            Class.forName(cfg.getProperty("jdbc.driver"));
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
        pool.setDriverClassName(cfg.getProperty("jdbc.driver"));
        pool.setUrl(cfg.getProperty("jdbc.url"));
        pool.setUsername(cfg.getProperty("jdbc.username"));
        pool.setPassword(cfg.getProperty("jdbc.password"));
        pool.setMinIdle(5);
        pool.setMaxIdle(10);
        pool.setMaxOpenPreparedStatements(100);
    }

    private static final class Lazy {
        private static final UserStore INST = new PsqlUserStore();
    }

    public static UserStore instOf() {
        return PsqlUserStore.Lazy.INST;
    }

    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            create(user);
        } else {
            update(user);
        }
    }

    private User create(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("INSERT INTO users(name, email, password) VALUES (?,?,?);",
                     PreparedStatement.RETURN_GENERATED_KEYS)
        ) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();
            try (ResultSet id = ps.getGeneratedKeys()) {
                if (id.next()) {
                    user.setId(id.getInt(1));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return user;
    }

    private void update(User user) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("update users set name = (?), email = (?), password where id = (?);")) {
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.execute();

        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public User findUserById(int id) {
        User user = new User();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from users where id = (?);")) {
            ps.setInt(1, id);
            ps.execute();
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public Collection<User> findAllUsers() {
        List<User> users = new ArrayList<>();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("SELECT * FROM users;")
        ) {
            try (ResultSet it = ps.executeQuery()) {
                while (it.next()) {
                    users.add(new User(it.getInt("id"), it.getString("name"),
                            it.getString("email"), it.getString("password")));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return users;
    }

    @Override
    public User findUserByEmail(String email) {
        User user = new User();
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select * from users where email = (?);")) {
            ps.setString(1, email);
            ps.execute();
            try (ResultSet resultSet = ps.executeQuery()) {
                if (resultSet.next()) {
                    user = new User(resultSet.getInt(1), resultSet.getString(2),
                            resultSet.getString(3), resultSet.getString(4));
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return user;
    }

    @Override
    public void delete(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("DELETE FROM users WHERE id=(?);")
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }
}
