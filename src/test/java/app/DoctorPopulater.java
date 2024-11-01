package app;

import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.Year;
import java.util.List;

public class DoctorPopulater {
    private EntityManagerFactory emf;

    public DoctorPopulater(EntityManagerFactory emf) {
        this.emf = emf;
    }

    public List<Doctor> create7Doctors() {
        return List.of(
                Doctor.builder()
                        .id(null)
                        .name("Dr. Alice Smith")
                        .birthDate(LocalDate.of(1975, 4, 12))
                        .yearOfGraduation(Year.of(2000))
                        .clinicName("City Health Clinic")
                        .speciality(Speciality.FAMILY_MEDICINE)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. Bob Johnson")
                        .birthDate(LocalDate.of(1980, 8, 5))
                        .yearOfGraduation(Year.of(2005))
                        .clinicName("Downtown Medical Center")
                        .speciality(Speciality.SURGERY)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. Clara Lee")
                        .birthDate(LocalDate.of(1983, 7, 22))
                        .yearOfGraduation(Year.of(2008))
                        .clinicName("Green Valley Hospital")
                        .speciality(Speciality.PEDIATRICS)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. David Park")
                        .birthDate(LocalDate.of(1978, 11, 15))
                        .yearOfGraduation(Year.of(2003))
                        .clinicName("Hillside Medical Practice")
                        .speciality(Speciality.PSYCHIATRY)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. Emily White")
                        .birthDate(LocalDate.of(1982, 9, 30))
                        .yearOfGraduation(Year.of(2007))
                        .clinicName("Metro Health Center")
                        .speciality(Speciality.PEDIATRICS)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. Fiona Martinez")
                        .birthDate(LocalDate.of(1985, 2, 17))
                        .yearOfGraduation(Year.of(2010))
                        .clinicName("Riverside Wellness Clinic")
                        .speciality(Speciality.SURGERY)
                        .build(),

                Doctor.builder()
                        .id(null)
                        .name("Dr. George Kim")
                        .birthDate(LocalDate.of(1979, 5, 29))
                        .yearOfGraduation(Year.of(2004))
                        .clinicName("Summit Health Institute")
                        .speciality(Speciality.FAMILY_MEDICINE)
                        .build()
        );
    }

    public List<Appointment> create5Appointments() {
        return List.of(
                Appointment.builder()
                        .id(null)
                        .clientName("John Smith")
                        .date(LocalDate.of(2023, 11, 24))
                        .time(LocalTime.of(9, 45))
                        .comment("First visit")
                        .build(),

                Appointment.builder()
                        .id(null)
                        .clientName("Alice Johnson")
                        .date(LocalDate.of(2023, 11, 27))
                        .time(LocalTime.of(10, 30))
                        .comment("Follow up")
                        .build(),

                Appointment.builder()
                        .id(null)
                        .clientName("Bob Anderson")
                        .date(LocalDate.of(2023, 12, 12))
                        .time(LocalTime.of(14, 0))
                        .comment("General check")
                        .build(),

                Appointment.builder()
                        .id(null)
                        .clientName("Emily White")
                        .date(LocalDate.of(2023, 12, 15))
                        .time(LocalTime.of(11, 0))
                        .comment("Consultation")
                        .build(),

                Appointment.builder()
                        .id(null)
                        .clientName("David Martinez")
                        .date(LocalDate.of(2023, 12, 18))
                        .time(LocalTime.of(15, 30))
                        .comment("Routine checkup")
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


