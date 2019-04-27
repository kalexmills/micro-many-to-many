package micro.tower.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.UUID;

@Data
public class Paper {
  private UUID id;
  private String name;
  @JsonProperty("event_id")
  private UUID eventId;
}
