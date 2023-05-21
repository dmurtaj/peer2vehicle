package ch.zhaw.peer2vehicle.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarCreateDTO;
import ch.zhaw.peer2vehicle.model.CarState;
import ch.zhaw.peer2vehicle.model.CarUpdateDTO;
import ch.zhaw.peer2vehicle.repository.CarRepository;

import org.springframework.security.access.annotation.Secured; //Mit @Secured wird der Request auf die entsprechende Rolle beschränkt
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class CarController {
    @Autowired
    CarRepository carRepository;

    @PostMapping("/car")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Car> createCar(
            @RequestBody CarCreateDTO cDTO) {
        Car cDAO = new Car(cDTO.getBrand(), cDTO.getModel(), cDTO.getYear(), cDTO.getCarArea(), cDTO.getPrice(),
                cDTO.getCarType(),
                cDTO.getCarTransmission(), cDTO.getDescription(), cDTO.getOwnerName(), cDTO.getOwnerEmail(),
                cDTO.getOwnerId());
        Car c = carRepository.save(cDAO);
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }

    @GetMapping("/mycars")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<Car>> getMyCars(@AuthenticationPrincipal Jwt jwt) {
        String actualUserEmail = jwt.getClaimAsString("email"); // Get the current user's Email from the JWT
        List<Car> myOwnedCars = carRepository.findByOwnerEmail(actualUserEmail);
        List<Car> myRentedCars = carRepository.findByUserEmail(actualUserEmail);
        List<Car> myCars = Stream.concat(myOwnedCars.stream(), myRentedCars.stream()).collect(Collectors.toList());
        return new ResponseEntity<>(myCars, HttpStatus.OK);
    }

    @GetMapping("/car/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Car> getCarById(@PathVariable String id) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isPresent()) {
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/car")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Page<Car>> getAllCars(
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) CarState state,
            @RequestParam(required = false) CarArea carArea,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize) {
        Page<Car> allCars;
        if (price == null && state == null && carArea == null) {
            allCars = carRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        } else {
            // Filter Price, State, Area
            if (price != null && state != null && carArea != null) {
                allCars = carRepository.findByCarStateAndCarAreaAndPriceLessThan(state, carArea, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }

            // Filter State, Area
            else if (price == null && state != null && carArea != null) {
                allCars = carRepository.findByCarStateAndCarArea(state, carArea,
                        PageRequest.of(pageNumber - 1, pageSize));
            }

            // Filter Price, Area
            else if (price != null && state == null && carArea != null) {
                allCars = carRepository.findByCarAreaAndPriceLessThan(carArea, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Price, State
            else if (price != null && state != null && carArea == null) {
                allCars = carRepository.findByCarStateAndPriceLessThan(state, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Price
            else if (price != null && state == null && carArea == null) {
                allCars = carRepository.findByPriceLessThan(price, PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter State
            else if (price == null && state != null && carArea == null) {
                allCars = carRepository.findByCarState(state, PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Area
            else {
                allCars = carRepository.findByCarArea(carArea, PageRequest.of(pageNumber - 1, pageSize));
            }
        }
        return new ResponseEntity<>(allCars, HttpStatus.OK);
    }

    @PutMapping("/car/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Car> updateCar(@PathVariable String id, @RequestBody CarUpdateDTO carUpdateDTO,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();
            if (!car.getOwnerEmail().equals(userEmail)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }
            car.setBrand(carUpdateDTO.getBrand());
            car.setModel(carUpdateDTO.getModel());
            car.setYear(carUpdateDTO.getYear());
            car.setCarArea(carUpdateDTO.getCarArea());
            car.setPrice(carUpdateDTO.getPrice());
            car.setCarType(carUpdateDTO.getCarType());
            car.setCarTransmission(carUpdateDTO.getCarTransmission());
            car.setDescription(carUpdateDTO.getDescription());

            Car updatedCar = carRepository.save(car);
            return new ResponseEntity<>(updatedCar, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("/car/{id}/setAsAvailable")
    @Secured("ROLE_admin")
    public ResponseEntity<Car> setCarAsAvailable(@PathVariable String id) {
        Optional<Car> optionalCar = carRepository.findById(id);
        if (optionalCar.isPresent()) {
            Car car = optionalCar.get();

            // Check if current car state is UNAVAILABLE
            if (!car.getCarState().equals(CarState.Besetzt)) {
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }

            car.setCarState(CarState.Verfügbar);
            car.setUserName(null);
            car.setUserEmail(null);
            car.setUserId(null);

            Car updatedCar = carRepository.save(car);
            return new ResponseEntity<>(updatedCar, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/car/{id}")
    @Secured("ROLE_admin")
    public ResponseEntity<String> deleteCarById(@PathVariable String id) {
        Optional<Car> carToDelete = carRepository.findById(id);
        if (carToDelete.isPresent()) {
            carRepository.deleteById(id);
            return ResponseEntity.status(HttpStatus.OK).body("Car with ID " + id + " has been deleted.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID " + id + " not found.");
        }
    }

    @DeleteMapping("/me/car/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteMyCarById(@PathVariable String id, @AuthenticationPrincipal Jwt jwt) {
        String actualUserEmail = jwt.getClaimAsString("email"); // Get the current user's Email from the JWT
        Optional<Car> carToDelete = carRepository.findById(id);
        if (carToDelete.isPresent()) {
            if (carToDelete.get().getOwnerEmail().equals(actualUserEmail)) {
                carRepository.deleteById(id);
                return ResponseEntity.status(HttpStatus.OK).body("Car with ID " + id + " has been deleted.");
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("You are not authorized to delete this car.");
            }
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Car with ID " + id + " not found.");
        }
    }
}