package app.routes;

import app.PopulatorTest;
import app.config.AppConfig;
import app.config.HibernateConfig;
import app.daos.DoctorDAO;
import app.dtos.AppointmentDTO;
import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
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
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Set;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

class DoctorRoutesTest {
    private static UserDTO userDTO, adminDTO;
    private static String userToken, adminToken;
    private static SecurityDAO securityDAO;
    private static SecurityController securityController;

    private static Javalin app;
    private static EntityManagerFactory emf;

    private static DoctorDAO doctorDAO;
    private static PopulatorTest populatorTest;

    private static List<AppointmentDTO> listOfAppointments;
    private static List<DoctorDTO> listOfDoctors;
    private static List<UserDTO> listOfUsers;
    private static List<Role> roles;

    private final String BASE_URL = "http://localhost:7000/api";

    @BeforeAll
    static void init() {
        emf = HibernateConfig.getEntityManagerFactoryForTest();
        app = AppConfig.startServer(emf);
        populatorTest = new PopulatorTest(emf);
        doctorDAO = new DoctorDAO(emf);
        securityDAO = new SecurityDAO(emf);
        securityController = securityController.getInstance(emf);
    }

    @BeforeEach
    void setUp() {
        List<Appointment> entityListOfAppointments = populatorTest.listOfAppointments();
        populatorTest.persist(entityListOfAppointments);

        List<Doctor> entityListOfDoctors = populatorTest.create7Doctors(entityListOfAppointments);
        populatorTest.persist(entityListOfDoctors);

        // Convert entities to DTOs after persisting
        listOfAppointments = entityListOfAppointments.stream().map(AppointmentDTO::new).toList();
        listOfDoctors = entityListOfDoctors.stream().map(DoctorDTO::new).toList();

        UserDTO[] users = PopulatorTest.populateUsers(emf);
        userDTO = users[0];
        adminDTO = users[1];

        try (EntityManager em = emf.createEntityManager()) {
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
        populatorTest.cleanup(Role.class);
        populatorTest.cleanup(User.class);

        populatorTest.cleanup(Doctor.class);
        populatorTest.cleanup(Appointment.class);
    }

    @AfterAll
    static void closeDown() {
        AppConfig.stopServer();
    }

    @Test
    void testGetAll() {
        DoctorDTO[] doctors = given()
                .when()
                .header("Authorization", userToken)
                .get(BASE_URL + "/doctors")
                .then()
                .statusCode(200)
                .extract()
                .as(DoctorDTO[].class);

        assertThat(doctors, arrayWithSize(7));
    }

    @Test
    void testGetById() {
        DoctorDTO expected = listOfDoctors.get(0);

        DoctorDTO actual = given()
                .when()
                .header("Authorization", adminToken)
                .get(BASE_URL + "/doctors/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(DoctorDTO.class);

        assertThat(expected, is(actual));
    }

    @Test
    void testGetBySpeciality() {
        DoctorDTO expectedDoctor = listOfDoctors.get(0);
        String speciality = expectedDoctor.getSpeciality().name(); // Convert enum to String

        DoctorDTO[] doctorsBySpeciality = given()
                .when()
                .header("Authorization", adminToken)
                .get(BASE_URL + "/doctors/specialities/{speciality}", speciality)
                .then()
                .statusCode(200)
                .extract()
                .as(DoctorDTO[].class);

        // Assertions to verify correct response
        assertThat(doctorsBySpeciality, arrayWithSize(greaterThan(0))); // Check if the array is not empty
        assertThat(doctorsBySpeciality[0].getSpeciality(), is(Speciality.valueOf(speciality)));
    }

    @Test
    void testGetByBirthdayRange() {
        LocalDate from = LocalDate.of(1970, 1, 1);
        LocalDate to = LocalDate.of(1980, 1, 1);

        Set<DoctorDTO> expected = doctorDAO.getByBirthdateRange(from, to);

        DoctorDTO[] actual = given()
                .when()
                .header("Authorization", adminToken)
                .queryParam("from", from.toString())
                .queryParam("to", to.toString())
                .get(BASE_URL + "/doctors/birthdate/range")
                .then()
                .statusCode(200)
                .extract()
                .as(DoctorDTO[].class);

        assertThat(actual, arrayWithSize(expected.size()));
    }

    @Test
    void testCreate() {
        DoctorDTO expected = new DoctorDTO(
                null,
                "test name",
                LocalDate.of(2000, 2, 2),
                Year.of(2024),
                "test clinic name",
                Speciality.PEDIATRICS,
                List.of()
        );
        DoctorDTO actual = given()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .body(expected)
                .when()
                .post(BASE_URL + "/doctors/")
                .then()
                .statusCode(201)
                .extract()
                .as(DoctorDTO.class);

        assertThat(actual.getName(), is(expected.getName()));
        assertThat(actual.getSpeciality(), is(expected.getSpeciality()));
        assertThat(actual.getYearOfGraduation(), is(expected.getYearOfGraduation()));
    }

    @Test
    void testUpdate() {
        DoctorDTO expected = listOfDoctors.get(2);
        expected.setYearOfGraduation(Year.of(2000));

        doctorDAO.update(expected.getId(), expected);

        DoctorDTO actual = given()
                .when()
                .header("Authorization", adminToken)
                .contentType(ContentType.JSON)
                .body(expected)
                .put(BASE_URL + "/doctors/{id}", expected.getId())
                .then()
                .statusCode(200)
                .extract()
                .as(DoctorDTO.class);

        assertThat(actual.getName(), is(expected.getName()));
    }
}