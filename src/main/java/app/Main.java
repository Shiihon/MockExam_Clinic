package app;

import app.config.AppConfig;
import app.config.HibernateConfig;
import jakarta.persistence.EntityManagerFactory;

public class Main {
    private static EntityManagerFactory emf;
//comment
    public static void main(String[] args) {

        emf = HibernateConfig.getEntityManagerFactory("clinic");
        AppConfig.startServer(emf);
    }
}