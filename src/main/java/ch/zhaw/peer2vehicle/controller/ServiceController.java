package ch.zhaw.peer2vehicle.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarStateChangeDTO;
import ch.zhaw.peer2vehicle.model.CarState;
import ch.zhaw.peer2vehicle.model.Mail;
import ch.zhaw.peer2vehicle.service.CarService;
import ch.zhaw.peer2vehicle.service.MailService;

@RestController
@RequestMapping("/api/service")
public class ServiceController {

    @Autowired
    CarService carService;

    @Autowired
    MailService mailService;

    private void sendCarStatusEmail(String to, String brand, String model, CarState carState) {
        String subject = String.format("'%s' '%s' marked as '%s'", brand, model, carState.name());
        String message = String.format("Hi, you just marked the car '%s' '%s' as '%s'", brand, model, carState.name());
        Mail mail = new Mail(to, subject, message);
        mailService.sendMail(mail);
    }

    @PutMapping("/rentcar")
    @Secured("ROLE_admin")
    public ResponseEntity<Car> rentCar(@RequestBody CarStateChangeDTO changes) {
        String userEmail = changes.getUserEmail();
        String carId = changes.getCarId();
        Optional<Car> car = carService.rentCar(carId, userEmail);
        if (car.isPresent()) {
            sendCarStatusEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarState());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/unrentcar")
    @Secured("ROLE_admin")
    public ResponseEntity<Car> unrentCar(@RequestBody CarStateChangeDTO changes) {
        String userEmail = changes.getUserEmail();
        String carId = changes.getCarId();
        Optional<Car> car = carService.unrentCar(carId, userEmail);
        if (car.isPresent()) {
            sendCarStatusEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarState());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/me/rentcar")
    public ResponseEntity<Car> rentToMe(@RequestParam String carId,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Optional<Car> car = carService.rentCar(carId, userEmail);
        if (car.isPresent()) {
            sendCarStatusEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarState());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/me/unrentcar")
    public ResponseEntity<Car> unrentToMe(@RequestParam String carId,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Optional<Car> car = carService.unrentCar(carId, userEmail);
        if (car.isPresent()) {
            sendCarStatusEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarState());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}