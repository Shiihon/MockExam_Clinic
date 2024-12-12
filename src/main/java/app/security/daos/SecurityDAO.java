package app.security.daos;


import app.security.dtos.UserDTO;
import app.security.entities.Role;
import app.security.entities.User;
import app.security.exceptions.ApiException;
import app.security.exceptions.ValidationException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityNotFoundException;
import org.mindrot.jbcrypt.BCrypt;

import java.time.LocalDate;
import java.util.stream.Collectors;


/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public class SecurityDAO implements ISecurityDAO {

    private static ISecurityDAO instance;
    private static EntityManagerFactory emf;

    public SecurityDAO(EntityManagerFactory _emf) {
        emf = _emf;
    }

    private EntityManager getEntityManager() {
        return emf.createEntityManager();
    }

    @Override
    public UserDTO getVerifiedUser(String username, String password) throws ValidationException {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, username);
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + username); //RuntimeException
            user.getRoles().size(); // force roles to be fetched from db
            if (!user.verifyPassword(password))
                throw new ValidationException("Wrong password");
            return new UserDTO(user.getUsername(), user.getRoles().stream().map(r -> r.getRoleName()).collect(Collectors.toSet()));
        }
    }

    @Override
    public User createUser(String username, String password, String firstname, String lastname, LocalDate birthdate, String address, String phonenumber) {
        try (EntityManager em = getEntityManager()) {
            User userEntity = em.find(User.class, username);
            if (userEntity != null) {
                throw new EntityExistsException("User with username: " + username + " already exists");
            }

            userEntity = new User();
            userEntity.setUsername(username);
            userEntity.setPassword(BCrypt.hashpw(password, BCrypt.gensalt())); // Securely hash the password
            userEntity.setFirstname(firstname);
            userEntity.setLastname(lastname);
            userEntity.setBirthdate(birthdate);
            userEntity.setAddress(address);
            userEntity.setPhonenumber(phonenumber);

            em.getTransaction().begin();

            Role userRole = em.find(Role.class, "user");
            if (userRole == null) {
                userRole = new Role("user");
                em.persist(userRole);
            }

            userEntity.addRole(userRole);
            em.persist(userEntity);
            em.getTransaction().commit();

            return userEntity;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ApiException(400, e.getMessage());
        }
    }


    @Override
    public User addRole(UserDTO userDTO, String newRole) {
        try (EntityManager em = getEntityManager()) {
            User user = em.find(User.class, userDTO.getUsername());
            if (user == null)
                throw new EntityNotFoundException("No user found with username: " + userDTO.getUsername());
            em.getTransaction().begin();
            Role role = em.find(Role.class, newRole);
            if (role == null) {
                role = new Role(newRole);
                em.persist(role);
            }
            user.addRole(role);
            //em.merge(user);
            em.getTransaction().commit();
            return user;
        }
    }
       //OLD VERSION OF CREATE USER
    //            userEntity = new User(username, password);
//            em.getTransaction().begin();
//            Role userRole = em.find(Role.class, "user");
//            if (userRole == null)
//                userRole = new Role("user");
//            em.persist(userRole);
//            userEntity.addRole(userRole);
//            em.persist(userEntity);
//            em.getTransaction().commit();
}

