package shopipi.click.entity;

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

  @NotEmpty(message = "Category name is required")
  private String name;

  @Default
  private String parentId = null;

  private String thumb;

  private String parentName;

}
