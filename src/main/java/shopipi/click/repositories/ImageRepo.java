package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Image;

@Repository
public interface ImageRepo extends MongoRepository<Image, String> {

}
