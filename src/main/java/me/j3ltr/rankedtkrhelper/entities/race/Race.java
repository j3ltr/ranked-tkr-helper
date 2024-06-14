package me.j3ltr.rankedtkrhelper.entities.race;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
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

    public String getDiscordCommand(HashMap<String, Long> ignToDiscordId) {
        List<RacePlacement> sortedRacePlacements = new ArrayList<>(racePlacements);
        sortedRacePlacements.sort(Comparator.comparingInt(RacePlacement::getPosition));

        List<String> placementStrings = new ArrayList<>();

        for (RacePlacement rp : sortedRacePlacements) {
            String discord;
            if (ignToDiscordId != null && ignToDiscordId.containsKey(rp.getPlayer())) {
                discord = "<@" + ignToDiscordId.get(rp.getPlayer()) + ">";
            } else {
                discord = rp.getPlayer();
            }

            placementStrings.add(rp.getPosition() + getPositionSuffix(rp.getPosition()) + ": " + discord);
        }

        return "/race map: " + map + " " + String.join(" ", placementStrings);
    }

    private String getPositionSuffix(int position) {
        switch (position) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }
}
