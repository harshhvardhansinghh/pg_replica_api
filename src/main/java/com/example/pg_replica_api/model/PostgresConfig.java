package com.example.pg_replica_api.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;


public class PostgresConfig {


    @NotNull
    private String postgresVersion;

    @NotNull
    private String instanceType;

    @NotNull
    private int numberOfReplicas;

    @NotNull
    private int maxConnections;

    @NotNull
    private String sharedBuffers;

    // Getters and Setters
    public String getPostgresVersion() {
        return postgresVersion;
    }

    public void setPostgresVersion(String postgresVersion) {
        this.postgresVersion = postgresVersion;
    }

    public String getInstanceType() {
        return instanceType;
    }

    public void setInstanceType(String instanceType) {
        this.instanceType = instanceType;
    }

    public int getNumberOfReplicas() {
        return numberOfReplicas;
    }

    public void setNumberOfReplicas(int numberOfReplicas) {
        this.numberOfReplicas = numberOfReplicas;
    }

    public int getMaxConnections() {
        return maxConnections;
    }

    public void setMaxConnections(int maxConnections) {
        this.maxConnections = maxConnections;
    }

    public String getSharedBuffers() {
        return sharedBuffers;
    }

    public void setSharedBuffers(String sharedBuffers) {
        this.sharedBuffers = sharedBuffers;
    }
}
