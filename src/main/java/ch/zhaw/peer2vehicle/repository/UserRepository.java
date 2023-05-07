package ch.zhaw.peer2vehicle.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import ch.zhaw.peer2vehicle.model.User;

public interface UserRepository extends MongoRepository<User, String> {
        User findFirstByEmail(String email);
}