package com.itm.space.backendresources.conteiners;

import org.springframework.stereotype.Component;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Component
public class CustomKeycloakContainer extends GenericContainer<CustomKeycloakContainer> {
    private static final String KEYCLOAK_IMAGE = "quay.io/keycloak/keycloak:22.0.0";

    public CustomKeycloakContainer() {
        super(KEYCLOAK_IMAGE);
        withExposedPorts(8080);
        withEnv("KEYCLOAK_IMPORT", "keycloak/ITM.json");
        waitingFor(Wait.forHttp("/auth/realms/ITM"));

    }

    public Integer getKeycloakPort() {
        return getMappedPort(8080);
    }


//    public String getAuthServerUrl() {
//        return "http://" + getContainerIpAddress() + ":" + getMappedPort(8080);
//        // return "http://keycloak-server.com:" + getMappedPort(8081);
//    }

    /*private String getKeycloakWaitUrl() {
        // Здесь укажите URL Keycloak, который вы хотите использовать для ожидания
        return "/http://backend-keycloak-auth:8082/auth";
    }*/
//    protected void configure() {
//        withCopyFileToContainer(MountableFile.forHostPath("C:\\keyCloak\\keycloak-22.0.0"), "/opt/jboss/keycloak");
//        withCommand("/opt/jboss/keycloak/bin/standalone.sh", "start-dev","--port=8080", "--host=0.0.0.0");
//        //"--port=8081", "--host=0.0.0.0"
//    }
}
