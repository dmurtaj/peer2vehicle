package ch.zhaw.peer2vehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.peer2vehicle.model.Mieter;

public interface MieterRepository extends MongoRepository<Mieter, String> {
        Mieter findFirstByEmail(String email);
}