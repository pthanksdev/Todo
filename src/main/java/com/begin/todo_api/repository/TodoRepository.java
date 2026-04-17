package com.begin.todo_api.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.begin.todo_api.model.Todo;
import com.begin.todo_api.model.User;

public interface TodoRepository extends JpaRepository<Todo, Long> {
    @Query("""
            select t from Todo t
            where t.user = :user
              and (:completed is null or t.completed = :completed)
              and (
                   :query is null
                   or lower(t.title) like lower(concat('%', :query, '%'))
                   or lower(coalesce(t.description, '')) like lower(concat('%', :query, '%'))
              )
            """)
    Page<Todo> search(
            @Param("user") User user,
            @Param("completed") Boolean completed,
            @Param("query") String query,
            Pageable pageable
    );
}
