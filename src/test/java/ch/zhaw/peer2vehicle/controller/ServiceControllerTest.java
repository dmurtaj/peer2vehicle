package ch.zhaw.peer2vehicle.controller;

import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarBrand;
import ch.zhaw.peer2vehicle.model.CarModel;
import ch.zhaw.peer2vehicle.model.CarTransmission;
import ch.zhaw.peer2vehicle.model.CarType;
import ch.zhaw.peer2vehicle.service.CarService;
import ch.zhaw.peer2vehicle.service.MailService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class ServiceControllerTest {

    @InjectMocks
    private ServiceController serviceController;

    @Mock
    private CarService carService;

    @Mock
    private MailService mailService;

    @Mock
    private Jwt jwt;

    @BeforeEach
    public void setup() throws Exception {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void rentToMe_Success() {
        // Arrange
        String ownerEmail = "test@test.com";
        String carId = "carId1";

        Car car = new Car();
        car.setId(carId);
        car.setBrand(CarBrand.Audi);
        car.setModel(CarModel.A3);
        car.setYear(2020.04);
        car.setCarArea(CarArea.Winterthur);
        car.setPrice(120.50);
        car.setCarType(CarType.Diesel);
        car.setCarTransmission(CarTransmission.Automat);
        car.setDescription("Test Beschreibung");
        car.setOwnerName("Mustermann");
        car.setOwnerEmail(ownerEmail);
        car.setOwnerId("1");

        when(jwt.getClaimAsString("email")).thenReturn(ownerEmail);
        when(carService.rentCar(anyString(), anyString())).thenReturn(Optional.of(car));

        // Act
        ResponseEntity<Car> response = serviceController.rentToMe(carId, jwt);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
    }

    @Test
    public void rentToMe_Failure() {
        // Arrange
        String userEmail = "test@test.com";
        String carId = "carId1";
        when(jwt.getClaimAsString("email")).thenReturn(userEmail);
        when(carService.rentCar(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Car> response = serviceController.rentToMe(carId, jwt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }

    @Test
    public void unrentToMe_Success() {
        // Arrange
        String ownerEmail = "test@test.com";
        String carId = "carId1";

        Car car = new Car();
        car.setId(carId);
        car.setBrand(CarBrand.Audi);
        car.setModel(CarModel.A3);
        car.setYear(2020.04);
        car.setCarArea(CarArea.Winterthur);
        car.setPrice(120.50);
        car.setCarType(CarType.Diesel);
        car.setCarTransmission(CarTransmission.Automat);
        car.setDescription("Test Beschreibung");
        car.setOwnerName("Mustermann");
        car.setOwnerEmail(ownerEmail);
        car.setOwnerId("1");

        when(jwt.getClaimAsString("email")).thenReturn(ownerEmail);
        when(carService.unrentCar(anyString(), anyString())).thenReturn(Optional.of(car));

        // Act
        ResponseEntity<Car> response = serviceController.unrentToMe(carId, jwt);

        // Assert
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(car, response.getBody());
    }

    @Test
    public void unrentToMe_Failure() {
        // Arrange
        String userEmail = "test@test.com";
        String carId = "carId1";
        when(jwt.getClaimAsString("email")).thenReturn(userEmail);
        when(carService.unrentCar(anyString(), anyString())).thenReturn(Optional.empty());

        // Act
        ResponseEntity<Car> response = serviceController.unrentToMe(carId, jwt);

        // Assert
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }
}
