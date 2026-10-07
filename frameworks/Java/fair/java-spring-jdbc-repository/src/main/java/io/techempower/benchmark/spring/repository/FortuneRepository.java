package io.techempower.benchmark.spring.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import io.techempower.benchmark.spring.model.Fortune;

import java.util.List;

@Repository
public interface FortuneRepository extends CrudRepository<Fortune, Integer> {

    @Query("SELECT id, message FROM fortune ORDER BY id")
    List<Fortune> findAllFortunes();
}
