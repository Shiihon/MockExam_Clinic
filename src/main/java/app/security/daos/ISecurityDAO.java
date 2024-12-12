package app.security.daos;

import app.security.dtos.UserDTO;
import app.security.entities.User;
import app.security.exceptions.ValidationException;

import java.time.LocalDate;

public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    User createUser(String username, String password, String firstname, String lastname, LocalDate birthdate, String address, String phonenumber);
    User addRole(UserDTO user, String newRole);
}
