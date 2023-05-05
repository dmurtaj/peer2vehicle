package ch.zhaw.peer2vehicle.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.annotation.Nonnull;
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
    private String brand;
    @NonNull
    private String model;
    @NonNull
    private Double price;
    @NonNull
    private CarType carType;
    @Nonnull
    private CarTransmission carTransmission;
    @NonNull
    private String description;
    private CarState carState = CarState.AVAILABLE;
    private String MieterId;
}