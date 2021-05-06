package ru.job4j.dream.store;

import ru.job4j.dream.model.Candidate;

import java.util.Collection;

public interface CandidateStore {
    Collection<Candidate> findAllCandidates();
    void save(Candidate candidate);
    Candidate findCandidateById(int id);
    void delete(int id);
}
