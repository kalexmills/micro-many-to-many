package micro.tower.conference;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.http.uri.UriBuilder;
import io.micronaut.validation.Validated;
import micro.tower.model.Event;
import micro.tower.conference.data.EventDao;
import micro.tower.services.EventOperations;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.JdbiException;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.net.URI;
import java.util.UUID;

@Validated
@Controller(EventController.PREFIX)
public class EventController implements EventOperations {

  public static final String PREFIX = "/event";

  private Jdbi jdbi;

  public EventController(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Event retrieve(@QueryValue UUID id) {
    return jdbi.withHandle(handle -> handle.attach(EventDao.class)
          .findById(id));
  }

  @Override
  public HttpResponse create(@Body @Valid Event conference) {
    conference.setId(UUID.randomUUID());
    if (jdbi.withHandle(handle -> handle.attach(EventDao.class).insert(conference))) {
      return HttpResponse.created(URI.create(PREFIX + "/" + conference.getId()));
    }
    return HttpResponse.serverError();
  }

  @Override
  public HttpResponse createAttendance(@NotNull UUID eventId, @NotNull UUID authorId) {
    try {
      URI location = UriBuilder.of(PREFIX)
          .path(authorId.toString())
          .path("attendance")
          .path(eventId.toString())
          .build();

      jdbi.withHandle(handle -> handle.attach(EventDao.class).insertAttendance(eventId, authorId));
      return HttpResponse.created(location);
    } catch (JdbiException e) {
      return HttpResponse.notFound();
    }
  }

  @Override
  public HttpResponse deleteAttendance(@NotNull UUID eventId, @NotNull UUID authorId) {
    return null;
  }


}
