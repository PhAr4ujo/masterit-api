package masterit.masterit.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class NodeInputDTO {

    @NotNull
    private Float positionX;

    @NotNull
    private Float positionY;

    @NotBlank
    @Size(max = 255)
    private String label;

    @NotBlank
    @Size(max = 1000)
    private String description;

    @NotBlank
    private String status;

    @NotBlank
    private String priority;

    @NotNull
    private UUID roadmapId;
}
