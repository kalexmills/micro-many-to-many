package micro.tower.services;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.micronaut.validation.Validated;
import micro.tower.model.Author;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Validated
public interface AuthorOperations {
  @Get(value="/{id}", produces = MediaType.APPLICATION_JSON)
  Author retrieve(@NotNull UUID id);

  @Post(value="/", consumes = MediaType.APPLICATION_JSON)
  HttpResponse create(@Body @Valid Author author);

  @Post(value="/{authorId}/attendance/{eventId}")
  HttpResponse createAttendance(@NotNull @QueryValue UUID eventId, @NotNull @QueryValue UUID authorId);

  @Delete(value="/{authorId}/attendance/{eventId}")
  HttpResponse deleteAttendance(@NotNull @QueryValue UUID eventId, @NotNull @QueryValue UUID authorId);
}
