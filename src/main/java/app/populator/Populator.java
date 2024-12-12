package app;

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
import java.util.ArrayList;
import java.util.List;

public class Populator {
    private EntityManagerFactory emf;

    public Populator(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public static List<User> populateUsers(EntityManagerFactory emf) {
        User user1, user2, user3, admin;
        Role userRole, adminRole;

        // Create roles
        userRole = new Role("USER");
        adminRole = new Role("ADMIN");

        // Create users
        user1 = new User("user1", "user1");
        user2 = new User("user2", "user2");
        user3 = new User("user3", "user3");
        admin = new User("admin", "admin");

        // Add roles to users
        user1.addRole(userRole);
        user2.addRole(userRole);
        user3.addRole(userRole);
        admin.addRole(adminRole);

        try (var em = emf.createEntityManager()) {
            em.getTransaction().begin();

            // Persist roles if they don't exist
            if (em.find(Role.class, "USER") == null) {
                em.persist(userRole);
            }
            if (em.find(Role.class, "ADMIN") == null) {
                em.persist(adminRole);
            }

            // Check if users already exist by username, if not, persist them
            if (em.find(User.class, "user1") == null) {
                em.persist(user1);
            }
            if (em.find(User.class, "user2") == null) {
                em.persist(user2);
            }
            if (em.find(User.class, "user3") == null) {
                em.persist(user3);
            }
            if (em.find(User.class, "admin") == null) {
                em.persist(admin);
            }
            em.getTransaction().commit();
        }

        return List.of(user1, user2, user3, admin);
    }

    public List<Appointment> listOfAppointments(List<User> users, List<Doctor> doctors) {
        return List.of(
                new Appointment(
                        null,
                        "John Smith",
                        LocalDate.of(2023, 11, 24),
                        LocalTime.of(9, 45),
                        "First visit",
                        users.get(0),
                        doctors.get(1)
                ),
                new Appointment(
                        null,
                        "Alice Johnson",
                        LocalDate.of(2023, 11, 27),
                        LocalTime.of(10, 30),
                        "Follow up",
                        users.get(1),
                        doctors.get(2)
                ),
                new Appointment(
                        null,
                        "Bob Anderson",
                        LocalDate.of(2023, 12, 12),
                        LocalTime.of(14, 0),
                        "General check",
                        users.get(2),
                        doctors.get(3)
                ),
                new Appointment(
                        null,
                        "Emily White",
                        LocalDate.of(2023, 12, 15),
                        LocalTime.of(11, 0),
                        "Consultation",
                        users.get(1),
                        doctors.get(4)
                ),
                new Appointment(
                        null,
                        "David Martinez",
                        LocalDate.of(2023, 12, 18),
                        LocalTime.of(15, 30),
                        "Routine checkup",
                        users.get(0),
                        doctors.get(5)
                )
        );
    }

    public List<Doctor> create7Doctors() {
        return List.of(
                Doctor.builder()
                        .name("Dr. Alice Smith")
                        .birthDate(LocalDate.of(1975, 4, 12))
                        .yearOfGraduation(Year.of(2000))
                        .clinicName("City Health Clinic")
                        .speciality(Speciality.FAMILY_MEDICINE)
                        .appointments(null) // No appointments assigned yet
                        .build(),

                Doctor.builder()
                        .name("Dr. Bob Johnson")
                        .birthDate(LocalDate.of(1980, 8, 5))
                        .yearOfGraduation(Year.of(2005))
                        .clinicName("Downtown Medical Center")
                        .speciality(Speciality.SURGERY)
                        .appointments(new ArrayList<>()) // Assign appointments later
                        .build(),

                Doctor.builder()
                        .name("Dr. Clara Lee")
                        .birthDate(LocalDate.of(1983, 7, 22))
                        .yearOfGraduation(Year.of(2008))
                        .clinicName("Green Valley Hospital")
                        .speciality(Speciality.PEDIATRICS)
                        .appointments(new ArrayList<>()) // Assign appointments later
                        .build(),

                Doctor.builder()
                        .name("Dr. David Park")
                        .birthDate(LocalDate.of(1978, 11, 15))
                        .yearOfGraduation(Year.of(2003))
                        .clinicName("Hillside Medical Practice")
                        .speciality(Speciality.PSYCHIATRY)
                        .appointments(null) // No appointments assigned yet
                        .build(),

                Doctor.builder()
                        .name("Dr. Emily White")
                        .birthDate(LocalDate.of(1982, 9, 30))
                        .yearOfGraduation(Year.of(2007))
                        .clinicName("Metro Health Center")
                        .speciality(Speciality.PEDIATRICS)
                        .appointments(null) // No appointments assigned yet
                        .build(),

                Doctor.builder()
                        .name("Dr. Fiona Martinez")
                        .birthDate(LocalDate.of(1985, 2, 17))
                        .yearOfGraduation(Year.of(2010))
                        .clinicName("Riverside Wellness Clinic")
                        .speciality(Speciality.SURGERY)
                        .appointments(null) // No appointments assigned yet
                        .build(),

                Doctor.builder()
                        .name("Dr. George Kim")
                        .birthDate(LocalDate.of(1979, 5, 29))
                        .yearOfGraduation(Year.of(2004))
                        .clinicName("Summit Health Institute")
                        .speciality(Speciality.FAMILY_MEDICINE)
                        .appointments(null) // No appointments assigned yet
                        .build()
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


