package ch.zhaw.peer2vehicle.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarState;

public interface CarRepository extends MongoRepository<Car, String> {
    // Einzelne Filter
    Page<Car> findByPriceLessThan(Double price, Pageable pageable);

    Page<Car> findByCarState(CarState carState, Pageable pageable);

    Page<Car> findByCarArea(CarArea carArea, Pageable pageable);

    // Multiple Filter
    Page<Car> findByCarStateAndCarArea(CarState carState, CarArea carArea, Pageable pageable);

    Page<Car> findByCarStateAndCarAreaAndPriceLessThan(CarState carState, CarArea carArea, Double price,
            Pageable pageable);

    Page<Car> findByCarAreaAndPriceLessThan(CarArea carArea, Double price, Pageable pageable);

    Page<Car> findByCarStateAndPriceLessThan(CarState carState, Double price, Pageable pageable);

    // List Filter für Meine Übersicht
    List<Car> findByOwnerEmail(String ownerEmail);

    List<Car> findByUserEmail(String userEmail);
}