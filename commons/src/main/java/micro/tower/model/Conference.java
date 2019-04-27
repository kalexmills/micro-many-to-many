package micro.tower.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class Conference {
  private UUID id;
  @NotNull @NotEmpty
  private String acronym;
}
