package me.j3ltr.rankedtkrhelper.entities.race;

public class RacePlacement {
    private String player;
    private int position;

    public RacePlacement(String player, int position) {
        this.player = player;
        this.position = position;
    }

    public String getPlayer() {
        return player;
    }

    public int getPosition() {
        return position;
    }
}
