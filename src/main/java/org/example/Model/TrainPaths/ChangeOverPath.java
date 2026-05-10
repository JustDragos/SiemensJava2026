package org.example.Model.TrainPaths;

import org.example.Model.Route;
import org.example.Model.Stations;
import org.example.Model.Train;

public class ChangeOverPath {
    private Stations from;
    private Stations to;
    private Route routeA;
    private Route routeB;
    private Train trainA;
    private Train trainB;
    private Stations mid;
    public ChangeOverPath(Stations from, Stations to, Route routeA, Route routeB, Train trainA, Train trainB, Stations mid) {
        this.from = from;
        this.to = to;
        this.routeA = routeA;
        this.routeB = routeB;
        this.trainA = trainA;
        this.trainB = trainB;
        this.mid = mid;

    }

    public Stations getMid() {
        return mid;
    }

    public void setMid(Stations mid) {
        this.mid = mid;
    }

    public Train getTrainB() {
        return trainB;
    }

    public void setTrainB(Train trainB) {
        this.trainB = trainB;
    }

    public Train getTrainA() {
        return trainA;
    }

    public void setTrainA(Train trainA) {
        this.trainA = trainA;
    }

    public Route getRouteB() {
        return routeB;
    }

    public void setRouteB(Route routeB) {
        this.routeB = routeB;
    }

    public Route getRouteA() {
        return routeA;
    }

    public void setRouteA(Route routeA) {
        this.routeA = routeA;
    }

    public Stations getTo() {
        return to;
    }

    public void setTo(Stations to) {
        this.to = to;
    }

    public Stations getFrom() {
        return from;
    }

    public void setFrom(Stations from) {
        this.from = from;
    }
}
