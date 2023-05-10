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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarCreateDTO;
import ch.zhaw.peer2vehicle.model.CarStateAggregation;
import ch.zhaw.peer2vehicle.model.CarTransmission;
import ch.zhaw.peer2vehicle.model.CarType;
import ch.zhaw.peer2vehicle.repository.CarRepository;

import org.springframework.security.access.annotation.Secured; //Mit @Secured wird der Request auf die entsprechende Rolle beschränkt
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;

@RestController
@RequestMapping("/api")
public class CarController {
    @Autowired
    CarRepository carRepository;

    @PostMapping("/car")
    public ResponseEntity<Car> createCar(
            @RequestBody CarCreateDTO cDTO) {
        Car cDAO = new Car(cDTO.getBrand(), cDTO.getModel(), cDTO.getPrice(), cDTO.getCarType(),
                cDTO.getCarTransmission(), cDTO.getDescription(), cDTO.getOwnerName(), cDTO.getOwnerEmail(),
                cDTO.getOwnerId());
        Car c = carRepository.save(cDAO);
        return new ResponseEntity<>(c, HttpStatus.CREATED);
    }

    @GetMapping("/mycars")
    public ResponseEntity<List<Car>> getMyCars(@AuthenticationPrincipal Jwt jwt) {
        String actualUserEmail = jwt.getClaimAsString("email"); // Get the current user's Email from the JWT
        List<Car> myOwnedCars = carRepository.findByOwnerEmail(actualUserEmail);
        List<Car> myRentedCars = carRepository.findByUserEmail(actualUserEmail);
        List<Car> myCars = Stream.concat(myOwnedCars.stream(), myRentedCars.stream()).collect(Collectors.toList());
        return new ResponseEntity<>(myCars, HttpStatus.OK);
    }

    @GetMapping("/car/{id}")
    public ResponseEntity<Car> getCarById(@PathVariable String id) {
        Optional<Car> car = carRepository.findById(id);
        if (car.isPresent()) {
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/car")
    public ResponseEntity<Page<Car>> getAllCars(
            @RequestParam(required = false) Double price,
            @RequestParam(required = false) CarType type,
            @RequestParam(required = false) CarTransmission transmission,
            @RequestParam(required = false, defaultValue = "1") Integer pageNumber,
            @RequestParam(required = false, defaultValue = "2") Integer pageSize) {
        Page<Car> allCars;
        if (price == null && type == null && transmission == null) {
            allCars = carRepository.findAll(PageRequest.of(pageNumber - 1, pageSize));
        } else {
            // Filter Price, Type, Transmission
            if (price != null && type != null && transmission != null) {
                allCars = carRepository.findByCarTypeAndCarTransmissionAndPriceLessThan(type, transmission, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }

            // Filter Type, Transmission
            else if (price == null && type != null && transmission != null) {
                allCars = carRepository.findByCarTypeAndCarTransmission(type, transmission,
                        PageRequest.of(pageNumber - 1, pageSize));
            }

            // Filter Price, Transmission
            else if (price != null && type == null && transmission != null) {
                allCars = carRepository.findByCarTransmissionAndPriceLessThan(transmission, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Price, Type
            else if (price != null && type != null && transmission == null) {
                allCars = carRepository.findByCarTypeAndPriceLessThan(type, price,
                        PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Price
            else if (price != null && type == null && transmission == null) {
                allCars = carRepository.findByPriceLessThan(price, PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Type
            else if (price == null && type != null && transmission == null) {
                allCars = carRepository.findByCarType(type, PageRequest.of(pageNumber - 1, pageSize));
            }
            // Filter Transmission
            else {
                allCars = carRepository.findByCarTransmission(transmission, PageRequest.of(pageNumber - 1, pageSize));
            }
        }
        return new ResponseEntity<>(allCars, HttpStatus.OK);
    }

    @GetMapping("/car/aggregation/state")
    public List<CarStateAggregation> getCarStateAggregation() {
        return carRepository.getCarStateAggregation();
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

    /*
     * @DeleteMapping("/car")
     * 
     * @Secured("ROLE_admin")
     * public ResponseEntity<String> deleteAllCars() {
     * carRepository.deleteAll();
     * return ResponseEntity.status(HttpStatus.OK).body("DELETED");
     * }
     */
    /*
     * Die End-to-End Tests sollen reproduzierbar sein, d.h., der Zustand des
     * Systems zu Beginn der Tests sollte immer gleich sein. Dies ist aber nicht
     * zwingend der Fall, weil
     * du deine Daten in einer Datenbank gespeichert hast.
     * Darum erstellst du als erstes einen zusätzlichen Endpoint, der alle
     * deine Cars in der Datenbank löscht. Diesen Endpoint kannst du nachher zu
     * Beginn deiner Tests aufrufen und somit sicherstellen, dass der Zustand der
     * Datenbank zu Beginn immer gleich ist.
     * Hinweis: Wenn du die bestehenden Daten in deiner Datenbank nicht verlieren
     * möchtest, kannst
     * in src/main/resources/application.properties statt der bestehenden Datenbank
     * den Namen einer Testdatenbank angeben.
     */
}