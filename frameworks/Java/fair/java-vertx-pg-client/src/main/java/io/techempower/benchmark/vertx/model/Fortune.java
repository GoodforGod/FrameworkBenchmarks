package io.techempower.benchmark.vertx.model;

public final class Fortune {

    private final int id;
    private final String message;

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
}
