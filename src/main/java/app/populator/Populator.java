package app.populator;

import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import app.security.entities.Role;
import app.security.entities.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;
import java.util.Set;

public class Populator {
    private EntityManagerFactory emf;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Appointment> listOfAppointments() {
        return List.of(
                new Appointment(
                        null,
                        "John Smith",
                        LocalDate.of(2023, 11, 24),
                        LocalTime.of(9, 45),
                        "First visit"
                ),
                new Appointment(
                        null,
                        "Alice Johnson",
                        LocalDate.of(2023, 11, 27),
                        LocalTime.of(10, 30),
                        "Follow up"
                ),
                new Appointment(
                        null,
                        "Bob Anderson",
                        LocalDate.of(2023, 12, 12),
                        LocalTime.of(14, 0),
                        "General check"
                ),
                new Appointment(
                        null,
                        "Emily White",
                        LocalDate.of(2023, 12, 15),
                        LocalTime.of(11, 0),
                        "Consultation"
                ),
                new Appointment(
                        null,
                        "David Martinez",
                        LocalDate.of(2023, 12, 18),
                        LocalTime.of(15, 30),
                        "Routine checkup"
                )
        );
    }

    public List<Doctor> create7Doctors(List<Appointment> appointments) {
        return List.of(
                new Doctor(
                        null,
                        "Dr. Alice Smith",
                        LocalDate.of(1975, 4, 12),
                        Year.of(2000),
                        "City Health Clinic",
                        Speciality.FAMILY_MEDICINE,
                        List.of(appointments.get(0), appointments.get(1)) // Appointments for Dr. Alice Smith
                ),
                new Doctor(
                        null,
                        "Dr. Bob Johnson",
                        LocalDate.of(1980, 8, 5),
                        Year.of(2005),
                        "Downtown Medical Center",
                        Speciality.SURGERY,
                        List.of(appointments.get(2), appointments.get(3)) // Appointments for Dr. Bob Johnson
                ),
                new Doctor(
                        null,
                        "Dr. Clara Lee",
                        LocalDate.of(1983, 7, 22),
                        Year.of(2008),
                        "Green Valley Hospital",
                        Speciality.PEDIATRICS,
                        List.of(appointments.get(4)) // Appointments for Dr. Clara Lee
                ),
                new Doctor(
                        null,
                        "Dr. David Park",
                        LocalDate.of(1978, 11, 15),
                        Year.of(2003),
                        "Hillside Medical Practice",
                        Speciality.PSYCHIATRY,
                        null // No appointments assigned yet
                ),
                new Doctor(
                        null,
                        "Dr. Emily White",
                        LocalDate.of(1982, 9, 30),
                        Year.of(2007),
                        "Metro Health Center",
                        Speciality.PEDIATRICS,
                        null // No appointments assigned yet
                ),
                new Doctor(
                        null,
                        "Dr. Fiona Martinez",
                        LocalDate.of(1985, 2, 17),
                        Year.of(2010),
                        "Riverside Wellness Clinic",
                        Speciality.SURGERY,
                        null // No appointments assigned yet
                ),
                new Doctor(
                        null,
                        "Dr. George Kim",
                        LocalDate.of(1979, 5, 29),
                        Year.of(2004),
                        "Summit Health Institute",
                        Speciality.FAMILY_MEDICINE,
                        null // No appointments assigned yet
                )
        );
    }

    public List<Role> createRoles() {
        return List.of(
                new Role("user"),
                new Role("admin")
        );
    }

    public List<User> createUsers(List<Role> roles) {
        return List.of(
                new User(
                        "user1",
                        "1234",
                        Set.of(roles.get(0))
                ),
                new User(
                        "user2",
                        "1234",
                        Set.of(roles.get(1))
                )
        );
    }

    public void persist(List<?> entities) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            entities.forEach(em::persist);
            em.getTransaction().commit();
        }
    }

    public void cleanup(Class<?> entityClass) {
        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();
            em.createQuery("DELETE FROM " + entityClass.getSimpleName()).executeUpdate();
            em.getTransaction().commit();
        }
    }
}


