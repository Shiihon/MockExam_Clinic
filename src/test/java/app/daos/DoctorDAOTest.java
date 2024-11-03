package app.daos;

import app.PopulatorTest;
import app.config.HibernateConfig;
import app.dtos.AppointmentDTO;
import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import jakarta.persistence.EntityManagerFactory;
import org.junit.jupiter.api.*;

import java.time.LocalDate;
import java.time.Year;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

class DoctorDAOTest {
    private static EntityManagerFactory emfTest;
    private static List<DoctorDTO> listOfDoctors;
    private static List<AppointmentDTO> listOfAppointments;
    private static DoctorDAO doctorDAO;
    private static PopulatorTest populatorTest;

    @BeforeAll
    static void setUpBeforeClass() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        populatorTest = new PopulatorTest(emfTest);
        doctorDAO = new DoctorDAO(emfTest);
    }

    @BeforeEach
    void setUp() {
        List<Appointment> entityListOfAppointments = populatorTest.listOfAppointments();
        List<Doctor> entityListOfDoctors = populatorTest.create7Doctors(entityListOfAppointments);

        populatorTest.persist(entityListOfAppointments);
        populatorTest.persist(entityListOfDoctors);

        // Convert entities to DTOs after persisting
        listOfAppointments = entityListOfAppointments.stream().map(AppointmentDTO::new).toList();
        listOfDoctors = entityListOfDoctors.stream().map(DoctorDTO::new).toList();
    }

    @AfterEach
    void tearDown() {
        populatorTest.cleanup(Doctor.class);
        populatorTest.cleanup(Appointment.class);
    }

    @Test
    void getAll() {
        List<DoctorDTO> expected = listOfDoctors;
        List<DoctorDTO> actual = doctorDAO.getAll().stream().toList();
        System.out.println(expected);
        System.out.println(actual);

        assertThat(actual, hasSize(expected.size()));
        //assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getById() {
        DoctorDTO expected = listOfDoctors.get(0);
        DoctorDTO actual = doctorDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void getBySpeciality() {
        Speciality speciality = Speciality.PEDIATRICS;

        List<DoctorDTO> expected = listOfDoctors.stream().filter(doctorDTO -> doctorDTO.getSpeciality().equals(speciality)).toList();
        List<DoctorDTO> actual = doctorDAO.getBySpeciality(speciality).stream().toList();
        System.out.println(expected);
        System.out.println(actual);

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }

    @Test
    void getByBirthdateRange() {
        LocalDate fromDate = LocalDate.of(1980, 1, 1);
        LocalDate toDate = LocalDate.of(1990, 12, 31);

        // Expected doctors within the birthdate range
        List<DoctorDTO> expected = listOfDoctors.stream()
                .filter(doctorDTO -> doctorDTO.getBirthDate() != null &&
                        !doctorDTO.getBirthDate().isBefore(fromDate) &&
                        !doctorDTO.getBirthDate().isAfter(toDate))
                .map(doctor -> new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getBirthDate(), null, null, null, null)) // Only name and birthDate populated
                .toList();

        // Actual result from DAO method with only name and birthDate
        List<DoctorDTO> actual = doctorDAO.getByBirthdateRange(fromDate, toDate).stream()
                .map(doctor -> new DoctorDTO(doctor.getId(), doctor.getName(), doctor.getBirthDate(), null, null, null, null))
                .toList();

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }


    @Test
    void create() {
        DoctorDTO expected = new DoctorDTO(
                null,
                "TestName",
                LocalDate.of(1992, 2, 1),
                Year.of(2024),
                "testClinic",
                Speciality.PEDIATRICS,
                List.of()
        );

        DoctorDTO createdDoctor = doctorDAO.create(expected);

        assertNotNull(createdDoctor);  // Ensure the doctor was created
        assertNotNull(createdDoctor.getId());  // Check that the ID is now generated
        assertEquals("TestName", createdDoctor.getName());
    }

    @Test
    void update() {
        DoctorDTO expected = listOfDoctors.get(0);
        expected.setYearOfGraduation(Year.of(2024));

        DoctorDTO actual = doctorDAO.update(expected.getId(), expected);

        Assertions.assertEquals(expected, actual);
    }
}