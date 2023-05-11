package ch.zhaw.peer2vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CarCreateDTO {
    private CarBrand brand;
    private CarModel model;
    private Double year;
    private CarArea carArea;
    private Double price;
    private CarType carType;
    private CarTransmission carTransmission;
    private String description;
    private String ownerName;
    private String ownerEmail;
    private String ownerId;
}