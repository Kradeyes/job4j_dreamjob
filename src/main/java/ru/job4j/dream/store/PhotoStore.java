package ru.job4j.dream.store;

public interface PhotoStore {
    int add(String name);
    void delete(int id);
    void update(int id, String newName);
    String get(int id);
}
