package me.j3ltr.rankedtkrhelper.entities.race;

import me.j3ltr.rankedtkrhelper.entities.round.RoundPlayerData;
import me.j3ltr.rankedtkrhelper.utils.RaceUtil;

import java.util.ArrayList;
import java.util.Comparator;
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

    public String getDiscordCommand(List<RoundPlayerData> roundPlayers) {
        List<RacePlacement> sortedRacePlacements = new ArrayList<>(racePlacements);
        sortedRacePlacements.sort(Comparator.comparingInt(RacePlacement::getPosition));

        List<String> placementStrings = new ArrayList<>();

        for (RacePlacement rp : sortedRacePlacements) {
            RoundPlayerData rpd = null;

            if (roundPlayers != null) {
                rpd = roundPlayers.stream().filter(player -> player.getMinecraftName().equals(rp.getPlayer())).findFirst().orElse(null);
            }

            String argument = rpd != null ? "<@" + rpd.getDiscordId() + ">" : rp.getPlayer();

            placementStrings.add(rp.getPosition() + RaceUtil.getPositionSuffix(rp.getPosition()) + ": " + argument);
        }

        return "/race map: " + map + " " + String.join(" ", placementStrings);
    }
}
