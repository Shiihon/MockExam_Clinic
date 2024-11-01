package app.security.daos;

import app.security.dtos.UserDTO;
import app.security.entities.User;
import app.security.exceptions.ValidationException;

public interface ISecurityDAO {
    UserDTO getVerifiedUser(String username, String password) throws ValidationException;
    User createUser(String username, String password);
    User addRole(UserDTO user, String newRole);
}
