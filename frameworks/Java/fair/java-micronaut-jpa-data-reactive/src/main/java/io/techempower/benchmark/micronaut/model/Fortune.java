package io.techempower.benchmark.micronaut.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fortune")
public class Fortune implements Comparable<Fortune> {

    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "message")
    private String message;

    public Fortune() {
    }

    public Fortune(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public int compareTo(Fortune other) {
        return this.message.compareTo(other.message);
    }
}
