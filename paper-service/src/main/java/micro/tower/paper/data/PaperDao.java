package micro.tower.paper.data;

import micro.tower.model.Paper;
import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.List;
import java.util.UUID;

public interface PaperDao {
  @SqlQuery("SELECT * FROM paper-svc.papers WHERE id = :id")
  @RegisterBeanMapper(Paper.class)
  Paper findById(UUID id);

  @SqlQuery("SELECT * FROM paper-svc.papers p " +
      "INNER JOIN paper-svc.paper_authors pa ON pa.paper_id = p.id " +
      "WHERE pa.author_id = :authorId")
  @RegisterBeanMapper(Paper.class)
  List<Paper> findByAuthorId(UUID authorId);

  @SqlUpdate("INSERT INTO paper-svc.papers (id, name, event_id) VALUES (:id, :name, :eventId)")
  boolean insert(@BindBean Paper conference);

  @SqlQuery("SELECT author_id FROM paper-svc.paper_authors WHERE paper_id = :paperId")
  List<UUID> findAuthorsFor(UUID paperId);

  @SqlUpdate("INSERT INTO paper-svc.papers (author_id, paper_id) VALUES (:authorId, :paperId)")
  boolean insertAuthorFor(UUID authorId, UUID paperId);
}
