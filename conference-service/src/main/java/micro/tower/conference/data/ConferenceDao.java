package micro.tower.conference.data;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface ConferenceDao {

  @SqlQuery("SELECT * FROM conferences WHERE id = :id")
  @RegisterBeanMapper(Conference.class)
  Conference findById(UUID id);

  @SqlQuery("SELECT * FROM conferences co " +
      "INNER JOIN attendance att ON att.conference_id = co.id " +
      "WHERE att.author_id = :authorId")
  @RegisterBeanMapper(Conference.class)
  List<Conference> findByAttendingAuthorId(UUID authorId);

  @SqlUpdate("INSERT INTO conferences (id, acronym) VALUES (:id, :acronym)")
  boolean insert(@BindBean Conference conference);

  @SqlQuery("SELECT author_id FROM attendance where conference_id = :conferenceId")
  List<UUID> findAttendingAuthorsFor(UUID conferenceId);

  @SqlUpdate("INSERT INTO attendance (conference_id, author_id) VALUES (:conferenceId, :authorId)")
  boolean insertAttendance(UUID authorId, UUID conferenceId);

}
