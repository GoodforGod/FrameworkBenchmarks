package org.springframework.benchmark.jdbc.repository;

import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.benchmark.jdbc.model.World;

@Repository
public interface WorldRepository extends CrudRepository<World, Integer> {

    @Query("SELECT id, randomnumber FROM world WHERE id = :id")
    World findById(@Param("id") int id);

    @Modifying
    @Query("UPDATE world SET randomnumber = :randomNumber WHERE id = :id")
    void updateRandomNumber(@Param("id") int id, @Param("randomNumber") int randomNumber);
}
