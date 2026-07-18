package org.springframework.benchmark.jdbc.repository;

import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import org.springframework.benchmark.jdbc.model.Fortune;

import java.util.List;

@Repository
public interface FortuneRepository extends CrudRepository<Fortune, Integer> {

    @Query("SELECT id, message FROM fortune ORDER BY id")
    List<Fortune> findAllFortunes();
}
