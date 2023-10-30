package ru.itmentor.spring.boot_security.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import ru.itmentor.spring.boot_security.demo.model.Customer;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.repository.CustomerRepository;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;

import javax.transaction.Transactional;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class CustomerServiceImpl implements UserDetailsService {
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    RoleRepository roleRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder;

    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Customer> customer = customerRepository.findByUsername(username);
        if(customer == null)
            throw new UsernameNotFoundException("User not found!");

        //customer.getRoles().size();
        return customer.get();
    }
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Customer findOne(int id) {
        Optional<Customer> foundCustomer = customerRepository.findById(id);
        return foundCustomer.orElse(new Customer());
    }
    @Transactional
    public void save(Customer customer) {
        //customer.setRoles(Collections.singleton(new Role(2,"ROLE_USER")));
        //customer.setPassword(bCryptPasswordEncoder.encode(customer.getPassword()));
        customerRepository.save(customer);
    }
    @Transactional
    public void update(int id, Customer updatedCustomer) {
        updatedCustomer.setId(id);
        customerRepository.save(updatedCustomer);
    }
    @Transactional
    public void delete(int id) {
        customerRepository.deleteById(id);
    }

    @Transactional
    public void updateUserAndRoles(int id, Customer updatedCustomer, Set<Role> selectedRoles) {
        Customer existingCustomer = findOne(id);
        if (existingCustomer != null) {
            // Обновите данные пользователя
            existingCustomer.setName(updatedCustomer.getName());
            existingCustomer.setAge(updatedCustomer.getAge());
            existingCustomer.setUsername(updatedCustomer.getUsername());
            existingCustomer.setPassword(updatedCustomer.getPassword());

            // Обновите роли пользователя
            existingCustomer.setRoles(selectedRoles);

            // Сохраните обновленного пользователя
            customerRepository.save(existingCustomer);
        }
    }
}
