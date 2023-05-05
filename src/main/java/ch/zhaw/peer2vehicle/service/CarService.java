package ch.zhaw.peer2vehicle.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.peer2vehicle.model.Mieter;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarState;
import ch.zhaw.peer2vehicle.repository.MieterRepository;
import ch.zhaw.peer2vehicle.repository.CarRepository;

@Service
public class CarService {
    @Autowired
    CarRepository carRepository;
    @Autowired
    MieterRepository mieterRepository;

    public Optional<Car> rentCar(String carId, String mieterEmail) {
        Optional<Car> carToRent = carRepository.findById(carId);
        if (carToRent.isPresent()) {
            Car car = carToRent.get();
            if (car.getCarState() == CarState.AVAILABLE) {
                Mieter mieterByEmail = mieterRepository.findFirstByEmail(mieterEmail);
                if (mieterByEmail != null) {
                    car.setCarState(CarState.UNAVAILABLE);
                    car.setMieterId(mieterByEmail.getId());
                    carRepository.save(car);
                    return Optional.of(car);
                }
            }
        }
        return Optional.empty();
    }

    /*
    public Optional<Car> unrentCar(String carId, String mieterEmail) {
        Optional<Car> carToUnrent = carRepository.findById(carId);
        if (carToUnrent.isPresent()) {
            Car car = carToUnrent.get();
            if (car.getCarState() == CarState.UNAVAILABLE) {
                Mieter mieterByEmail = mieterRepository.findFirstByEmail(mieterEmail);
                //System.out.println(mieterByEmail.getId());
                if (mieterByEmail != null && mieterByEmail.getId().equals(car.getMieterId())) {
                    car.setCarState(CarState.AVAILABLE);
                    carRepository.save(car);
                    return Optional.of(car);
                }
            }
        }
        return Optional.empty();
    }
    */
}