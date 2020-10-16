package com.assignment.proxify.distributedworker.service;

import com.assignment.proxify.distributedworker.enums.Status;
import com.assignment.proxify.distributedworker.model.Job;
import com.assignment.proxify.distributedworker.repository.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class JobServiceImpl implements JobService {

    @Autowired
    private TaskRepository taskRepository;

    @Override
    @Transactional
    public Optional<Job> save(Job job) {
        Optional<Job> currentTask = taskRepository.findByUrl(job.getUrl());
        if(!currentTask.isPresent()){
            return Optional.ofNullable(taskRepository.save(job));
        }else{
           return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Job> update(Job job) {
        Optional<Job> currentTask = taskRepository.findById(job.getId());
        if(currentTask.isPresent()){
            currentTask.get().setHttp_code(job.getHttp_code());
            currentTask.get().setStatus(job.getStatus());
            return Optional.ofNullable(taskRepository.save(currentTask.get()));
        }else{
            return Optional.empty();
        }
    }

    @Override
    public List<Job> findAllByStatus(Status status) {
        return taskRepository.findAllByStatus(status);
    }

}
