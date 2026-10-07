package io.techempower.benchmark.spring.repository;

import io.techempower.benchmark.spring.model.Fortune;
import io.techempower.benchmark.spring.model.World;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface WorldRepository extends JpaRepository<World, Integer> {

    @Override
    @Query("SELECT w FROM World w WHERE w.id = :id")
    Optional<World> findById(@Param("id") Integer id);

    @Query("SELECT w.randomNumber FROM World w WHERE w.id = :id")
    int findRandomNumberById(@Param("id") int id);

    @Query("SELECT f FROM Fortune f ORDER BY f.message")
    List<Fortune> findAllFortunes();
}
