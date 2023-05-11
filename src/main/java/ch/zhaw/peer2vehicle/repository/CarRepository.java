package ch.zhaw.peer2vehicle.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarStateAggregation;
import ch.zhaw.peer2vehicle.model.CarType;

public interface CarRepository extends MongoRepository<Car, String> {
    // Einzelne Filter
    Page<Car> findByPriceLessThan(Double price, Pageable pageable);

    Page<Car> findByCarType(CarType carType, Pageable pageable);

    Page<Car> findByCarArea(CarArea carArea, Pageable pageable);

    // Multiple Filter

    Page<Car> findByCarTypeAndCarArea(CarType carType, CarArea carArea, Pageable pageable);

    Page<Car> findByCarTypeAndCarAreaAndPriceLessThan(CarType carType, CarArea carArea, Double price, Pageable pageable);

    Page<Car> findByCarAreaAndPriceLessThan(CarArea carArea, Double price, Pageable pageable);

    Page<Car> findByCarTypeAndPriceLessThan(CarType carType, Double price, Pageable pageable);

    // List Filter für Meine Übersicht

    List<Car> findByOwnerEmail(String ownerEmail);

    List<Car> findByUserEmail(String userEmail);

    @Aggregation("{$group:{_id:'$carState',carIds:{$push:'$_id',},count:{$count:{}}}}")
    List<CarStateAggregation> getCarStateAggregation();
}