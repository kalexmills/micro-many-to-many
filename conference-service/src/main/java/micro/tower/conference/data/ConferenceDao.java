package micro.tower.conference.data;

import micro.tower.model.Conference;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface ConferenceDao {

  @SqlQuery("SELECT * FROM conference_svc.conferences WHERE id = :id")
  @RegisterBeanMapper(Conference.class)
  Conference findById(UUID id);

  @SqlQuery("SELECT * FROM conference_svc.conferences co " +
      "INNER JOIN attendance att ON att.conference_id = co.id " +
      "WHERE att.author_id = :authorId")
  @RegisterBeanMapper(Conference.class)
  List<Conference> findAllByAuthorId(UUID authorId);

  @SqlUpdate("INSERT INTO conference_svc.conferences (id, acronym) VALUES (:id, :acronym)")
  boolean insert(@BindBean Conference conference);
}
