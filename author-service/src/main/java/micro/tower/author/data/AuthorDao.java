package micro.tower.author.data;

import micro.tower.model.Author;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface AuthorDao {

  @SqlQuery("SELECT * FROM author_svc.authors WHERE id = :id")
  @RegisterBeanMapper(Author.class)
  Author findById(UUID id);

  @SqlUpdate("INSERT INTO author_svc.authors (id, full_name) VALUES (:id, :fullName)")
  boolean insert(@BindBean Author author);

  @SqlQuery("SELECT * from author_svc.authors au INNER JOIN attendance att ON att.author_id = au.id WHERE att.event_id = :eventId")
  @RegisterBeanMapper(Author.class)
  List<Author> findAllByEventId(UUID eventId);

  @SqlUpdate("INSERT INTO author_svc.attendance (event_id, author_id) VALUES (:eventId, :authorId) ON CONFLICT DO NOTHING")
  boolean insertAttendance(UUID authorId, UUID eventId);

  @SqlUpdate("DELETE FROM author_svc.attendance WHERE author_id = :authorId AND event_id = :eventId")
  boolean deleteAttendance(UUID authorId, UUID eventId);
}
