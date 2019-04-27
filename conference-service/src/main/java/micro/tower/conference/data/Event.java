package micro.tower.conference.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Event {
  private UUID id;
  private UUID conferenceId;
  private int seq;
}
