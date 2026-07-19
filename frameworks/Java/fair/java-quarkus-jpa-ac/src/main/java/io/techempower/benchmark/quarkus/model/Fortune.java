package io.techempower.benchmark.quarkus.model;

import io.quarkus.hibernate.orm.panache.PanacheEntityBase;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.List;

@Entity
@Table(name = "fortune")
public class Fortune extends PanacheEntityBase {
    
    @Id
    public int id;
    
    @Column(name = "message")
    public String message;
    
    public Fortune() {}
    
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

    public static List<Fortune> findAllFortunes() {
        return listAll();
    }
}
