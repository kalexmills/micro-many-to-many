package micro.tower.conference.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Conference {
  private UUID id;
  private String acronym;
}
