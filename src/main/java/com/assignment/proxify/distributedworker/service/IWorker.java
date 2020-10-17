package com.assignment.proxify.distributedworker.service;

import com.assignment.proxify.distributedworker.model.Job;
import org.springframework.http.HttpStatus;

import java.util.Optional;

public interface IWorker extends Runnable {
    Optional<Job> getNextAvailableJob();
    HttpStatus callJobUrl(String jobUrl);
}
