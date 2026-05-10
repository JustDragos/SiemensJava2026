package org.example.Model.TrainPaths;

import org.example.Model.Route;
import org.example.Model.Stations;
import org.example.Model.Train;

public class DirectPath {
    private Stations from;
    private Stations to;
    private Route route;
    private Train train;
    public DirectPath(Stations from, Stations to, Route route, Train train) {
        this.from = from;
        this.to = to;
        this.route = route;
        this.train = train;
    }

    public Stations getFrom() {
        return from;
    }

    public void setFrom(Stations from) {
        this.from = from;
    }

    public Stations getTo() {
        return to;
    }

    public void setTo(Stations to) {
        this.to = to;
    }

    public Route getRoute() {
        return route;
    }

    public void setRoute(Route route) {
        this.route = route;
    }

    public Train getTrain() {
        return train;
    }

    public void setTrain(Train train) {
        this.train = train;
    }
}
