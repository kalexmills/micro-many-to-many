package micro.tower.gateway.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import micro.tower.model.Author;
import micro.tower.model.Event;

import java.util.List;

@Data
@AllArgsConstructor
public class EventAttendance {
  public Event event;
  public List<Author> attendees;
}
