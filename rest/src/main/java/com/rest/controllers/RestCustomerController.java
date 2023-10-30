package com.rest.controllers;

import com.rest.model.Customer;
import com.rest.model.CustomerLoginRequest;
import com.rest.model.Role;
import com.rest.service.RoleServiceImpl;
import com.rest.utils.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.rest.service.CustomerServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.*;

@RestController
@RequestMapping("/")
public class RestCustomerController {
    private final CustomerServiceImpl customerServiceImpl;
    private final RoleServiceImpl roleServiceImpl;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public RestCustomerController(CustomerServiceImpl customerServiceImpl, RoleServiceImpl roleServiceImpl, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.customerServiceImpl = customerServiceImpl;
        this.roleServiceImpl = roleServiceImpl;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @PostMapping("/custom-login")
    public ResponseEntity<String> customLogin(@RequestBody CustomerLoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Получите информацию о пользователе (Customer) после успешной аутентификации
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // Возвращайте информацию о пользователе или токен, как вам нужно
        return ResponseEntity.ok("Authenticated as: " + userDetails.getUsername());
    }

    @GetMapping("/user")
    public Customer getCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Customer customer = (Customer) authentication.getPrincipal();
        return customer;
    }

    @GetMapping("/admin/all")
    public List<Customer> showAllCustomers() {
        return customerServiceImpl.findAll();
    }

    @GetMapping("/admin/{id}")
    public Customer show(@PathVariable int id) {
        return customerServiceImpl.findOne(id);
    }

    @PostMapping("/admin/new")
    public ResponseEntity<Customer> create(@RequestBody Customer customer, @RequestParam("selectedRoles") Integer[] selectedRoles) {
        Set<Role> roles = new HashSet<>();
        for (Integer roleID : selectedRoles) {
            Role roleObject = roleServiceImpl.findRoleById(roleID);
            if (roleObject != null) {
                roles.add(roleObject);
            }
        }
        customer.setRoles(roles);
        customerServiceImpl.save(customer);
        return ResponseEntity.ok(customer);
    }

    @PutMapping("/admin/{id}/update")
    public ResponseEntity<Customer> update(@PathVariable("id") int id, @RequestBody Customer updatedCustomer) {
        Customer existingCustomer = customerServiceImpl.findOne(id);

        existingCustomer.setName(updatedCustomer.getName());
        existingCustomer.setAge(updatedCustomer.getAge());
        existingCustomer.setUsername(updatedCustomer.getUsername());
        existingCustomer.setPassword(updatedCustomer.getPassword());
        existingCustomer.setRoles(updatedCustomer.getRoles());

        customerServiceImpl.update(id, existingCustomer);
        return ResponseEntity.ok(existingCustomer);
    }

    @DeleteMapping("/admin/{id}/delete")
    public ResponseEntity<String>  delete(@PathVariable("id") int id) {
        customerServiceImpl.delete(id);
        return ResponseEntity.ok("User deleted");
    }
}
