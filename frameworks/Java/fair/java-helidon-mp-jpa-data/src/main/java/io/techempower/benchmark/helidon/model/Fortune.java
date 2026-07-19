
package io.techempower.benchmark.helidon.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "fortune")
public class Fortune implements Comparable<Fortune> {

    @Id
    @Column(name = "id")
    public int id;

    @Column(name = "message")
    public String message;

    public Fortune() {
    }

    public Fortune(int id, String message) {
        this.id = id;
        this.message = message;
    }

    public int getId() {
        return id;
    }

    public String getMessage() {
        return message;
    }
    @Override
    public int compareTo(Fortune other) {
        return message.compareTo(other.message);
    }
}
