package masterit.masterit.services.interfaces;

import masterit.masterit.entities.User;

import java.util.Optional;

public interface IUserService {
    Optional<User> findByEmail(String email);
}
