package ch.zhaw.peer2vehicle.model;

import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Getter
public class CarStateAggregation {
    private String id;
    private List<String> carIds;
    private String count;
}