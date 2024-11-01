package app.routes;

import app.controllers.DoctorController;
import app.daos.DoctorDAO;
import app.security.enums.Role;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.*;

public class DoctorRoutes {
    private static DoctorController doctorController;
    private static DoctorDAO doctorDAO;

    public DoctorRoutes(EntityManagerFactory emf) {
        doctorDAO = new DoctorDAO(emf);
        doctorController = new DoctorController(doctorDAO);
    }

    public EndpointGroup getDoctorRoutes() {
        return () -> {
            get("/", doctorController::getAll, Role.USER, Role.ADMIN);
            get("/{id}", doctorController::getById, Role.USER, Role.ADMIN);
            get("/specialities/{speciality}", doctorController::getBySpeciality, Role.USER, Role.ADMIN);
            get("/birthdate/range", doctorController::getByBirthdayRange, Role.USER, Role.ADMIN);
            post("/", doctorController::create, Role.ADMIN);
            put("/{id}", doctorController::update, Role.ADMIN);
        };
    }
}
