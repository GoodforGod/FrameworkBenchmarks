package io.techempower.benchmark.quarkus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "world")
public class World extends PanacheEntity {

    @Column(name = "randomnumber")
    public int randomNumber;

    public World() { }

    public World(int id, int randomNumber) {
        this.id = Long.valueOf(id);
        this.randomNumber = randomNumber;
    }

    public int getId() {
        return id.intValue();
    }

    public int getRandomNumber() {
        return randomNumber;
    }
}
