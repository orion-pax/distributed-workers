package com.assignment.proxify.distributedworker.service;

import com.assignment.proxify.distributedworker.enums.Status;
import com.assignment.proxify.distributedworker.model.Job;

import java.util.List;
import java.util.Optional;

public interface JobService {
    Optional<Job> save(Job job);
    Optional<Job> update(Job job);
    List<Job> findAllByStatus(Status status);
}
