package io.quarkus.benchmark.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "world")
public class World {
    
    @Id
    private int id;
    
    @Column(name = "randomnumber")
    public int randomNumber;
    
    public World() {}
    
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
}
