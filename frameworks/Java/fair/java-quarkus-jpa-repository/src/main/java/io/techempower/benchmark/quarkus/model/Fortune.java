package io.techempower.benchmark.quarkus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "fortune")
public class Fortune extends PanacheEntity {

    @Column(name = "message")
    public String message;

    public Fortune() { }

    public Fortune(int id, String message) {
        this.id = Long.valueOf(id);
        this.message = message;
    }

    public int getId() {
        return id.intValue();
    }

    public String getMessage() {
        return message;
    }
}
