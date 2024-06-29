package me.j3ltr.rankedtkrhelper.entities.race;

import java.util.ArrayList;
import java.util.List;

public class Race {
    private final String map;
    private final List<RacePlacement> racePlacements = new ArrayList<>();
    private RaceStatus raceStatus;


    public Race(String map) {
        this.map = map;
        this.raceStatus = RaceStatus.ONGOING;
    }

    public String getMap() {
        return map;
    }

    public List<RacePlacement> getRacePlacements() {
        return racePlacements;
    }

    public void addRacePlacement(RacePlacement placement) {
        racePlacements.add(placement);
    }

    public RaceStatus getStatus() {
        return raceStatus;
    }

    public void setStatus(RaceStatus status) {
        this.raceStatus = status;
    }
}
