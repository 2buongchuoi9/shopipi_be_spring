package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Sale;

@Repository
public interface SaleRepo extends MongoRepository<Sale, String> {

}
