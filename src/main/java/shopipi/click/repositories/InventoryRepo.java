package shopipi.click.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import shopipi.click.entity.Inventory;

@Repository
public interface InventoryRepo extends MongoRepository<Inventory, String> {

}
