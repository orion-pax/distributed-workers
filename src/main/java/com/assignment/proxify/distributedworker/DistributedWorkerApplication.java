package com.assignment.proxify.distributedworker;

import com.assignment.proxify.distributedworker.enums.Status;
import com.assignment.proxify.distributedworker.model.Job;
import com.assignment.proxify.distributedworker.service.IWorker;
import com.assignment.proxify.distributedworker.service.JobService;
import com.assignment.proxify.distributedworker.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@SpringBootApplication
public class DistributedWorkerApplication implements CommandLineRunner {

    private static final Logger LOGGER = Logger.getLogger(DistributedWorkerApplication.class.getName());

    private JobService jobService;
    private RestTemplate restTemplate;
    private IWorker worker;

    public DistributedWorkerApplication(JobService jobService, RestTemplate restTemplate, IWorker worker) {
        this.jobService = jobService;
        this.restTemplate = restTemplate;
        this.worker = worker;
    }

    public static void main(String[] args) {
        SpringApplication.run(DistributedWorkerApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        populateJobTable();
        executeJobs();
    }

    private void populateJobTable() {
        List<String> initialUrls = new ArrayList<String>() {
            {
                add("https://proxify.io");
                add("https://reddit.com");
                add("httpstat.us/200?sleep=5000");
                add("https:/http.error");
            }
        };
        IntStream.range(0, 4).forEach(value ->
        {
            String url = getRandomUrl();
            initialUrls.add(url);
        });
        initialUrls.forEach(url -> {
            Job job = new Job(url, Status.NEW);
            jobService.save(job);
        });
    }

    private void executeJobs() {
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        IntStream.range(0, 5).forEach(value -> executorService.execute(worker));
        executorService.shutdown();
        while (!executorService.isTerminated()) ;
        LOGGER.log(Level.INFO,":: Workers have finished executing requests");
    }

    private String getRandomUrl() {
        String baseUrl = "https://httpstat.us/";
        int min = 1;
        List<Integer> statusCodes = Arrays.stream(HttpStatus.values()).map(HttpStatus::value).collect(Collectors.toList());
        int max = statusCodes.size()-1;
        int range = max - min + 1;
        int randomNumber = (int) (Math.random() * range) + min;
        return baseUrl + statusCodes.get(randomNumber);
    }


}
