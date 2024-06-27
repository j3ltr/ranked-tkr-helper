package me.j3ltr.rankedtkrhelper;

import com.google.gson.Gson;
import gg.essential.api.EssentialAPI;
import gg.essential.api.gui.Notifications;
import gg.essential.universal.UDesktop;
import kotlin.Unit;
import me.j3ltr.rankedtkrhelper.commands.LastRaceCommand;
import me.j3ltr.rankedtkrhelper.commands.RankedTkrHelperCommand;
import me.j3ltr.rankedtkrhelper.entities.race.Race;
import me.j3ltr.rankedtkrhelper.utils.ClipboardUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatStyle;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent;

import java.io.*;
import java.net.URI;
import java.net.URL;
import java.util.*;
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
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new RaceListener(this, raceHandler));
        ClientCommandHandler.instance.registerCommand(new LastRaceCommand(this));
        ClientCommandHandler.instance.registerCommand(new RankedTkrHelperCommand(this));
    }

    @Mod.EventHandler
    public void onLoadComplete(FMLLoadCompleteEvent event) {
        checkForUpdates();
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

        if (minecraft.isSingleplayer() || minecraft.getCurrentServerData() == null) {
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

    private void checkForUpdates() {
        String currentVersion = FMLCommonHandler.instance().findContainerFor(this).getVersion();
        String latestVersion;

        try {
            Properties properties = new Properties();
            URL url = new URL(Requester.LATEST_MOD_PROPERTIES);
            InputStream input = Requester.openHTTPSConnection(url).getInputStream();

            properties.load(input);

            latestVersion = properties.getProperty("version");
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        if (latestVersion == null) {
            return;
        }

        if (!currentVersion.equals(latestVersion)) {
            Notifications notifications = EssentialAPI.getNotifications();

            notifications.push("Ranked TKR Helper", "A new version is available! " +
                    "Click to open the Github Release page of the latest version.", 10, () -> {

                String updateLink = "https://github.com/j3ltr/ranked-tkr-helper/releases/latest";

                try {
                    UDesktop.browse(URI.create(updateLink));
                } catch (Exception openException) {
                    notifications.push("Ranked TKR Helper", "Failed to open Github Release page. Link is now copied to your clipboard.");
                    try {
                        ClipboardUtil.copyToClipboard(updateLink);
                    } catch (Exception clipboardException) {
                        notifications.push("Ranked TKR Helper", "Failed to copy Github Release page link to clipboard.");
                    }
                }

                return Unit.INSTANCE;
            });
        }
    }
}