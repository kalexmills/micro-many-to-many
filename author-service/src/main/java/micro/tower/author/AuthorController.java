package micro.tower.author;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import micro.tower.model.Author;
import micro.tower.author.data.AuthorDao;
import micro.tower.model.Authors;
import micro.tower.services.AuthorOperations;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;

import javax.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.UUID;

@Validated
@Controller(AuthorController.PREFIX)
public class AuthorController implements AuthorOperations {

  static final String PREFIX = "/author";

  private Jdbi jdbi;

  public AuthorController(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Author retrieve(@QueryValue UUID id) {
    return jdbi.withHandle(handle -> handle.attach(AuthorDao.class)
          .findById(id));
  }

  @Override
  public Single<Authors> retrieveByEvent(@QueryValue UUID eventId) {
    return Single.just(jdbi.withHandle(handle -> handle.attach(AuthorDao.class).findAllByEventId(eventId)))
      .map(Authors::new);
  }

  @Override
  public HttpResponse create(@Body @Valid Author author) {
    author.setId(UUID.randomUUID());
    if (jdbiInsertAuthor(author)) {
      return HttpResponse.created(URI.create(PREFIX + "/" + author.getId()));
    }
    return HttpResponse.serverError();
  }

  @Override
  public Single<HttpResponse> createAttendance(@QueryValue UUID eventId, @QueryValue UUID authorId) {
    return Single.defer(() -> Single.just(jdbiInsertAttendance(authorId, eventId)))
        .map(result -> (HttpResponse)HttpResponse.created(attendanceUri(eventId, authorId)))
        .onErrorReturn((e) -> HttpResponse.notFound());
  }

  @Override
  public HttpResponse deleteAttendance(@QueryValue UUID eventId, @QueryValue UUID authorId) {
    if (jdbiDeleteAttendance(authorId, eventId)) {
      return HttpResponse.ok();
    }
    return HttpResponse.notFound();
  }

  private boolean jdbiDeleteAttendance(UUID authorId, UUID eventId) {
    return jdbi.withHandle(handle -> handle.attach(AuthorDao.class).deleteAttendance(authorId, eventId));
  }

  private boolean jdbiInsertAttendance(UUID authorId, UUID eventId) {
    return jdbi.withHandle(handle -> handle.attach(AuthorDao.class).insertAttendance(authorId, eventId));
  }

  private boolean jdbiInsertAuthor(Author author) {
    return jdbi.withHandle(handle -> handle.attach(AuthorDao.class).insert(author));
  }

  private URI attendanceUri(UUID eventId, UUID authorId) {
    return UriBuilder.of(PREFIX)
        .path(authorId.toString())
        .path("attendance")
        .path(eventId.toString())
        .build();
  }
}
