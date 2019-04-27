package micro.tower.conference.data;

import micro.tower.model.Event;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface EventDao {
  @SqlQuery("SELECT * FROM conference_svc.events WHERE id = :id")
  @RegisterBeanMapper(Event.class)
  Event findById(UUID id);

  @SqlQuery("SELECT * FROM conference_svc.events WHERE conference_id = :conferenceId")
  @RegisterBeanMapper(Event.class)
  List<Event> findByConferenceId(UUID conferenceId);

  @SqlUpdate("INSERT INTO conference_svc.events (id, conference_id, seq) VALUES (:id, :conferenceId, :seq)")
  boolean insert(@BindBean Event event);

  @SqlUpdate("INSERT INTO conference_svc.attendance (event_id, author_id) VALUES (:eventId, :authorId) ON CONFLICT DO NOTHING")
  boolean insertAttendance(UUID eventId, UUID authorId);

  @SqlUpdate("DELETE FROM conference_svc.attendance WHERE author_id = :authorId AND event_id = :eventId")
  boolean deleteAttendance(UUID eventId, UUID authorId);
}
