package ru.itmentor.spring.boot_security.demo.controller;

import javax.validation.Valid;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import ru.itmentor.spring.boot_security.demo.model.Customer;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.service.CustomerServiceImpl;
import ru.itmentor.spring.boot_security.demo.service.RoleServiceImpl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Controller
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private CustomerServiceImpl customerServiceImpl;
    @Autowired
    private RoleServiceImpl roleServiceImpl;


    @GetMapping("/user")
    public String getCustomer(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            Customer customer = (Customer) authentication.getPrincipal();
            Hibernate.initialize(customer.getRoles());
            model.addAttribute("customer", customer);
        }
        return "user";
    }
    
    @GetMapping("/admin")
    public String showAllCustomers(Model model) {
        model.addAttribute("customer", customerServiceImpl.findAll());
        return "admin";
    }

    @GetMapping("/admin/{id}")
    public String show(@PathVariable int id, Model model) {
        model.addAttribute("customer", customerServiceImpl.findOne(id));
        return "user";
    }

    @GetMapping("/admin/new")
    public String newCustomer(Model model) {
        Customer customer = new Customer();
        List<Role> allRoles = roleServiceImpl.getAllRoles();
        model.addAttribute("customer", customer);
        model.addAttribute("allRoles", allRoles);
        return "new";
    }

    @PostMapping("/admin")
    public String create(@ModelAttribute("customer") @Valid Customer customer, @RequestParam("selectedRoles") Integer[] selectedRoles) {
        Set<Role> roles = new HashSet<>();
        for (Integer roleID : selectedRoles) {
            Role roleObject = roleServiceImpl.findRoleById(roleID);
            if (roleObject != null) {
                roles.add(roleObject);
            }
        }
        customer.setRoles(roles);
        customerServiceImpl.save(customer);

        return "redirect:/admin";
    }

    @GetMapping("/admin/{id}/edit")
    public String edit(Model model, @PathVariable("id") int id) {
            Customer customer = customerServiceImpl.findOne(id);
            List<Role> allRoles = roleServiceImpl.getAllRoles();
            model.addAttribute("customer", customer);
            model.addAttribute("allRoles", allRoles);
            return "edit";
    }

    @PostMapping("/admin/{id}")
    public String update(@PathVariable("id") int id, @ModelAttribute("customer") @Valid Customer customer, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return "edit";
        }
        customerServiceImpl.update(id, customer);

        return "redirect:/admin";
    }
    @DeleteMapping("/admin/{id}")
    public String delete(@PathVariable("id") int id) {
        customerServiceImpl.delete(id);
        return "redirect:/admin";
    }
}
