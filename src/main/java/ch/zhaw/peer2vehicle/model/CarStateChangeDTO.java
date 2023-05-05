package ch.zhaw.peer2vehicle.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CarStateChangeDTO {
    private String carId;
    private String MieterEmail;    
}
