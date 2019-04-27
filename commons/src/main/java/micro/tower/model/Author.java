package micro.tower.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class Author {
  private UUID id;
  @NotNull @NotEmpty
  @JsonProperty("full_name")
  private String fullName;
}
