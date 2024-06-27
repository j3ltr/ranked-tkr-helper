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

    public static Config INSTANCE = new Config();

    public Config() {
        super(new File("./config/ranked-tkr-helper.toml"), "Ranked TKR Helper Settings");
        initialize();
    }
}
