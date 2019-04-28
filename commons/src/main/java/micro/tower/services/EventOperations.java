package micro.tower.services;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import io.reactivex.Single;
import micro.tower.model.Event;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Validated
public interface EventOperations {
  @Get(value="/{id}", produces = MediaType.APPLICATION_JSON)
  Single<Event> retrieve(@QueryValue UUID id);

  @Post(value="/", consumes = MediaType.APPLICATION_JSON)
  HttpResponse create(@Body @Valid Event conference);

  @Post(value="/{eventId}/attendance/{authorId}")
  Single<HttpResponse> createAttendance(@NotNull @QueryValue UUID eventId, @NotNull @QueryValue UUID authorId);

  @Delete(value="/{eventId}/attendance/{authorId}")
  HttpResponse deleteAttendance(@NotNull @QueryValue UUID eventId, @NotNull @QueryValue UUID authorId);
}
