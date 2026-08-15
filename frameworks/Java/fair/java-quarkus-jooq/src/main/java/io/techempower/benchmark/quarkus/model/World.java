package io.techempower.benchmark.quarkus.model;

public class World {

    public int id;
    public int randomNumber;

    public World() {
    }

    public World(int id, int randomNumber) {
        this.id = id;
        this.randomNumber = randomNumber;
    }

    public int getIdInt() {
        return id;
    }
}
