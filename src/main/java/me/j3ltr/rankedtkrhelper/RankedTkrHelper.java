package me.j3ltr.rankedtkrhelper;

import com.google.gson.Gson;
import me.j3ltr.rankedtkrhelper.commands.LastRaceCommand;
import me.j3ltr.rankedtkrhelper.entities.race.Race;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Mod(modid = "rankedtkrhelper", useMetadata=true)
public class RankedTkrHelper {
    public static final String MESSAGE_PREFIX_TEXT = "[Ranked TKR Helper]";
    public static final EnumChatFormatting MESSAGE_PREFIX_COLOR = EnumChatFormatting.DARK_AQUA;

    private final Gson gson = new Gson();

    private Race currentRace = null;
    private final List<Race> previousRaces = new ArrayList<>();
    private HashMap<String, Long> ignToDiscordId = null;

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        RaceHandler raceHandler = new RaceHandler(this);
        MinecraftForge.EVENT_BUS.register(new RaceListener(this, raceHandler));
        ClientCommandHandler.instance.registerCommand(new LastRaceCommand(this));
    }

    public void sendMessage(String message) {
        ChatComponentText messageComponent = new ChatComponentText(message);
        messageComponent.setChatStyle(new ChatStyle().setBold(false).setColor(EnumChatFormatting.GRAY));

        sendMessage(messageComponent);
    }

    public void sendMessage(IChatComponent component) {
        Minecraft minecraft = Minecraft.getMinecraft();
        GuiNewChat chat = minecraft.ingameGUI.getChatGUI();

        ChatComponentText prefixComponent = new ChatComponentText(MESSAGE_PREFIX_TEXT + " ");
        prefixComponent.setChatStyle(new ChatStyle().setBold(true).setColor(MESSAGE_PREFIX_COLOR));

        chat.printChatMessage(prefixComponent.appendSibling(component));
    }

    public boolean isPlayerOnHypixel() {
        Minecraft minecraft = Minecraft.getMinecraft();

        if (minecraft.isSingleplayer()) {
            return false;
        }

        String serverIp = minecraft.getCurrentServerData().serverIP;

        return serverIp.endsWith("hypixel.net");
    }

    public boolean isModMessage(String unformattedText) {
        return unformattedText.startsWith(MESSAGE_PREFIX_TEXT + " ");
    }

    public Gson getGson() {
        return gson;
    }

    public Race getCurrentRace() {
        return currentRace;
    }

    public void setCurrentRace(Race race) {
        this.currentRace = race;
    }

    public List<Race> getPreviousRaces() {
        return previousRaces;
    }

    public Race getPreviousRace() {
        if (previousRaces.isEmpty()) {
            return null;
        }

        return previousRaces.get(previousRaces.size() - 1);
    }

    public HashMap<String, Long> getIgnToDiscordId() {
        return ignToDiscordId;
    }

    public void setIgnToDiscordId(HashMap<String, Long> ignToDiscordId) {
        this.ignToDiscordId = ignToDiscordId;
    }
}