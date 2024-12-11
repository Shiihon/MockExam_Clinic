package app.security.controllers;

import app.PopulatorTest;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.dtos.DoctorDTO;
import app.security.daos.SecurityDAO;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import app.util.ApiProps;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class SecurityControllerTest {
    private static PopulatorTest populatorTest;
    private static Javalin app;
    private static EntityManagerFactory emfTest;

    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static SecurityDAO securityDAO;
    private static SecurityController securityController;

    @BeforeAll
    static void beforeAll() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        populatorTest = new PopulatorTest(emfTest);
        securityDAO = new SecurityDAO(emfTest);

        app = AppConfig.startServer(emfTest);

        RestAssured.baseURI = String.format("http://localhost:%d/api", ApiProps.PORT);
    }

    @BeforeEach
    void setUp() {
        UserDTO[] users = PopulatorTest.populateUsers(emfTest);
        userDTO = users[0];
        adminDTO = users[1];

        try (EntityManager em = emfTest.createEntityManager()) {
            User user = em.find(User.class, userDTO.getUsername());
            System.out.println("user found : " + user);
        }

        try {
            UserDTO verifiedUser = securityDAO.getVerifiedUser(userDTO.getUsername(), userDTO.getPassword());
            UserDTO verifiedAdmin = securityDAO.getVerifiedUser(adminDTO.getUsername(), adminDTO.getPassword());
            userToken = "Bearer " + securityController.createToken(verifiedUser);
            adminToken = "Bearer " + securityController.createToken(verifiedAdmin);

        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        populatorTest.cleanup(User.class);
        populatorTest.cleanup(Role.class);
    }

    @AfterAll
    static void afterAll() {
        AppConfig.stopServer();
    }

    @Test
    void login() {
        // Ensure userDTO is correctly populated before running this test
        assertNotNull(userDTO, "UserDTO should not be null before login test");

        // Perform the login request
        UserDTO user = given()
                .contentType(ContentType.JSON)
                .body(userDTO)
                .when()
                .post("/auth/login/")
                .then()
                .extract()
                .as(UserDTO.class);

        assertEquals(userDTO.getUsername(), user.getUsername());
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
    System.out.println(userDTO);
}

}