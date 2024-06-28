package me.j3ltr.rankedtkrhelper;

import gg.essential.vigilance.Vigilant;
import gg.essential.vigilance.data.*;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class Config extends Vigilant {

    @Property(
            type = PropertyType.SWITCH,
            name = "Ranked TKR Helper",
            description = "Enable the Ranked TKR Helper mod.",
            category = "General",
            subcategory = "Ranked TKR Helper"
    )
    public static boolean isEnabled = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Check for updates",
            description = "Check for updates on startup and send a notification if there's a new version of the mod available.",
            category = "General",
            subcategory = "Ranked TKR Helper"
    )
    public static boolean checkForUpdates = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Automatically copy the scoring command",
            description = "Automatically copy the scoring command after the race has ended.",
            category = "General",
            subcategory = "Scoring"
    )
    public static boolean automaticallyCopyScoringCommand = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show team colors and suffixes",
            description = "Show team colors and suffixes whenever a player finishes.",
            category = "General",
            subcategory = "Miscellaneous"
    )
    public static boolean showTeamColorsSuffixes = true;

    @Property(
            type = PropertyType.SWITCH,
            name = "Show position points",
            description = "Show the amount of points gained whenever a player finishes.",
            category = "General",
            subcategory = "Miscellaneous"
    )
    public static boolean showPositionPoints = true;

    public static Config INSTANCE = new Config();

    public Config() {
        super(new File("./config/ranked-tkr-helper.toml"),
                "Ranked TKR Helper Settings",
                new JVMAnnotationPropertyCollector(),
                new ConfigSorting());
        initialize();
    }
}

class ConfigSorting extends SortingBehavior {
    @Override
    public @NotNull Comparator<? super Map.Entry<String, ? extends List<PropertyData>>> getSubcategoryComparator() {
        List<String> order = Arrays.asList("Ranked TKR Helper", "Scoring", "Miscellaneous");

        return Comparator.comparing(o -> order.indexOf(o.getKey()));
    }
}