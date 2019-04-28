package micro.tower.gateway;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.uri.UriBuilder;
import io.reactivex.Single;
import micro.tower.gateway.service.AuthorClient;
import micro.tower.gateway.service.EventClient;

import java.net.URI;
import java.util.UUID;

@Controller
public class GatewayController {

  AuthorClient authorClient;
  EventClient eventClient;

  public GatewayController(AuthorClient authorClient, EventClient eventClient) {
    this.authorClient = authorClient;
    this.eventClient = eventClient;
  }

  @Post("/event/{eventId}/attendance/{authorId}")
  public Single<HttpResponse> addAttendance(@QueryValue UUID eventId, @QueryValue UUID authorId) {
    return authorClient.createAttendance(eventId, authorId)
        .map(r -> true).onErrorReturn(e -> false)
        .zipWith(eventClient.createAttendance(eventId, authorId)
                .map(r -> true).onErrorReturn(e -> false),
        (authorSuccess, eventSuccess) -> {
          if (!authorSuccess || !eventSuccess) {
            if (authorSuccess) {
              authorClient.deleteAttendance(eventId, authorId);
            }
            if (eventSuccess) {
              eventClient.deleteAttendance(eventId, authorId);
            }
            return HttpResponse.notFound();
          }
          return HttpResponse.created(attendanceUri(eventId, authorId));
        });
  }

  private URI attendanceUri(UUID eventId, UUID authorId) {
    return UriBuilder.of("/event/")
        .path(eventId.toString())
        .path("attendance")
        .path(authorId.toString())
        .build();
  }
}
