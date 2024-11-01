package app.security.controllers;

import app.PopulatorTestUtil;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import app.util.ApiProps;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

class SecurityControllerTest {
    private static PopulatorTestUtil populatorTestUtil;
    private static Javalin app;

    @BeforeAll
    static void beforeAll() {
        EntityManagerFactory emf = HibernateConfig.getEntityManagerFactoryForTest();

        populatorTestUtil = new PopulatorTestUtil(emf);
        app = AppConfig.startServer(emf);

        RestAssured.baseURI = String.format("http://localhost:%d/api", ApiProps.PORT);
    }

    @BeforeEach
    void setUp() {
        List<Role> roles = populatorTestUtil.createRoles();

        List<User> users = populatorTestUtil.createUsers(roles);
        populatorTestUtil.persist(users);
    }

    @AfterEach
    void tearDown() {
        populatorTestUtil.cleanup(User.class);
        populatorTestUtil.cleanup(Role.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer();
    }

    @Test
    void login() {
        UserDTO userDTO = new UserDTO("User1", "1234");

        given()
                .body(userDTO)
                .when()
                .post("/auth/login")
                .then()
                .statusCode(200)
                .body("username", equalTo(userDTO.getUsername()))
                .body("token", is(notNullValue()));
    }

    @Test
    void register() {
        UserDTO userDTO = new UserDTO("User8", "1234");

        given()
                .body(userDTO)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201)
                .body("username", equalTo(userDTO.getUsername()));
    }

}