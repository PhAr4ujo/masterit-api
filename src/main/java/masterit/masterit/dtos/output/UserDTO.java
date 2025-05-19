package masterit.masterit.dtos.output;

import masterit.masterit.enums.Role;
import lombok.Data;

import java.util.UUID;

@Data
public class UserDTO {
    private UUID id;
    private String name;
    private String email;
    private Role role;
}
