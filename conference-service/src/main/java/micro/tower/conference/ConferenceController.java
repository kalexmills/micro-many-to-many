package micro.tower.conference;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import micro.tower.model.Conference;
import micro.tower.conference.data.ConferenceDao;
import micro.tower.services.ConferenceOperations;
import org.jdbi.v3.core.Jdbi;

import javax.validation.Valid;
import java.net.URI;
import java.util.UUID;

@Validated
@Controller(ConferenceController.PREFIX)
public class ConferenceController implements ConferenceOperations {

  static final String PREFIX = "/conference";

  private Jdbi jdbi;

  public ConferenceController(Jdbi jdbi) {
    this.jdbi = jdbi;
  }

  @Override
  public Conference retrieve(@QueryValue UUID id) {
    return jdbi.withHandle(handle -> handle.attach(ConferenceDao.class)
          .findById(id));
  }

  @Override
  public HttpResponse create(@Body @Valid Conference conference) {
    conference.setId(UUID.randomUUID());
    if (jdbi.withHandle(handle -> handle.attach(ConferenceDao.class).insert(conference))) {
      return HttpResponse.created(URI.create(PREFIX + "/" + conference.getId()));
    }
    return HttpResponse.serverError();
  }
}
