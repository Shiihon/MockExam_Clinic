package app.daos;

import app.DoctorPopulater;
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
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;

class DoctorDAOTest {
    private static EntityManagerFactory emfTest;
    private static List<DoctorDTO> listOfDoctors;
    private static List<AppointmentDTO> listOfAppointments;
    private static DoctorDAO doctorDAO;
    private static DoctorPopulater populater;

    @BeforeAll
    static void setUpBeforeClass() {
        emfTest = HibernateConfig.getEntityManagerFactoryForTest();
        populater = new DoctorPopulater(emfTest);
        doctorDAO = new DoctorDAO(emfTest);
    }

    @BeforeEach
    void setUp() {
        List<Doctor> entityListOfDoctors = populater.create7Doctors();
        List<Appointment> entityListOfAppointments = populater.create5Appointments();

        entityListOfDoctors.get(0).setAppointments(List.of(entityListOfAppointments.get(0), entityListOfAppointments.get(1)));
        entityListOfDoctors.get(1).setAppointments(List.of(entityListOfAppointments.get(2), entityListOfAppointments.get(3)));
        entityListOfDoctors.get(2).setAppointments(List.of(entityListOfAppointments.get(4)));

        entityListOfAppointments.get(0).setDoctor(entityListOfDoctors.get(0));
        entityListOfAppointments.get(1).setDoctor(entityListOfDoctors.get(0));
        entityListOfAppointments.get(2).setDoctor(entityListOfDoctors.get(1));
        entityListOfAppointments.get(3).setDoctor(entityListOfDoctors.get(1));
        entityListOfAppointments.get(4).setDoctor(entityListOfDoctors.get(2));

        populater.persist(entityListOfDoctors);
        populater.persist(entityListOfAppointments);

        //Fra entitet til DTO.
        listOfDoctors = entityListOfDoctors.stream().map(DoctorDTO::new).toList();
        listOfAppointments = entityListOfAppointments.stream().map(AppointmentDTO::new).toList();
    }

    @AfterEach
    void tearDown() {
        populater.cleanup(Appointment.class);
        populater.cleanup(Doctor.class);
    }

    @Test
    void getAll() {
        List<DoctorDTO> expected = new ArrayList<>(listOfDoctors);
        List<DoctorDTO> actual = doctorDAO.getAll().stream().toList();

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
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

        List<DoctorDTO> expected = listOfDoctors.stream().filter(ddoctorDTO -> ddoctorDTO.getSpeciality().equals(speciality)).toList();
        List<DoctorDTO> actual = doctorDAO.getBySpeciality(speciality).stream().toList();

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
                .toList();

        // Actual result from DAO method
        List<DoctorDTO> actual = doctorDAO.getByBirthdateRange(fromDate, toDate).stream().toList();

        assertThat(actual, hasSize(expected.size()));
        assertThat(actual, containsInAnyOrder(expected.toArray()));
    }


    @Test
    void create() {
        DoctorDTO doctorDTO = new DoctorDTO(null, "TestName", LocalDate.of(1992,2,1), Year.of(2024), "testClinic", Speciality.PEDIATRICS, listOfAppointments);

        DoctorDTO expected = doctorDAO.create(doctorDTO);
        DoctorDTO actual = doctorDAO.getById(expected.getId());

        Assertions.assertEquals(expected, actual);
    }

    @Test
    void update() {
        DoctorDTO expected = listOfDoctors.get(0);
        expected.setYearOfGraduation(Year.of(2024));

        DoctorDTO actual = doctorDAO.update(expected.getId(), expected);

        Assertions.assertEquals(expected, actual);
    }
}