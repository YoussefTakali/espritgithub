package com.example.esprit.service;

import com.example.esprit.model.Task;
import com.example.esprit.repository.TaskRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;

    public Task createTask(Task task) {
        task.setCreatedDate(LocalDateTime.now());
        return taskRepository.save(task);
    }
}
