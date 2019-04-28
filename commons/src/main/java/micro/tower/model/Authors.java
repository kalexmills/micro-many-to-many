package micro.tower.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
public class Authors {
  @NonNull @NotEmpty
  private List<Author> authors = new ArrayList<>();

  public Authors(List<Author> authors) {
    this.authors.addAll(authors);
  }

}