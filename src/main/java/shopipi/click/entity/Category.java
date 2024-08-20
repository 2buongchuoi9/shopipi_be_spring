package shopipi.click.entity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Data;
import lombok.Builder.Default;

@Document(collection = "Categories")
@Data
@Builder
public class Category {
  @Id
  private String id;

  @NotEmpty(message = "Slug is required")
  private String slug;

  @NotEmpty(message = "Name is required")
  private String name;

  @Default
  private List<String> parentIds = new ArrayList<>();

  @NotEmpty(message = "Thumb is required")
  private String thumb;

}
