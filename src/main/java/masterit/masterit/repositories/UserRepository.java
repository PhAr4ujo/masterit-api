package masterit.masterit.repositories;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import masterit.masterit.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByEmail(@NotBlank @Email String email);
}
