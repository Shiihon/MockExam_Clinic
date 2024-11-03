package app.daos;

import app.dtos.DoctorDTO;
import app.entities.Appointment;
import app.entities.Doctor;
import app.enums.Speciality;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class DoctorDAO implements IDAO<DoctorDTO, Speciality> {
    private static EntityManagerFactory emf;

    public DoctorDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    @Override
    public Set<DoctorDTO> getAll() {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery("SELECT d FROM Doctor d", Doctor.class);

            return query.getResultStream().map(DoctorDTO::new).collect(Collectors.toSet());

        } catch (RollbackException e) {
            throw new RollbackException("Could not get all doctors", e);
        }
    }

    @Override
    public DoctorDTO getById(Long id) {
        try (EntityManager em = emf.createEntityManager()) {
            Doctor doctor = em.find(Doctor.class, id);

            if (doctor == null) {
                throw new EntityNotFoundException("Doctor with id " + id + " not found");
            }
            return new DoctorDTO(doctor);
        }
    }

    @Override
    public Set<DoctorDTO> getBySpeciality(Speciality speciality) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery("SELECT d FROM Doctor d WHERE d.speciality = :speciality", Doctor.class);
            query.setParameter("speciality", speciality);
            Set<DoctorDTO> queryResult = query.getResultStream().map(DoctorDTO::new).collect(Collectors.toSet());

            if (queryResult.isEmpty()) {
                throw new EntityNotFoundException("speciality :  " + speciality + " not found");
            }
            return queryResult;
        }
    }

    public Set<DoctorDTO> getByBirthdateRange(LocalDate from, LocalDate to) {
        try (EntityManager em = emf.createEntityManager()) {
            TypedQuery<Doctor> query = em.createQuery(
                    "SELECT d FROM Doctor d WHERE d.birthDate BETWEEN :from AND :to", Doctor.class);
            query.setParameter("from", from);
            query.setParameter("to", to);

            Set<DoctorDTO> queryResult = query.getResultStream()
                    .map(DoctorDTO::new)
                    .collect(Collectors.toSet());

            if (queryResult.isEmpty()) {
                throw new EntityNotFoundException("No doctors found in the birthdate range: " + from + " to " + to);
            }
            return queryResult;
        }
    }

    @Override
    public DoctorDTO create(DoctorDTO doctorDTO) {
        Doctor doctor = doctorDTO.getAsEntity();

        try (EntityManager em = emf.createEntityManager()) {
            em.getTransaction().begin();

            em.persist(doctor);
            em.getTransaction().commit();

            return new DoctorDTO(doctor);
        } catch (RuntimeException e) {
            throw new RuntimeException("Error creating doctor : " + e.getMessage(), e);
        }
    }


    @Override
    public DoctorDTO update(Long id, DoctorDTO doctorDTO) {
        Doctor doctor = doctorDTO.getAsEntity();
        try (EntityManager em = emf.createEntityManager()) {

            Doctor existingDoctor = em.find(Doctor.class, doctor.getId());
            if (existingDoctor == null) {
                throw new EntityNotFoundException("Doctor not found");
            }
            em.getTransaction().begin();

            if (doctor.getBirthDate() != null) {
                existingDoctor.setBirthDate(doctor.getBirthDate());
            }
            if (doctor.getYearOfGraduation() != null) {
                existingDoctor.setYearOfGraduation(doctor.getYearOfGraduation());
            }
            if (doctor.getClinicName() != null) {
                existingDoctor.setClinicName(doctor.getClinicName());
            }
            if (doctor.getSpeciality() != null) {
                existingDoctor.setSpeciality(doctor.getSpeciality());
            }

            em.getTransaction().commit();
            return new DoctorDTO(existingDoctor);

        } catch (RollbackException e) {
            throw new RollbackException(String.format("Unable to update doctor, with id: %d : %s", doctorDTO.getId(), e.getMessage()));
        }
    }
}
