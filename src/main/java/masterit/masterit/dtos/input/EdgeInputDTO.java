package masterit.masterit.dtos.input;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.UUID;

@Data
public class EdgeInputDTO {

    @NotNull
    private UUID roadmapId;

    @NotNull
    private UUID sourceId;

    @NotNull
    private UUID targetId;

    @NotNull
    private Boolean animated;

    @NotBlank
    @Size(max = 50)
    private String type;

    @Size(max = 50)
    private String markerEndType;

    private Integer markerEndWidth;

    private Integer markerEndHeight;
}
