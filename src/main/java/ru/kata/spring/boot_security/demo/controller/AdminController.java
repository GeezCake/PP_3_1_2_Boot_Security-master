package ru.kata.spring.boot_security.demo.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import ru.kata.spring.boot_security.demo.model.User;
import ru.kata.spring.boot_security.demo.service.UserService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public String allUsers(Model model) {
        List<User> users = userService.getAll();
        model.addAttribute("users", users);
        return "admin/users";
    }

    @GetMapping("/add")
    public String addUserForm(Model model) {
        model.addAttribute("user", new User());
        return "admin/user-form";
    }

    @PostMapping("/save")
    public String createUser(@ModelAttribute("user") User user) {
        userService.create(user);
        return "redirect:/admin";
    }

    @GetMapping("/edit")
    public String editUser(@RequestParam("id") Long id, Model model) {
        model.addAttribute("user", userService.getById(id));
        return "admin/user-form";
    }

    @PostMapping("/update")
    public String updateUser(@ModelAttribute("user") User user) {
        userService.update(user);
        return "redirect:/admin";
    }

    @PostMapping("/delete")
    public String deleteUser(@RequestParam("id") Long id) {
        userService.delete(id);
        return "redirect:/admin";
    }
}


