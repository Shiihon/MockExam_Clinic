package app.controllers;

import app.daos.DoctorDAOMock;
import app.dtos.DoctorDTO;
import app.enums.Speciality;
import app.exceptions.ApiException;
import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Set;

public class DoctorMockController implements Controller {
    private DoctorDAOMock dao;

    public DoctorMockController(DoctorDAOMock dao) {
        this.dao = dao;
    }

    @Override
    public void getAll(Context ctx) {
        try {
            Set<DoctorDTO> doctors = dao.getAll();

            if (doctors.isEmpty()) {
                throw new EntityNotFoundException("No doctors were found");
            } else {
                ctx.res().setStatus(200);
                ctx.json(doctors);
            }
        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void getById(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            DoctorDTO doctor = dao.getById(id);

            if (doctor == null) {
                ctx.res().setStatus(404);
                throw new EntityNotFoundException("doctor with id " + id + " could not be found");
            }
            ctx.res().setStatus(200);
            ctx.json(doctor);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void getBySpeciality(Context ctx) {
        try {
            String specialityStr = ctx.pathParam("type");
            Speciality speciality = Speciality.valueOf(specialityStr.toUpperCase());

            Set<DoctorDTO> doctorsByType = dao.getBySpeciality(speciality);

            if (doctorsByType.isEmpty()) {
                ctx.res().setStatus(404);
                throw new EntityNotFoundException("Speciality " + speciality + " not found");
            } else {
                ctx.res().setStatus(200);
                ctx.json(doctorsByType);
            }

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void getByBirthdayRange(Context ctx) {
        try {
            // Extract 'from' and 'to' dates from query parameters
            LocalDate from = LocalDate.parse(ctx.queryParam("from"));
            LocalDate to = LocalDate.parse(ctx.queryParam("to"));

            // Fetch doctors within the birthday range using DAO
            Set<DoctorDTO> doctorsInRange = dao.getByBirthdateRange(from, to);

            if (doctorsInRange.isEmpty()) {
                throw new ApiException(404, "No doctors found in the specified birthday range.");
            }
            // Send the result as JSON with 200 OK status
            ctx.json(doctorsInRange);
            ctx.res().setStatus(200);

        } catch (DateTimeParseException e) {
            throw new ApiException(400, "Invalid date format. Use 'YYYY-MM-DD'.");

        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            DoctorDTO doctor = ctx.bodyAsClass(DoctorDTO.class);
            DoctorDTO newDoctor = dao.create(doctor);

            if (newDoctor != null) {
                ctx.res().setStatus(201);
                ctx.json(newDoctor);
            } else {
                ctx.res().setStatus(400);
                throw new IllegalArgumentException("doctor could not be created");
            }
        } catch (IllegalArgumentException e) {
            throw new ApiException(400, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void update(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            DoctorDTO doctorDTO = ctx.bodyAsClass(DoctorDTO.class);

            doctorDTO.setId(id);
            DoctorDTO updatedDoctorDTO = dao.update(doctorDTO.getId(), doctorDTO);

            ctx.res().setStatus(200);
            ctx.json(updatedDoctorDTO);

        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid ID format. Must be a number.");

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, "Doctor with the given id, could not be found");

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }
}
