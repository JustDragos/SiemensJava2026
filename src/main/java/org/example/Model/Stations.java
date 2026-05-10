package org.example.Model;

import java.util.Objects;

public class Stations {
    private static int idCounter = 1;
    private int id;
    private String name;

    public Stations(String name) {
        this.id = idCounter++;
        this.name = name;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Stations)) return false;
        Stations s = (Stations) o;
        return id == s.id;
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() { return name + " (ID:" + id + ")"; }
}