package ch.zhaw.peer2vehicle.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PutMapping;
//import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.RequestParam;
import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarBrand;
import ch.zhaw.peer2vehicle.model.CarModel;
//import ch.zhaw.peer2vehicle.model.CarStateChangeDTO;
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

    @PreAuthorize("isAuthenticated()")
    private void sendRentedEmail(String to, CarBrand brand, CarModel model, CarArea carArea) {
        String subject = String.format("Miete von %s %s gestartet", brand, model);
        String message = String.format("Lieber Kunde\n\nDas Fahrzeug %s %s aus %s wurde soeben für Dich gemietet.\n\nWir wünschen eine gute Fahrt!\n\nLiebe Grüsse\nDein P2V-Team", brand, model, carArea);
        Mail mail = new Mail(to, subject, message);
        mailService.sendMail(mail);
    }

    @PreAuthorize("isAuthenticated()")
    private void sendUnrentedEmail(String to, CarBrand brand, CarModel model, CarArea carArea) {
        String subject = String.format("Miete von %s %s beendet", brand, model);
        String message = String.format("Lieber Kunde\n\nDie Miete für das Fahrzeug %s %s aus %s wurde soeben beendet.\n\nWir freuen uns wieder von Dir zu hören!\n\nLiebe Grüsse\nDein P2V-Team", brand, model, carArea);
        Mail mail = new Mail(to, subject, message);
        mailService.sendMail(mail);
    }

    /*
    @PutMapping("/rentcar")
    @Secured("ROLE_admin")
    public ResponseEntity<Car> rentCar(@RequestBody CarStateChangeDTO changes) {
        String userEmail = changes.getUserEmail();
        String carId = changes.getCarId();
        Optional<Car> car = carService.rentCar(carId, userEmail);
        if (car.isPresent()) {
            sendRentedEmail(userEmail, car.get().getBrand(), car.get().getModel());
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
            sendUnrentedEmail(userEmail, car.get().getBrand(), car.get().getModel());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
    */

    @PutMapping("/me/rentcar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Car> rentToMe(@RequestParam String carId,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Optional<Car> car = carService.rentCar(carId, userEmail);
        if (car.isPresent()) {
            sendRentedEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarArea());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PutMapping("/me/unrentcar")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Car> unrentToMe(@RequestParam String carId,
            @AuthenticationPrincipal Jwt jwt) {
        String userEmail = jwt.getClaimAsString("email");
        Optional<Car> car = carService.unrentCar(carId, userEmail);
        if (car.isPresent()) {
            sendUnrentedEmail(userEmail, car.get().getBrand(), car.get().getModel(), car.get().getCarArea());
            return new ResponseEntity<>(car.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}