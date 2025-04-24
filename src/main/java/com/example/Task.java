package com.example;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    int id;
    String status;
    String description;

    public Task(String description, int id){
        this.description = description;
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
        this.id = id;
        status = "todo";
    }

}
