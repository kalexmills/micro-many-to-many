package micro.tower.gateway.service;

import io.micronaut.http.client.annotation.Client;
import micro.tower.services.EventOperations;

@Client("${microtower.event.host}")
public interface EventClient extends EventOperations {

}
