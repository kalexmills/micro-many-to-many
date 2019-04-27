package micro.tower.services;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Get;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.validation.Validated;
import micro.tower.model.Conference;

import javax.validation.Valid;
import java.util.UUID;

@Validated
public interface ConferenceOperations {
  @Get(value="/{id}", produces = MediaType.APPLICATION_JSON)
  Conference retrieve(@QueryValue UUID id);

  @Post(consumes = MediaType.APPLICATION_JSON)
  HttpResponse create(@Body @Valid Conference conference);
}
