package micro.tower.author.data;

import io.micronaut.context.annotation.Factory;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.sqlobject.SqlObjectPlugin;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.sql.DataSource;

@Factory
public class JdbiFactory {
  @Inject
  DataSource dataSource;

  @Singleton
  public Jdbi jdbi() {
    return Jdbi.create(dataSource)
        .installPlugin(new SqlObjectPlugin());
  }
}
