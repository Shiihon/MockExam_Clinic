package app.daos;

import app.dtos.AppointmentDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.security.dtos.UserDTO;
import app.security.entities.User;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class AppointmentDAO implements IDAO<AppointmentDTO, Long> {
    private static EntityManagerFactory emf;

    public AppointmentDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<AppointmentDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Appointment> query = em.createQuery("SELECT a FROM Appointment a", Appointment.class);
            return query.getResultStream()
                    .map(AppointmentDTO::new) // Convert to DTO
                    .collect(Collectors.toSet());
        }
    }

    @Override
    public AppointmentDTO getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Appointment appointment = em.find(Appointment.class, id);
            if (appointment == null) {
                throw new EntityNotFoundException("Appointment with id " + id + " not found");
            }
            return new AppointmentDTO(appointment);
        }
    }

    public User getUserByUsername(String username) {
        // Ensure EntityManager is properly opened and closed
        try (EntityManager em = emf.createEntityManager()) {
            // Perform the query to fetch the user by username
            User user = em.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class)
                    .setParameter("username", username)
                    .getSingleResult();

            return user;
        } catch (NoResultException e) {
            // Handle case where no user is found
            return null; // Or throw an exception based on your use case
        } catch (Exception e) {
            // Log and rethrow or handle unexpected exceptions
            throw new RuntimeException("Error fetching user by username", e);
        }
    }

    public List<AppointmentDTO> getAppointmentsByUser(UserDTO user) {
        try (EntityManager em = emf.createEntityManager()) {
            String query = "SELECT a FROM Appointment a WHERE a.user.username = :userName";

            TypedQuery<Appointment> query1 = em.createQuery(query, Appointment.class);
            query1.setParameter("userName", user.getUsername());

            List<Appointment> appointments = query1.getResultList();

            if (appointments == null) {
                appointments = new ArrayList<>();
            }

            List<AppointmentDTO> appointmentDTOS = appointments.stream()
                    .map(AppointmentDTO::new)
                    .toList();

            return appointmentDTOS;
        } catch (RuntimeException e) {
            throw new EntityNotFoundException("Appointment for user " + user.getUsername() + " not found");
        }
    }

    @Override // no usage.
    public Set<AppointmentDTO> getBySpeciality(Long aLong) {
        return Set.of();
    }

    // Create an appointment without a doctor
    @Override
    public AppointmentDTO create(AppointmentDTO appointmentDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Appointment appointment = appointmentDTO.getAsEntity();

            User user = em.find(User.class, appointmentDTO.getUserName());
            if (user == null) {
                throw new EntityNotFoundException("User not found");
            }

            Doctor doctor = em.find(Doctor.class, appointmentDTO.getDoctorId());
            if (doctor == null) {
                throw new EntityNotFoundException("Doctor not found");
            }

            appointment.setUser(user); // Assign the user
            appointment.setDoctor(doctor); // Initially no doctor

            em.getTransaction().begin();
            em.persist(appointment);
            em.getTransaction().commit();

            return new AppointmentDTO(appointment);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating appointment: " + e.getMessage(), e);
        }
    }

    @Override
    public AppointmentDTO update(Long id, AppointmentDTO appointmentDTO) {
        try (EntityManager em = emf.createEntityManager()) {
            Appointment appointment = em.find(Appointment.class, id);
            if (appointment == null) {
                throw new EntityNotFoundException("Appointment not found");
            }

            em.getTransaction().begin();

            if (appointmentDTO.getClientName() != null) {
                appointment.setClientName(appointmentDTO.getClientName());
            }
            if (appointmentDTO.getDate() != null) {
                appointment.setDate(appointmentDTO.getDate());
            }
            if (appointmentDTO.getTime() != null) {
                appointment.setTime(appointmentDTO.getTime());
            }
            if (appointmentDTO.getComment() != null) {
                appointment.setComment(appointmentDTO.getComment());
            }

            em.getTransaction().commit();
            return new AppointmentDTO(appointment);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error updating appointment: " + e.getMessage(), e);
        }
    }

    public void delete(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Appointment appointment = em.find(Appointment.class, id);
            if (appointment == null) {
                throw new EntityNotFoundException("Appointment not found");
            }
            em.getTransaction().begin();
            em.remove(appointment);
            em.getTransaction().commit();
        }
    }
}
