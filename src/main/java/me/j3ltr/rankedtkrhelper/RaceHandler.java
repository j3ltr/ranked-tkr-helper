package me.j3ltr.rankedtkrhelper;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.j3ltr.rankedtkrhelper.entities.race.Race;
import me.j3ltr.rankedtkrhelper.entities.race.RacePlacement;
import me.j3ltr.rankedtkrhelper.entities.race.RaceStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.event.ClickEvent;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;

import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;

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

        // Request player data now so that the next request, when the race ends, has up-to-date data.
        // See https://devcenter.heroku.com/articles/dataclips#dataclip-execution-and-data-freshness
        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Requester.PLAYER_DATA_URL);
                Requester.openHTTPSConnection(url).getInputStream();
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
        mod.sendMessage("Retrieving round data...");

        Thread thread = new Thread(() -> {
            try {
                URL url = new URL(Requester.PLAYER_DATA_URL);
                JsonObject apiResponse = mod.getGson().fromJson(new InputStreamReader(Requester.openHTTPSConnection(url).getInputStream()), JsonObject.class);

                JsonArray values = apiResponse.get("values").getAsJsonArray();
                HashMap<String, Long> ignToDiscordId = new HashMap<>();

                for (JsonElement value : values) {
                    JsonArray valueArray = value.getAsJsonArray();

                    ignToDiscordId.put(valueArray.get(1).getAsString(), valueArray.get(0).getAsLong());
                }

                mod.setIgnToDiscordId(ignToDiscordId);

                copyToClipboard(mod.getPreviousRace().getDiscordCommand(mod.getIgnToDiscordId()));

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    mod.sendMessage("Round data successfully retrieved.");
                    mod.sendMessage("The /race command has been copied to your clipboard. Use this in the round thread in Discord.");

                    ChatComponentText lastRaceCommandText = new ChatComponentText("Use /lastrace or click this message to copy the command again.");
                    lastRaceCommandText.setChatStyle(new ChatStyle()
                            .setBold(false)
                            .setUnderlined(true)
                            .setColor(EnumChatFormatting.BLUE)
                            .setChatClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/lastrace")));

                    mod.sendMessage(lastRaceCommandText);
                });
            } catch (Exception e) {
                copyToClipboard(mod.getPreviousRace().getDiscordCommand(null));

                Minecraft.getMinecraft().addScheduledTask(() -> {
                    mod.sendMessage("Round data retrieval failed.");
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
        });
        thread.start();
    }
}
