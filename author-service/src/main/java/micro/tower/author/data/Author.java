package micro.tower.author.data;

import lombok.Data;

import java.util.UUID;

@Data
public class Author {
  private UUID id;
  private String fullName;
}
