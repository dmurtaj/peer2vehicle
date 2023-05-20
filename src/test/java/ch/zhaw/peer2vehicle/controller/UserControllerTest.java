package ch.zhaw.peer2vehicle.controller;

import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;

import ch.zhaw.peer2vehicle.model.User;
import ch.zhaw.peer2vehicle.repository.UserRepository;
import ch.zhaw.peer2vehicle.security.TestSecurityConfig;

@SpringBootTest
@Import(TestSecurityConfig.class)
@AutoConfigureMockMvc
@TestMethodOrder(OrderAnnotation.class)
public class UserControllerTest {

    @Autowired
    UserRepository userRepository;

    private static final String TEST_EMAIL = "test.abc.xyz@gmail.com";

    @Test
    @Order(1)
    @WithMockUser
    public void testDeleteUser() throws Exception {
        User result = userRepository.findFirstByEmail(TEST_EMAIL);
        if (result != null) {
            userRepository.deleteById(result.getId());
        }

        result = userRepository.findFirstByEmail(TEST_EMAIL);
        assertNull(result);
    }
}