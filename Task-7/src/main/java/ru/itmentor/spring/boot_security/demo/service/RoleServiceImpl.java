package ru.itmentor.spring.boot_security.demo.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.itmentor.spring.boot_security.demo.model.Role;
import ru.itmentor.spring.boot_security.demo.repository.RoleRepository;

import java.util.List;
import java.util.Optional;

@Service
public class RoleServiceImpl {
    private final RoleRepository roleRepository;
    @Autowired
    public RoleServiceImpl(RoleRepository roleRepository){
        this.roleRepository=roleRepository;
    }
    public List<Role> getAllRoles(){
        return roleRepository.findAll();
    }
    public Role findRoleById(Integer id) {
        Optional<Role> foundRole = roleRepository.findById(id);
        return foundRole.orElse(null);
    }
}
