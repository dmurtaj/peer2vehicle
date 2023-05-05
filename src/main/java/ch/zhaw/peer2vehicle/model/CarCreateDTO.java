package ch.zhaw.peer2vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CarCreateDTO {
    private String brand;
    private String model;
    private Double price;
    private CarType carType;
    private CarTransmission carTransmission;
    private String description;   
}