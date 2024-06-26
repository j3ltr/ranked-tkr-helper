package me.j3ltr.rankedtkrhelper;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import gg.essential.universal.ChatColor;
import me.j3ltr.rankedtkrhelper.entities.race.RacePlacement;
import me.j3ltr.rankedtkrhelper.entities.round.RoundPlayerData;
import me.j3ltr.rankedtkrhelper.utils.RaceUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ChatComponentText;
import net.minecraftforge.client.event.ClientChatReceivedEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RaceListener {
    private static final Pattern PLAYER_FINISH_CHAT_PATTERN = Pattern.compile("(?<name>[0-9A-Za-z_]{1,16}) has finished the race at position #(?<position>[1-9]|1[012])!");
    private static final Pattern RACE_END_CHAT_PATTERN = Pattern.compile("\\(\\+\\d+ coins\\) Completed the race!");

    private final RankedTkrHelper mod;
    private final RaceHandler raceHandler;

    /**
     * Whether the mod has used the "/locraw" command to poll the current Hypixel server info.
     */
    private boolean hasPolledServerInfo = false;

    /**
     * Whether the mod has received a response from the "/locraw" command.
     */
    private boolean hasReceivedServerInfo = false;

    public RaceListener(RankedTkrHelper mod, RaceHandler raceHandler) {
        this.mod = mod;
        this.raceHandler = raceHandler;
    }


    // TODO: Fix onWorldLoad also firing when pausing for the first time after the world has loaded.
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        if (!mod.isPlayerOnHypixel() || !Config.isEnabled) {
            return;
        }

        // Mark the current world as eligible for "/locraw" to be run.
        hasPolledServerInfo = false;
        hasReceivedServerInfo = false;
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (!mod.isPlayerOnHypixel()  || !Config.isEnabled) {
            return;
        }

        if (!hasPolledServerInfo) {
            Minecraft.getMinecraft().thePlayer.sendChatMessage("/locraw");

            hasPolledServerInfo = true;
        }
    }

    @SubscribeEvent
    public void onClientChatReceived(ClientChatReceivedEvent event) {
        if (!mod.isPlayerOnHypixel()  || !Config.isEnabled) {
            return;
        }

        String messageText = event.message.getUnformattedText();

        if (mod.isModMessage(messageText)) {
            return;
        }

        if (!hasReceivedServerInfo) {
            try {
                JsonObject response = mod.getGson().fromJson(messageText, JsonObject.class);

                if (response == null) {
                    return;
                }
                if (response.has("gametype") &&
                    response.get("gametype").getAsString().equals("GINGERBREAD")) {
                    raceHandler.handleServerInfoReceived(response);
                }

                hasReceivedServerInfo = true;
                event.setCanceled(true);
            } catch (JsonSyntaxException ignore) {}

            return;
        }

        Matcher playerFinishChatMatcher = PLAYER_FINISH_CHAT_PATTERN.matcher(messageText);
        if (playerFinishChatMatcher.matches()) {
            String player = playerFinishChatMatcher.group("name");
            int position = Integer.parseInt(playerFinishChatMatcher.group("position"));

            RoundPlayerData rpd = mod.getRoundPlayerData(player);

            if (rpd != null) {
                String message = event.message.getFormattedText();

                if (Config.showTeamColorsSuffixes) {
                    String[] teamNames = {"A", "B", "C", "D", "E", "F"};
                    ChatColor[] teamColors = {ChatColor.RED, ChatColor.GOLD, ChatColor.YELLOW, ChatColor.GREEN, ChatColor.AQUA, ChatColor.BLUE};

                    int index = rpd.getTeamNumber() - 1;

                    message = message.replace(player, teamColors[index] + player + " [" + teamNames[index] + "]");
                }

                if (Config.showPositionPoints) {
                    message += ChatColor.GRAY + " [+" + RaceUtil.getPositionPoints(position) + "]";
                }

                event.message = new ChatComponentText(message);
            }

            raceHandler.handlePlayerFinished(new RacePlacement(player, position));

            return;
        }

        Matcher raceEndChatMatcher = RACE_END_CHAT_PATTERN.matcher(messageText);
        if (raceEndChatMatcher.matches()) {
            raceHandler.handleRaceEnded();

            return;
        }
    }
}
