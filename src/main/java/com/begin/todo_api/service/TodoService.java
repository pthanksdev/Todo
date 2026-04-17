package com.begin.todo_api.service;

import com.begin.todo_api.dto.TodoCreateRequest;
import com.begin.todo_api.dto.TodoResponse;
import com.begin.todo_api.dto.TodoUpdateRequest;
import com.begin.todo_api.exception.NotFoundException;
import com.begin.todo_api.model.Todo;
import com.begin.todo_api.model.User;
import com.begin.todo_api.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TodoService {
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public TodoResponse create(User user, TodoCreateRequest request) {
        boolean completed = request.getCompleted() != null && request.getCompleted();
        Todo todo = new Todo(request.getTitle(), request.getDescription(), completed, user);
        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public Page<TodoResponse> list(User user, Boolean completed, String query, Pageable pageable) {
        Page<Todo> page = todoRepository.search(user, completed, emptyToNull(query), pageable);
        return page.map(this::toResponse);
    }

    public TodoResponse getOne(User user, Long id) {
        Todo todo = todoRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("todo not found"));
        return toResponse(todo);
    }

    public TodoResponse update(User user, Long id, TodoUpdateRequest request) {
        Todo todo = todoRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("todo not found"));

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        boolean completed = request.getCompleted() != null && request.getCompleted();
        todo.setCompleted(completed);

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public void delete(User user, Long id) {
        Todo todo = todoRepository.findById(id)
                .filter(item -> item.getUser().getId().equals(user.getId()))
                .orElseThrow(() -> new NotFoundException("todo not found"));
        todoRepository.delete(todo);
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt(),
                todo.getUpdatedAt()
        );
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }
}
