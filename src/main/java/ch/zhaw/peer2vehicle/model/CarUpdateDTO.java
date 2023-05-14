package ch.zhaw.peer2vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class CarUpdateDTO {
    private CarBrand brand;
    private CarModel model;
    private Double year;
    private CarArea carArea;
    private Double price;
    private CarType carType;
    private CarTransmission carTransmission;
    private String description;
}