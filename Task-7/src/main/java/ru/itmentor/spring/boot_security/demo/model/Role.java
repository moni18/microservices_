package ru.itmentor.spring.boot_security.demo.model;

import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;


@Entity
@Getter
@Setter
//@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name="roles")
public class Role implements GrantedAuthority {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    public int id;
    @Column
    public String role;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    private Set<Customer> customers = new HashSet<>();
    public Role(int id, String role) {
        this.id = id;
        this.role = role;
    }

    @Override
    public String getAuthority() {
        return this.role;
    }
}