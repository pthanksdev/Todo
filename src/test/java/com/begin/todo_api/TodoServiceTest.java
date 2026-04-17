package com.begin.todo_api;

import com.begin.todo_api.dto.TodoCreateRequest;
import com.begin.todo_api.dto.TodoUpdateRequest;
import com.begin.todo_api.model.User;
import com.begin.todo_api.repository.UserRepository;
import com.begin.todo_api.service.TodoService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class TodoServiceTest {
    @Autowired
    private TodoService todoService;

    @Autowired
    private UserRepository userRepository;

    @Test
    void createUpdateDeleteFlow() {
        User user = userRepository.save(new User("alice", "hash", "ROLE_USER"));

        TodoCreateRequest createRequest = new TodoCreateRequest();
        createRequest.setTitle("Study");
        createRequest.setDescription("Read docs");
        createRequest.setCompleted(false);

        var created = todoService.create(user, createRequest);
        Assertions.assertNotNull(created.getId());
        Assertions.assertEquals("Study", created.getTitle());

        TodoUpdateRequest updateRequest = new TodoUpdateRequest();
        updateRequest.setTitle("Study hard");
        updateRequest.setDescription("Read docs and notes");
        updateRequest.setCompleted(true);

        var updated = todoService.update(user, created.getId(), updateRequest);
        Assertions.assertTrue(updated.isCompleted());

        Page<?> page = todoService.list(user, true, "Study", PageRequest.of(0, 10));
        Assertions.assertEquals(1, page.getTotalElements());

        todoService.delete(user, created.getId());
        Page<?> afterDelete = todoService.list(user, null, null, PageRequest.of(0, 10));
        Assertions.assertEquals(0, afterDelete.getTotalElements());
    }
}
