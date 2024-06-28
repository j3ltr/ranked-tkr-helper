package me.j3ltr.rankedtkrhelper;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.Property;
import gg.essential.vigilance.data.PropertyType;

import java.io.File;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.SWITCH,
            name = "Ranked TKR Helper",
            description = "Enable the Ranked TKR Helper mod.",
            category = "General"
    )
    public static boolean isEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Check for updates",
            description = "Check for updates on startup and send a notification if there's a new version available.",
            category = "General"
    )
    public static boolean checkForUpdates = true;

    public static Config INSTANCE = new Config();

    public Config() {
        super(new File("./config/ranked-tkr-helper.toml"), "Ranked TKR Helper Settings");
        initialize();
    }
}
