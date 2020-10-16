package com.assignment.proxify.distributedworker.worker;

import com.assignment.proxify.distributedworker.enums.Status;
import com.assignment.proxify.distributedworker.model.Job;
import com.assignment.proxify.distributedworker.service.JobService;
import org.springframework.http.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.client.UnknownHttpStatusCodeException;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Worker implements Runnable {
    private static final Logger LOGGER = Logger.getLogger(Worker.class.getName());
    private JobService jobService;
    private RestTemplate restTemplate;

    public Worker(JobService jobService, RestTemplate restTemplate) {
        this.jobService = jobService;
        this.restTemplate = restTemplate;
    }

    @Override
    @Transactional
    public void run() {
        try {
            Optional<Job> optionalJob = getNextAvailableJob();
            while (optionalJob.isPresent()) {
                Job job = optionalJob.get();
                try {
                    job.setStatus(Status.PROCESSING);
                    job = jobService.update(job).get();
                    HttpStatus responseStatus = callJobUrl(job.getUrl());
                    job.setHttp_code(responseStatus != null ? String.valueOf(responseStatus.value()) : null);
                    job.setStatus((responseStatus != null && responseStatus.equals(HttpStatus.OK)) ? Status.DONE : Status.ERROR);
                    jobService.update(job);
                    LOGGER.log(Level.INFO, String.format("Job %d completed with status %s", job.getId(), job.getStatus()));
                } catch (Exception e) {
                    LOGGER.log(Level.WARNING, String.format("Job %d is being processed by another worker.", job.getId()));
                } finally {
                    optionalJob = getNextAvailableJob();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private HttpStatus callJobUrl(String jobUrl) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/54.0.2840.99 Safari/537.36");
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        HttpStatus responseStatus;

        try {
            ResponseEntity<String> response = restTemplate.exchange(jobUrl, HttpMethod.GET, entity, String.class);
            responseStatus = response.getStatusCode();

        } catch (HttpClientErrorException e) {
            LOGGER.log(Level.SEVERE, jobUrl + ":: " + e.getMessage());
            e.printStackTrace();
            responseStatus = e.getStatusCode();
        } catch(UnknownHttpStatusCodeException e){
            LOGGER.log(Level.SEVERE, jobUrl + ":: " + e.getMessage());
            e.printStackTrace();
            responseStatus =  HttpStatus.valueOf(e.getRawStatusCode());
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, jobUrl + ":: " + e.getMessage());
            e.printStackTrace();
            responseStatus = null;
        }
        return responseStatus;
    }

    private Optional<Job> getNextAvailableJob() {
        List<Job> jobs = jobService.findAllByStatus(Status.NEW);
        return !jobs.isEmpty() ? Optional.of(jobs.get(0)) : Optional.empty();
    }


}
