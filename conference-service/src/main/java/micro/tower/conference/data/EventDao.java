package micro.tower.conference.data;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface EventDao {
  @SqlQuery("SELECT * FROM events WHERE id = :id")
  @RegisterBeanMapper(Event.class)
  Event findById(UUID id);

  @SqlQuery("SELECT * FROM events WHERE conference_id = :conferenceId")
  @RegisterBeanMapper(Event.class)
  List<Event> findByConferenceId(UUID conferenceId);

  @SqlUpdate("INSERT INTO events (id, conference_id, seq) VALUES (:id, :conferenceId, seq)")
  boolean insert(@BindBean Event event);

}
