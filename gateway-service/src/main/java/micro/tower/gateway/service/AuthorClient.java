package micro.tower.gateway.service;

import io.micronaut.http.client.annotation.Client;
import micro.tower.services.AuthorOperations;

@Client("${microtower.author.host}")
public interface AuthorClient extends AuthorOperations {

}
