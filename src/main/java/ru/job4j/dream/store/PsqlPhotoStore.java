package ru.job4j.dream.store;

import org.apache.commons.dbcp2.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

public class PsqlPhotoStore implements PhotoStore {
    private final BasicDataSource pool = new BasicDataSource();
    private static final Logger LOG = LoggerFactory.getLogger(PsqlPostStore.class.getName());

    private PsqlPhotoStore() {
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
        private static final PhotoStore INST = new PsqlPhotoStore();
    }

    public static PhotoStore instOf() {
        return PsqlPhotoStore.Lazy.INST;
    }

    @Override
    public int add(String name) {
        int result = -1;
        try {
            try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement("insert into photo(name) values(?)",
                         PreparedStatement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, name);
                ps.execute();
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) {
                        result = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }

    @Override
    public void delete(int id) {
        try (Connection cn = pool.getConnection();
             PreparedStatement ps =  cn.prepareStatement("DELETE FROM photo WHERE id=(?)")
        ) {
            ps.setInt(1, id);
            ps.execute();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public void update(int id, String newName) {
        try (Connection cn = pool.getConnection();
                 PreparedStatement ps = cn.prepareStatement("update photo set name= (?) where id= (?)")) {
                ps.setString(1, newName);
                ps.setInt(2, id);
                ps.execute();
            } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
    }

    @Override
    public String get(int id) {
        String result = "";
        try (Connection cn = pool.getConnection();
             PreparedStatement ps = cn.prepareStatement("select name from photo where id= (?)")) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result = rs.getString(1);
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return result;
    }
}

