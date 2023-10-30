package com.springboot.service;

import com.springboot.model.Customer;
import com.springboot.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CustomerServiceImpl {
    private final CustomerRepository customerRepository;
    @Autowired
    public CustomerServiceImpl(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    public List<Customer> findAll() {
        return customerRepository.findAll();
    }
    public Customer findOne(int id) {
        Optional<Customer> foundCustomer = customerRepository.findById(id);
        return foundCustomer.orElse(null);
    }

    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    public void update(int id, Customer updatedCustomer) {
        updatedCustomer.setId(id);
        customerRepository.save(updatedCustomer);
    }

    public void delete(int id) {
        customerRepository.deleteById(id);
    }
}
