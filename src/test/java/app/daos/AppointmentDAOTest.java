package app.daos;

import app.PopulatorTest;
import app.config.HibernateConfig;
import app.dtos.AppointmentDTO;
import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

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
    }

    @Test
    void getById() {
    }

    @Test
    void getAppointmentsByUser() {
    }

    @Test
    void getBySpeciality() {
    }

    @Test
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void delete() {
    }
}