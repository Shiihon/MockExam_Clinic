package app.routes;

import app.security.routes.SecurityRoutes;
import io.javalin.apibuilder.EndpointGroup;
import jakarta.persistence.EntityManagerFactory;

import static io.javalin.apibuilder.ApiBuilder.path;

public class Routes {
    private SecurityRoutes securityRoutes;
    private DoctorRoutes doctorRoutes;

    public Routes(EntityManagerFactory emf) {
        doctorRoutes = new DoctorRoutes(emf);
        securityRoutes = new SecurityRoutes(emf);
    }

    public EndpointGroup getApiRoutes() {
        return () -> {
            path("/doctors", doctorRoutes.getDoctorRoutes());
            path("/", securityRoutes.getSecurityRoutes());
        };
    }
}

