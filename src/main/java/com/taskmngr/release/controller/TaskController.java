package com.taskmngr.release.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taskmngr.release.model.Task;
import com.taskmngr.release.model.User;
import com.taskmngr.release.repository.TaskRepository;
import com.taskmngr.release.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    // Dodajemy testowych użytkowników przy starcie apki
    @PostConstruct
    public void initData() {
        if (userRepository.count() == 0) {
            userRepository.save(new User("Jan Kowalski"));
            userRepository.save(new User("Anna Nowak"));
        }
    }

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("tasks", taskRepository.findAll());
        model.addAttribute("users", userRepository.findAll());
        return "index";
    }

  
    @PostMapping("/add")
    public String addTask(@RequestParam String description, @RequestParam(required = false) Long userId) {
        Task newTask = new Task(description);
        

        if (userId != null) {
            userRepository.findById(userId).ifPresent(user -> newTask.setAssignedUser(user));
        }
        
        taskRepository.save(newTask);
        return "redirect:/";
    }

    @PostMapping("/toggle/{id}")
    public String toggleTask(@PathVariable Long id) {
        Task task = taskRepository.findById(id).orElseThrow();
        task.setCompleted(!task.isCompleted());
        taskRepository.save(task);
        return "redirect:/";
    }

    @PostMapping("/delete/{id}")
    public String deleteTask(@PathVariable Long id) {
        taskRepository.deleteById(id);
        return "redirect:/";
    }
}