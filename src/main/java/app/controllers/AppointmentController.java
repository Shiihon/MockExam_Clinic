package app.controllers;

import app.daos.AppointmentDAO;
import app.dtos.AppointmentDTO;
import app.exceptions.ApiException;
import app.security.dtos.UserDTO;
import io.javalin.http.Context;
import jakarta.persistence.EntityNotFoundException;

import java.util.List;
import java.util.Set;

public class AppointmentController implements ControllerAppointment {
    private AppointmentDAO dao;

    public AppointmentController(AppointmentDAO dao) {
        this.dao = dao;
    }

    @Override
    public void getAll(Context ctx) {
        try {
            Set<AppointmentDTO> appointments = dao.getAll();

            if (appointments.isEmpty()) {
                throw new EntityNotFoundException("No appointments were found");
            } else {
                ctx.res().setStatus(200);
                ctx.json(appointments);
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
            AppointmentDTO appointment = dao.getById(id);

            if (appointment == null) {
                ctx.res().setStatus(404);
                throw new EntityNotFoundException("Appointment with id " + id + " could not be found");
            }
            ctx.res().setStatus(200);
            ctx.json(appointment);

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    public void getByUserId(Context ctx) {
        try {
            String username = ctx.pathParam("username");
            String password = ctx.pathParam("password");

            if (password == null) {
                throw new ApiException(400, "Password is required");
            }

            UserDTO userDTO = new UserDTO(username, password);

            List<AppointmentDTO> appointmentsByUser = dao.getAppointmentsByUser(userDTO);

            if (appointmentsByUser.isEmpty()) {
                ctx.res().setStatus(404);
                throw new EntityNotFoundException("No appointments for user " + username + " were found.");
            } else {
                ctx.res().setStatus(200);
                ctx.json(appointmentsByUser);
            }

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void create(Context ctx) {
        try {
            AppointmentDTO appointment = ctx.bodyAsClass(AppointmentDTO.class);
            AppointmentDTO newAppointment = dao.create(appointment);

            if (newAppointment != null) {
                ctx.res().setStatus(201);
                ctx.json(newAppointment);
            } else {
                ctx.res().setStatus(400);
                throw new IllegalArgumentException("Appointment could not be created");
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
            AppointmentDTO appointment = ctx.bodyAsClass(AppointmentDTO.class);

            appointment.setId(id);
            AppointmentDTO updatedAppointment = dao.update(appointment.getId(), appointment);

            ctx.res().setStatus(200);
            ctx.json(updatedAppointment);

        } catch (NumberFormatException e) {
            throw new ApiException(400, "Invalid ID format. Must be a number.");

        } catch (EntityNotFoundException e) {
            throw new ApiException(404, "Appointment with the given id, could not be found");

        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }

    @Override
    public void delete(Context ctx) {
        try {
            Long id = Long.parseLong(ctx.pathParam("id"));
            AppointmentDTO appointment = dao.getById(id);

            if (appointment == null) {
                ctx.res().setStatus(404);
                throw new EntityNotFoundException("Appointment with id " + id + " could not be found");
            } else {
                dao.delete(id);
                ctx.res().setStatus(204);
            }
        } catch (EntityNotFoundException e) {
            throw new ApiException(404, e.getMessage());
        } catch (Exception e) {
            throw new ApiException(500, e.getMessage());
        }
    }
}
