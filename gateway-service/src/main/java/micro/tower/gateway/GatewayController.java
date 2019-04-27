package micro.tower.gateway;

import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.QueryValue;
import io.micronaut.http.client.exceptions.HttpClientResponseException;
import io.micronaut.http.uri.UriBuilder;
import micro.tower.gateway.service.AuthorClient;
import micro.tower.gateway.service.EventClient;

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
  public HttpResponse addAttendance(@QueryValue UUID eventId, @QueryValue UUID authorId) {
    // TODO: Non-blocking I/O everywhere... concurrently create both attendance records and rollback if either fails.
    boolean authorCreated = false;
    try {
      if (authorClient.createAttendance(eventId, authorId).status().getCode() < 400) {
        authorCreated = true;
        if (eventClient.createAttendance(eventId, authorId).status().getCode() < 400) {
          return HttpResponse.created(UriBuilder.of("/event/")
              .path(eventId.toString())
              .path("attendance")
              .path(authorId.toString())
              .build());
        } else {
          authorClient.deleteAttendance(eventId, authorId).status();
        }
      }
    } catch (HttpClientResponseException e) {
      if (authorCreated) {
        authorClient.deleteAttendance(eventId, authorId).status();
      }
      return HttpResponse.status(e.getStatus());
    }
    return HttpResponse.notFound();
  }
}
