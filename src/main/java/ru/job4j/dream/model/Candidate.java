package ru.job4j.dream.model;

import java.util.Objects;

public class Candidate {
    private int id;
    private String name;
    private int photoId;

    public Candidate(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Candidate(int id, String name, int photoId) {
        this.id = id;
        this.name = name;
        this.photoId = photoId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPhotoId() {
        return photoId;
    }

    public Candidate setId(int id) {
        return new Candidate(id, name, photoId);
    }

    public Candidate setName(String name) {
        return new Candidate(id, name, photoId);
    }

    public Candidate setPhotoId(int photoId) {
        return new Candidate(id, name, photoId);
    }

    public static Candidate of(Candidate c) {
        return new Candidate(c.id, c.name, c.photoId);
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Candidate candidate = (Candidate) o;
        return getId() == candidate.getId() && getPhotoId() == candidate.getPhotoId() && Objects.equals(getName(), candidate.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getName(), getPhotoId());
    }
}