package ch.zhaw.peer2vehicle.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

import ch.zhaw.peer2vehicle.model.Car;
import ch.zhaw.peer2vehicle.model.CarArea;
import ch.zhaw.peer2vehicle.model.CarBrand;
import ch.zhaw.peer2vehicle.model.CarModel;
import ch.zhaw.peer2vehicle.model.CarTransmission;
import ch.zhaw.peer2vehicle.model.CarType;
import ch.zhaw.peer2vehicle.repository.CarRepository;
import ch.zhaw.peer2vehicle.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class CarControllerTest {
    
    @Autowired
    private MockMvc mvc;

    @Autowired
    CarRepository carRepository;

    private static final String TEST_STRING = "TEST-abc...xyz";
    private static ObjectMapper mapper = new ObjectMapper();
    private static ObjectWriter ow = mapper.writer().withDefaultPrettyPrinter();

    @Test
    @Order(1)
    @WithMockUser
    public void testCreateCar() throws Exception {
        // create a test car and convert to Json
        Car car = new Car();
        car.setBrand(CarBrand.Audi);
        car.setModel(CarModel.A3);
        car.setYear(2020.04);
        car.setCarArea(CarArea.Winterthur);
        car.setPrice(120.50);
        car.setCarType(CarType.Diesel);
        car.setCarTransmission(CarTransmission.Automat);
        car.setDescription(TEST_STRING);
        car.setOwnerName("Mustermann");
        car.setOwnerEmail("muster@mail.com");
        car.setOwnerId("1");
        var jsonBody = ow.writeValueAsString(car);

        // POST Json to service with authorization header
        mvc.perform(post("/api/car")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonBody)
        .header(HttpHeaders.AUTHORIZATION, "Bearer token"))
        .andDo(print())
        .andExpect(status().isCreated())
        .andReturn();
    }

    @Test
    @Order(2)
    @WithMockUser
    public void testGetAllCar() throws Exception {
        var json = getAllCars();
        
        // assert list of cars is not empty and result contains test string
        assertFalse(json.isEmpty());
        assertTrue(json.contains(TEST_STRING));
    }

    @Test
    @Order(3)
    @WithMockUser
    public void testDeleteCars() throws Exception {
        // analyse json response and delete all test data cars
        var json = getAllCars();
        JsonNode jsonNode = mapper.readTree(json);
        var content = jsonNode.get("content");
        for (var x : content) {
            var id = x.get("id");
            var description = x.get("description");
            if (description.asText().equals(TEST_STRING)) {
                carRepository.deleteById(id.asText());
            }
        }
        
        // reload cars and assert no test data
        json = getAllCars();
        System.out.println(json);
        assertFalse(json.contains("\"" + TEST_STRING + "\""));
    }

    private String getAllCars() throws Exception {
        var result = mvc.perform(get("/api/car")
        .param("pageSize", String.valueOf(Integer.MAX_VALUE))
        .contentType(MediaType.TEXT_PLAIN))
        .andDo(print())
        .andExpect(status().isOk())
        .andReturn();
        return result.getResponse().getContentAsString();
    }

}
