package com.restTemplate.RestTemplate.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class User {
    private Long id;
    private String name;
    private String lastName;
    private Byte age;
}
