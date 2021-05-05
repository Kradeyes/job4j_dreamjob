package ru.job4j.dream.store;

import ru.job4j.dream.model.Post;

import java.util.Collection;

public interface PostStore {
    Collection<Post> findAllPosts();
    void save(Post post);
    Post findPostById(int id);
}
