package ch.zhaw.peer2vehicle.service;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ch.zhaw.peer2vehicle.model.User;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarState;
import ch.zhaw.peer2vehicle.repository.UserRepository;
import ch.zhaw.peer2vehicle.repository.CarRepository;

@Service
public class CarService {
    @Autowired
    CarRepository carRepository;
    @Autowired
    UserRepository userRepository;

    public Optional<Car> rentCar(String carId, String userEmail) {
        Optional<Car> carToRent = carRepository.findById(carId);
        if (carToRent.isPresent()) {
            Car car = carToRent.get();
            if (car.getCarState() == CarState.AVAILABLE) {
                User userByEmail = userRepository.findFirstByEmail(userEmail);
                if (userByEmail != null) {
                    car.setCarState(CarState.UNAVAILABLE);
                    car.setUserId(userByEmail.getId());
                    carRepository.save(car);
                    return Optional.of(car);
                }
            }
        }
        return Optional.empty();
    }

    public Optional<Car> unrentCar(String carId, String userEmail) {
        Optional<Car> carToUnrent = carRepository.findById(carId);
        if (carToUnrent.isPresent()) {
            Car car = carToUnrent.get();
            if (car.getCarState() == CarState.UNAVAILABLE) {
                User userByEmail = userRepository.findFirstByEmail(userEmail);
                //System.out.println(userByEmail.getId());
                if (userByEmail != null && userByEmail.getId().equals(car.getUserId())) {
                    car.setCarState(CarState.AVAILABLE);
                    car.setUserId(null);
                    carRepository.save(car);
                    return Optional.of(car);
                }
            }
        }
        return Optional.empty();
    }
}