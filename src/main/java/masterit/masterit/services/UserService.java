package masterit.masterit.services;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import masterit.masterit.services.interfaces.IUserService;

@Service
@RequiredArgsConstructor
@Primary
public class UserService implements IUserService {
}
