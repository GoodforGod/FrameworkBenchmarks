package io.quarkus.benchmark.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class World {

    // Quarkus recommends public fields, what can I do about it
    @Id
    public int id;
    public int randomNumber;

    public World() { }

    public World(int id, int randomNumber) {
        this.id = id;
        this.randomNumber = randomNumber;
    }
}