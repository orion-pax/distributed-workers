package com.assignment.proxify.distributedworker.repository;

import com.assignment.proxify.distributedworker.enums.Status;
import com.assignment.proxify.distributedworker.model.Job;
import org.springframework.data.repository.CrudRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository extends CrudRepository<Job, Long> {
    List<Job> findAllByStatus(Status status);
    Optional<Job> findByUrl(String url);
    Optional<Job> findOneByStatus (Status status);
}
