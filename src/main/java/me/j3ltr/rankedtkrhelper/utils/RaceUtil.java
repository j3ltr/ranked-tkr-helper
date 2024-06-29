package me.j3ltr.rankedtkrhelper.utils;

import me.j3ltr.rankedtkrhelper.entities.race.Race;
import me.j3ltr.rankedtkrhelper.entities.race.RacePlacement;
import me.j3ltr.rankedtkrhelper.entities.round.RoundPlayerData;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class RaceUtil {
    public static String getPositionSuffix(int position) {
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

    public static int getPositionPoints(int placement) {
        int[] pointsPerPositon = {15, 12, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        return pointsPerPositon[placement - 1];
    }

    public static String getDiscordCommand(Race race, List<RoundPlayerData> roundPlayers) {
        List<RacePlacement> sortedRacePlacements = new ArrayList<>(race.getRacePlacements());
        sortedRacePlacements.sort(Comparator.comparingInt(RacePlacement::getPosition));

        List<String> placementStrings = new ArrayList<>();

        for (RacePlacement rp : sortedRacePlacements) {
            RoundPlayerData rpd = null;

            if (roundPlayers != null) {
                rpd = roundPlayers
                        .stream()
                        .filter(player -> player.getMinecraftName().equals(rp.getPlayer()))
                        .findFirst()
                        .orElse(null);
            }

            String argument = rpd != null ? "<@" + rpd.getDiscordId() + ">" : rp.getPlayer();

            placementStrings.add(rp.getPosition() + RaceUtil.getPositionSuffix(rp.getPosition()) + ": " + argument);
        }

        return "/race map: " + race.getMap() + " " + String.join(" ", placementStrings);
    }
}
