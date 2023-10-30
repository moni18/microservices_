package com.itm.space.backendresources;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.itm.space.backendresources.api.request.UserRequest;
import dasniko.testcontainers.keycloak.KeycloakContainer;
import org.junit.jupiter.api.BeforeEach;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@AutoConfigureMockMvc
public class KeyCloakContainerTest extends BaseIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    private ObjectWriter objectMapper;
    @Container
    static private KeycloakContainer keycloak = new KeycloakContainer().withRealmImportFile("keycloak/ITM.json");

    @BeforeEach
    void setup() {
        objectMapper = new ObjectMapper().writer().withDefaultPrettyPrinter();
    }

    @DynamicPropertySource
    static void registerResourceServerIssuerProperty(DynamicPropertyRegistry registry) {
        registry.add("spring.security.oauth2.resourceserver.jwt.issuer-uri", () -> keycloak.getAuthServerUrl() + "/realms/ITM");
    }
    private String getToken(){
        try(Keycloak keycloakClient = KeycloakBuilder.builder()
                .serverUrl(keycloak.getAuthServerUrl())
                .realm("ITM")
                .clientId("backend-gateway-client")
                .username("demo")
                .password("test")
                .clientSecret("OdS0AlvkLckq6ugHzvkNQw0TXYSvh881")
                .build()) {
            return keycloakClient.tokenManager().getAccessToken().getToken();
        }catch (Exception ex) {
            ex.printStackTrace();
        }
        return null;
    }

    protected RequestPostProcessor bearerToken(String accessToken){
        return mockRequest -> {
            mockRequest.addHeader("Authorization", "Bearer " + accessToken);
            return mockRequest;
        };
    }
    @Test
    public void testCreateUser() throws Exception{
        UserRequest userRequest = new UserRequest("testuser", "testuser@example.com",
                "testpassword", "TestName", "TestLastName");

        mockMvc.perform(requestToJson(MockMvcRequestBuilders
                        .post("/api/users"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
                        .with(bearerToken(getToken())))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
    @Test
    public void testGetUserById() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/{id}", "f4ec8cb3-0de5-482c-86c2-1e9371789c77")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(bearerToken(getToken())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.firstName", equalTo("DemoName")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.lastName", equalTo("DemoLastName")));
    }

    @Test
    public void testHello() throws Exception{
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/users/hello")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(bearerToken(getToken())))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string("f4ec8cb3-0de5-482c-86c2-1e9371789c77"));
    }
}