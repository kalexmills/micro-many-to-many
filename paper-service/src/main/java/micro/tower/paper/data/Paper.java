package micro.tower.paper.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Paper {
  private UUID id;
  private String name;
  private UUID eventId;
}
