package ru.job4j.dream.store;

public class PsqlMain {
    public static void main(String[] args) {
        PostStore store = PsqlPostStore.instOf();
        CandidateStore store1 = PsqlCandidateStore.instOf();
        PhotoStore store3 = PsqlPhotoStore.instOf();
        store3.add("djgfkd");
    }
}