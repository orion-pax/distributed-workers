package com.assignment.proxify.distributedworker.model;

import com.assignment.proxify.distributedworker.enums.Status;
import javax.persistence.*;

@Entity
@Table(name = "jobs")
public class Job {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="url", nullable = false, unique = true)
    private String url;

    @Enumerated(EnumType.STRING)
    @Column(name="status", nullable = false)
    private Status status = Status.NEW;

    @Column(name="http_code")
    private String http_code;

    @Version
    private Long version;


    public Job(Long id, String url, Status status, String http_code) {
        this.id = id;
        this.url = url;
        this.status = status;
        this.http_code = http_code;
    }

    public Job(String url, Status status) {
        this.url = url;
        this.status = status;
    }

    public Job() {
    }

    public Long getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public String getHttp_code() {
        return http_code;
    }

    public void setHttp_code(String http_code) {
        this.http_code = http_code;
    }

    @Override
    public String toString() {
        return "Job{" +
                "id=" + id +
                ", url='" + url + '\'' +
                ", status=" + status +
                ", http_code='" + http_code + '\'' +
                ", version=" + version +
                '}';
    }
}
