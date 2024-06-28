package me.j3ltr.rankedtkrhelper.entities.round;

public class RoundPlayerData {
    private final long discordId;
    private final String minecraftName;
    private final int teamNumber;

    public RoundPlayerData(long discordId, String minecraftName, int teamNumber) {
        this.minecraftName = minecraftName;
        this.discordId = discordId;
        this.teamNumber = teamNumber;
    }

    public String getMinecraftName() {
        return minecraftName;
    }

    public long getDiscordId() {
        return discordId;
    }

    public int getTeamNumber() {
        return teamNumber;
    }
}
