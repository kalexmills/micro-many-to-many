package micro.tower.author.data;

import org.jdbi.v3.sqlobject.config.RegisterBeanMapper;
import org.jdbi.v3.sqlobject.customizer.BindBean;
import org.jdbi.v3.sqlobject.statement.SqlQuery;
import org.jdbi.v3.sqlobject.statement.SqlUpdate;

import java.util.UUID;

public interface AuthorsDao {

  @SqlQuery("SELECT * FROM authors WHERE id = :id")
  @RegisterBeanMapper(Author.class)
  Author findById(UUID id);

  @SqlUpdate("INSERT INTO authors (id, full_name) VALUES (:id, :fullName)")
  boolean insert(@BindBean Author author);

}
