package app.routes;

import app.controllers.AppointmentController;
import app.controllers.DoctorController;
import app.daos.AppointmentDAO;
import app.daos.DoctorDAO;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class AppointmentRoutes {
    private static AppointmentController appointmentController;
    private static AppointmentDAO appointmentDAO;

    public AppointmentRoutes(EntityManagerFactory emf) {
        appointmentDAO = new AppointmentDAO(emf);
        appointmentController = new AppointmentController(appointmentDAO);
    }

    public EndpointGroup getAppointmentRoutes() {
        return () -> {
            get("/", appointmentController::getAll, Role.ADMIN);
            get("/{id}", appointmentController::getById, Role.ADMIN);
            get("/user/{username}", appointmentController::getByUserId, Role.USER, Role.ADMIN);
            post("/", appointmentController::create, Role.ADMIN, Role.USER);
            put("/{id}", appointmentController::update, Role.ADMIN, Role.USER);
            delete("/{id}", appointmentController::delete, Role.ADMIN);
        };
    }
}
