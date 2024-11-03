package app.routes;

import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {

    private static DoctorRoutes doctorRoutes;

    public Routes(EntityManagerFactory emf) {
        doctorRoutes = new DoctorRoutes(emf);
    }

    public EndpointGroup getApiRoutes() {
        return () -> {
            path("/doctors", doctorRoutes.getDoctorRoutes());
        };
    }
}

