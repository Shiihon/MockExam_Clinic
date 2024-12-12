package app.daos;

import app.PopulatorTest;
import app.config.HibernateConfig;
import app.dtos.AppointmentDTO;
import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.*;

class AppointmentDAOTest {
    private static EntityManagerFactory emfTest;
    private static List<DoctorDTO> listOfDoctors;
    private static List<AppointmentDTO> listOfAppointments;
    private static List<UserDTO> listOfUsers;
    private static AppointmentDAO appointmentDAO;
    private static PopulatorTest populatorTest;
    private static User user1, user2, user3, admin;

    @BeforeAll
    static void setUpBeforeClass() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        populatorTest = new PopulatorTest(emfTest);
        appointmentDAO = new AppointmentDAO(emfTest);
    }

    @BeforeEach
    void setUp() {
        List<User> entityListOfUsers = PopulatorTest.populateUsers(emfTest); // This will create and persist users and roles
        user1 = entityListOfUsers.get(0);  // Get the first user
        user2 = entityListOfUsers.get(1); // Get the second user
        user3 = entityListOfUsers.get(2); // Get the third user
        admin = entityListOfUsers.get(3);

        List<Doctor> entityListOfDoctors = populatorTest.create7Doctors();
        populatorTest.persist(entityListOfDoctors);

        List<Appointment> entityListOfAppointments = populatorTest.listOfAppointments(entityListOfUsers, entityListOfDoctors);
        populatorTest.persist(entityListOfAppointments);

        // Convert entities to DTOs after persisting
        listOfUsers = entityListOfUsers.stream().map(user -> new UserDTO(user.getUsername(), user.getPassword())).toList();
        listOfDoctors = entityListOfDoctors.stream().map(DoctorDTO::new).toList();
        listOfAppointments = entityListOfAppointments.stream().map(AppointmentDTO::new).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTest.cleanup(Appointment.class);
        populatorTest.cleanup(Doctor.class);

        populatorTest.cleanup(Role.class);
        populatorTest.cleanup(User.class);
    }

    @Test
    void getAll() {
        List<AppointmentDTO> expected = listOfAppointments;
        List<AppointmentDTO> actual = appointmentDAO.getAll().stream().toList();
        System.out.println(expected);
        System.out.println(actual);

        assertThat(actual, hasSize(expected.size()));
    }

    @Test
    void getById() {
        AppointmentDTO expected = listOfAppointments.get(0);
        AppointmentDTO actual = appointmentDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getAppointmentsByUser() {
        List<AppointmentDTO> expected = listOfAppointments.stream()
                .filter(appointmentDTO -> appointmentDTO.getUserName().equals(listOfUsers.get(0).getUsername()))
                .toList();
        List<AppointmentDTO> actual = appointmentDAO.getAppointmentsByUser(listOfUsers.get(0));

        // Assertions
        assertThat(actual, hasSize(expected.size())); // Ensure the sizes match
        assertThat(actual, containsInAnyOrder(expected.toArray())); // Ensure the contents match
    }

    @Test
    void getBySpeciality() {
    }

    @Test
    void create() {
        AppointmentDTO expected = new AppointmentDTO(
                null,
                "TestName",
                listOfDoctors.get(0).getId(),
                "user1",
                LocalDate.of(2024, 12, 12),
                LocalTime.of(20, 16),
                "test comment"
        );
        AppointmentDTO createdApp = appointmentDAO.create(expected);

        assertNotNull(createdApp);  // Ensure the doctor was created
        assertNotNull(createdApp.getId());  // Check that the ID is now generated
        assertEquals("TestName", createdApp.getClientName());
    }

    @Test
    void update() {
        AppointmentDTO expected = listOfAppointments.get(1);
        expected.setClientName("TestName2");

        AppointmentDTO actual = appointmentDAO.update(expected.getId(), expected);

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void delete() {
        Long appointmentId = listOfAppointments.get(2).getId();

        appointmentDAO.delete(appointmentId);

       assertThrows(EntityNotFoundException.class, () -> appointmentDAO.getById(appointmentId));
    }
}