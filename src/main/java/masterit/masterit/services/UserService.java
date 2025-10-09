package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import masterit.masterit.entities.User;
import masterit.masterit.repositories.UserRepository;
import masterit.masterit.services.interfaces.IUserService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Primary
public class UserService implements IUserService {

    private final UserRepository userRepository;

    @Override
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }
}
