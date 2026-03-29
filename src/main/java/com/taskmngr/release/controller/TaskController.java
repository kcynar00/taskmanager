package com.taskmngr.release.controller;

import com.taskmngr.release.model.Task;
import com.taskmngr.release.model.User;
import com.taskmngr.release.repository.TaskRepository;
import com.taskmngr.release.repository.UserRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.List;

@Controller
public class TaskController {

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;

    public TaskController(TaskRepository taskRepository, UserRepository userRepository) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index(Model model, Principal principal) {
        // Jeśli ktoś nie jest zalogowany (choć Spring Security powinno tego pilnować), uciekamy
        if (principal == null) return "redirect:/login";

        // Pobieramy aktualnie zalogowanego użytkownika
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        
        List<Task> tasks;
        
        // LOGIKA RÓL - co kto widzi na liście zadań
        if ("ADMIN".equals(currentUser.getRole())) {
            tasks = taskRepository.findAll(); // Admin widzi wszystko
            model.addAttribute("users", userRepository.findAll()); // Admin może przypisać każdemu
        } else if ("MANAGER".equals(currentUser.getRole())) {
            tasks = taskRepository.findByAssignedUser_Manager(currentUser); // Menedżer widzi zadania swoich ludzi
            model.addAttribute("users", userRepository.findByManager(currentUser)); // Przypisuje tylko swoim
        } else {
            tasks = taskRepository.findByAssignedUser(currentUser); // Zwykły user widzi tylko swoje
      
        }

        model.addAttribute("tasks", tasks);
        model.addAttribute("currentUser", currentUser); // Przekazujemy usera do HTML, by móc ukrywać elementy
        
        return "index";
    }

    @PostMapping("/add")
    public String addTask(@RequestParam String description, @RequestParam(required = false) Long userId, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        
        Task newTask = new Task(description);
        newTask.setCreator(currentUser); // Zapisujemy w bazie, kto jest autorem!

        if ("USER".equals(currentUser.getRole())) {
            // Zwykły pracownik zawsze przypisuje zadanie samemu sobie
            newTask.setAssignedUser(currentUser);
        } else {
            // Admin/Manager może wybrać pracownika z listy
            if (userId != null) {
                userRepository.findById(userId).ifPresent(newTask::setAssignedUser);
            } else {
                newTask.setAssignedUser(currentUser); // Jeśli nie wybrał, przypisuje sobie
            }
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
    public String deleteTask(@PathVariable Long id, Principal principal) {
        User currentUser = userRepository.findByUsername(principal.getName()).orElseThrow();
        Task task = taskRepository.findById(id).orElseThrow();
        
        // Możesz usunąć zadanie, jeśli jesteś Adminem/Menedżerem ALBO jeśli sam je stworzyłeś
        boolean isAdminOrManager = "ADMIN".equals(currentUser.getRole()) || "MANAGER".equals(currentUser.getRole());
        boolean isMyOwnTask = task.getCreator() != null && task.getCreator().getId().equals(currentUser.getId());

        if (isAdminOrManager || isMyOwnTask) {
            taskRepository.deleteById(id);
        }

        return "redirect:/";
    }
}