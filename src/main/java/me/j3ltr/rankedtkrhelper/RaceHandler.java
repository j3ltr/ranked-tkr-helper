package me.j3ltr.rankedtkrhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.j3ltr.rankedtkrhelper.entities.race.Race;
import me.j3ltr.rankedtkrhelper.entities.race.RacePlacement;
import me.j3ltr.rankedtkrhelper.entities.race.RaceStatus;
import me.j3ltr.rankedtkrhelper.entities.round.RoundPlayerData;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static me.j3ltr.rankedtkrhelper.utils.ClipboardUtil.copyToClipboard;

public class RaceHandler {
    private final RankedTkrHelper mod;

    public RaceHandler(RankedTkrHelper mod) {
        this.mod = mod;
    }

    public void handleServerInfoReceived(JsonObject serverInfo) {
        String map = serverInfo.get("map").getAsString();

        mod.sendMessage("You have joined a " + map + " queue.");

        if (mod.getPreviousRace() != null && mod.getPreviousRace().getMap().equals(map)) {
            mod.sendMessage("Last race was also on this map. Make sure to requeue if necessary.");
        }

        mod.setCurrentRace(new Race(map));

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Requester.PLAYER_DATA_URL);

                // Request player data two times so that the second request has up-to-date data.
                // See https://devcenter.heroku.com/articles/dataclips#dataclip-execution-and-data-freshness
                Requester.openHTTPSConnection(url).getInputStream();
                JsonObject apiResponse = mod.getGson().fromJson(new InputStreamReader(Requester.openHTTPSConnection(url).getInputStream()), JsonObject.class);

                JsonArray values = apiResponse.get("values").getAsJsonArray();
                List<RoundPlayerData> currentRoundPlayers = new ArrayList<>();

                for (JsonElement value : values) {
                    JsonArray valueArray = value.getAsJsonArray();
                    long discordId = valueArray.get(0).getAsLong();
                    String minecraftName = valueArray.get(1).getAsString();
                    int teamNumber = valueArray.get(2).getAsInt();


                    currentRoundPlayers.add(new RoundPlayerData(discordId, minecraftName, teamNumber));
                }

                mod.setCurrentRoundPlayers(currentRoundPlayers);
            } catch (Exception ignore) {}
        });
        thread.start();
    }

    public void handlePlayerFinished(RacePlacement racePlacement) {
        if (mod.getCurrentRace() == null) {
            return;
        }

        mod.getCurrentRace().addRacePlacement(racePlacement);
    }

    public void handleRaceEnded() {
        if (mod.getCurrentRace() == null) {
            return;
        }

        mod.getCurrentRace().setStatus(RaceStatus.ENDED);

        mod.getPreviousRaces().add(mod.getCurrentRace());
        mod.setCurrentRace(null);

        mod.sendMessage("The race has ended.");

        copyToClipboard(mod.getPreviousRace().getDiscordCommand(mod.getCurrentRoundPlayers()));

        Minecraft.getMinecraft().addScheduledTask(() -> {
            mod.sendMessage("The /race command has been copied to your clipboard. Use this in the round thread in Discord.");

            ChatComponentText lastRaceCommandText = new ChatComponentText("Use /lastrace or click this message to copy the command again.");
            lastRaceCommandText.setChatStyle(new ChatStyle()
                    .setBold(false)
                    .setUnderlined(true)
                    .setColor(EnumChatFormatting.BLUE)
                    .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lastrace")));

            mod.sendMessage(lastRaceCommandText);
        });
    }
}
