package io.quarkus.benchmark.dao;

import io.quarkus.benchmark.entity.World;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.util.List;

@ApplicationScoped
public class WorldDao implements PanacheRepository<World> {
    
    public World findById(int id) {
        return getEntityManager()
                .createQuery("SELECT w.id, w.randomNumber FROM World w WHERE w.id = :id", World.class)
                .setParameter("id", id)
                .getSingleResult();
    }
    
    public int findRandomNumberById(int id) {
        return getEntityManager()
                .createQuery("SELECT w.randomNumber FROM World w WHERE w.id = :id", Integer.class)
                .setParameter("id", id)
                .getSingleResult();
    }
    
    public void updateRandomNumber(int id, int randomNumber) {
        getEntityManager()
                .createQuery("UPDATE World w SET w.randomNumber = :randomNumber WHERE w.id = :id")
                .setParameter("id", id)
                .setParameter("randomNumber", randomNumber)
                .executeUpdate();
    }
    
    public List<World> findAllFortunesAsWorlds() {
        return listAll();
    }
}
