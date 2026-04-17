package com.begin.todo_api;

import com.begin.todo_api.dto.TodoCreateRequest;
import com.begin.todo_api.model.User;
import com.begin.todo_api.repository.UserRepository;
import com.begin.todo_api.security.JwtService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureMockMvc
class TodoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void createAndListTodos() throws Exception {
        User user = userRepository.save(new User("carol", "hash", "ROLE_USER"));
        String token = jwtService.generateToken(user.getUsername(), user.getRole());

        TodoCreateRequest createRequest = new TodoCreateRequest();
        createRequest.setTitle("Walk dog");
        createRequest.setDescription("Evening walk");
        createRequest.setCompleted(false);

        mockMvc.perform(post("/todos")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated());

        MvcResult listResult = mockMvc.perform(get("/todos")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        String body = listResult.getResponse().getContentAsString();
        Assertions.assertTrue(body.contains("Walk dog"));
    }
}
