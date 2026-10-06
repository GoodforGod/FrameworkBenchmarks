package io.techempower.benchmark.quarkus.model;

import io.quarkus.hibernate.reactive.panache.PanacheEntityBase;
import io.smallrye.mutiny.Uni;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "world")
public class World extends PanacheEntityBase {

    @Id
    public int id;

    @Column(name = "randomnumber")
    public int randomNumber;

    public World() {
    }

    public World(int id, int randomNumber) {
        this.id = id;
        this.randomNumber = randomNumber;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRandomNumber() {
        return randomNumber;
    }

    public void setRandomNumber(int randomNumber) {
        this.randomNumber = randomNumber;
    }

    public static Uni<World> findWorld(int id) {
        return findById(id);
    }

    public static Uni<Integer> updateRandomNumber(int id, int randomNumber) {
        return update("randomNumber = ?1 WHERE id = ?2", randomNumber, id);
    }
}
