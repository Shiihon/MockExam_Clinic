package app.routes;

import app.PopulatorTest;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.daos.AppointmentDAO;
import app.dtos.AppointmentDTO;
import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.security.controllers.SecurityController;
import app.security.daos.SecurityDAO;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ValidationException;
import io.javalin.Javalin;
import io.restassured.http.ContentType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.arrayWithSize;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentRoutesTest {
    private static UserDTO userDTO, adminDTO;
    private static User user1, user2, user3, admin;
    private static String userToken1, userToken2, userToken3, adminToken;
    private static SecurityDAO securityDAO;
    private static SecurityController securityController;

    private static Javalin app;
    private static EntityManagerFactory emfTest;

    private static AppointmentDAO appointmentDAO;
    private static PopulatorTest populatorTest;

    private static List<AppointmentDTO> listOfAppointments;
    private static List<DoctorDTO> listOfDoctors;
    private static List<UserDTO> listOfUsers;
    private static List<Role> roles;

    private final String BASE_URL = "http://localhost:7000/api";

    @BeforeAll
    static void init() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        app = AppConfig.startServer(emfTest);
        populatorTest = new PopulatorTest(emfTest);
        appointmentDAO = new AppointmentDAO(emfTest);
        securityDAO = new SecurityDAO(emfTest);
        securityController = securityController.getInstance(emfTest);
    }

    @BeforeEach
    void setUp() {
        // Populate users and roles using populateUsers method (ensures at least 3 users)
        List<User> entityListOfUsers = PopulatorTest.populateUsers(emfTest); // This will create and persist users and roles
        user1 = entityListOfUsers.get(0);  // Get the first user
        user2 = entityListOfUsers.get(1); // Get the second user
        user3 = entityListOfUsers.get(2); // Get the third user
        admin = entityListOfUsers.get(3);

        // Persist doctors and appointments
        List<Doctor> entityListOfDoctors = populatorTest.create7Doctors();
        populatorTest.persist(entityListOfDoctors);

        List<Appointment> entityListOfAppointments = populatorTest.listOfAppointments(entityListOfUsers, entityListOfDoctors);
        populatorTest.persist(entityListOfAppointments);

        // Convert entities to DTOs after persisting
        UserDTO userDTO1 = new UserDTO(user1.getUsername(), "user1");
        UserDTO userDTO2 = new UserDTO(user2.getUsername(), "user2");
        UserDTO userDTO3 = new UserDTO(user3.getUsername(), "user3");
        UserDTO adminDTO = new UserDTO(admin.getUsername(), "admin");

        listOfUsers = List.of(userDTO1, userDTO2, userDTO3, adminDTO);

        listOfDoctors = entityListOfDoctors.stream().map(DoctorDTO::new).toList();
        listOfAppointments = entityListOfAppointments.stream().map(AppointmentDTO::new).toList();


        try (EntityManager em = emfTest.createEntityManager()) {
            // Example of verifying a user from the database
            User user = em.find(User.class, userDTO1.getUsername());
            System.out.println("User found: " + user);
        }

        // Iterate over UserDTOs to generate tokens for each user
        try {
            for (UserDTO user : listOfUsers) {
                UserDTO verifiedUser = securityDAO.getVerifiedUser(user.getUsername(), user.getPassword());
                String token = "Bearer " + securityController.createToken(verifiedUser);

                // Assign token to corresponding variable based on username
                if (user.getUsername().equals(userDTO1.getUsername())) {
                    userToken1 = token; // For the first user (userDTO1)
                } else if (user.getUsername().equals(adminDTO.getUsername())) {
                    adminToken = token; // For the admin (adminDTO)
                } else if (user.getUsername().equals(userDTO2.getUsername())) {
                    userToken2 = token; // For the second user (userDTO2)
                } else if (user.getUsername().equals(userDTO3.getUsername())) {
                    userToken3 = token; // For the third user (userDTO3)
                }
            }
        } catch (ValidationException e) {
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        populatorTest.cleanup(Appointment.class);
        populatorTest.cleanup(Doctor.class);

        populatorTest.cleanup(Role.class);
        populatorTest.cleanup(User.class);
    }

    @AfterAll
    static void closeDown() {
        AppConfig.stopServer();
    }

    @Test
    void testGetAll() {
        AppointmentDTO[] appointments = given()
                .when()
                .header("Authorization", adminToken)
                .get(BASE_URL + "/appointments")
                .then()
                .statusCode(200)
                .extract()
                .as(AppointmentDTO[].class);

        assertThat(appointments, arrayWithSize(5));
    }

    @Test
    void testGetByUserId() {
        AppointmentDTO expected = listOfAppointments.get(0);

        AppointmentDTO actual = given()
                .when()
                .header("Authorization", adminToken)
                .get(BASE_URL + "/appointments/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(AppointmentDTO.class);

        assertThat(expected, is(actual));
    }

    @Test
    void testCreate() {

        String userName = listOfUsers.get(0).getUsername();
        String name = listOfUsers.get(0).getFirstName() + " " + listOfUsers.get(0).getLastName();

        AppointmentDTO expected = new AppointmentDTO(
                null,  // id
                user1.getFirstname() + " " + user1.getLastname(),  // clientName
                listOfDoctors.get(0).getId(),  // doctorId
                user1.getUsername(),  // userName
                LocalDate.of(2024, 10, 10),  // date
                LocalTime.of(10, 30),  // time
                "testComment",  // comment
                user1  // user
        );

        AppointmentDTO actual = given()
                .header("Authorization", userToken1)
                .contentType(ContentType.JSON)
                .body(expected)
                .when()
                .post(BASE_URL + "/appointments/")
                .then()
                .statusCode(201)
                .extract()
                .as(AppointmentDTO.class);

        assertThat(actual.getClientName(), is(expected.getClientName()));
        assertThat(actual.getComment(), is(expected.getComment()));
        assertThat(actual.getDate(), is(expected.getDate()));

        assertThat(actual.getUserName(), is(expected.getUserName()));
    }

    @Test
    void testUpdate() {
        AppointmentDTO expected = listOfAppointments.get(1);
        expected.setClientName("newTestName");

        appointmentDAO.update(expected.getId(), expected);

        AppointmentDTO actual = given()
                .when()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .body(expected)
                .put(BASE_URL + "/appointments/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(AppointmentDTO.class);

        assertThat(actual.getClientName(), is(expected.getClientName()));
    }

    @Test
    void testDelete() {
        given()
                .when()
                .header("Authorization", adminToken)
                .delete(BASE_URL + "/appointments/{id}", listOfAppointments.get(0).getId())
                .then()
                .statusCode(204);

        assertThrows(EntityNotFoundException.class, () -> appointmentDAO.getById(listOfAppointments.get(0).getId()));
    }
}