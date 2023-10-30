package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itm.space.backendresources.api.request.UserRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.hamcrest.Matchers.equalTo;

@SpringBootTest
@AutoConfigureMockMvc
public class UserIntegrationTest extends BaseIntegrationTest{

    @Autowired
    private MockMvc mockMvc;
    private ObjectWriter objectMapper;

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }
    private String getToken() {
        try (Keycloak keycloakClient = KeycloakBuilder.builder()
                .serverUrl("http://backend-keycloak-auth:8080/auth")
                .realm("ITM")
                .clientId("backend-gateway-client")
                .username("demo")
                .password("test")
                .clientSecret("OdS0AlvkLckq6ugHzvkNQw0TXYSvh881")
                .build()) {
            return keycloakClient.tokenManager().getAccessToken().getToken();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Test
    public void testCreateUser() throws Exception {
        UserRequest userRequest = new UserRequest("testuser", "testuser@example.com",
                "testpassword", "TestName", "TestLastName");

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .header("Authorization", "Bearer " + getToken())) // Добавляем заголовок с токеном
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    public void testGetUserById() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{id}", "f4ec8cb3-0de5-482c-86c2-1e9371789c77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken())) // Добавляем заголовок с токеном
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", equalTo("DemoName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", equalTo("DemoLastName")));
    }

    @Test
    public void testHello() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + getToken())) // Добавляем заголовок с токеном
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("f4ec8cb3-0de5-482c-86c2-1e9371789c77"));
    }
}