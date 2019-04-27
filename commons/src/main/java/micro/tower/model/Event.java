package micro.tower.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.validation.constraints.Min;
import java.util.UUID;

@Data
public class Event {
  private UUID id;
  @JsonProperty("conference_id")
  private UUID conferenceId;
  @Min(1)
  private int seq;
}
