package ch.zhaw.peer2vehicle.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Document("car")
public class Car {
    @Id
    private String id;
    @NonNull
    private CarBrand brand;
    @NonNull
    private CarModel model;
    @NonNull
    private Double year;
    @NonNull
    private Double price;
    @NonNull
    private CarType carType;
    @NonNull
    private CarTransmission carTransmission;
    @NonNull
    private String description;
    private CarState carState = CarState.AVAILABLE;
    @NonNull
    private String ownerName;
    @NonNull
    private String ownerEmail;
    @NonNull
    private String ownerId;
    private String userName;
    private String userEmail;
    private String userId;
}