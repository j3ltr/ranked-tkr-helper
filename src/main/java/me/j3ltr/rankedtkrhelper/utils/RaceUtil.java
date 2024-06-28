package me.j3ltr.rankedtkrhelper.utils;

public class RaceUtil {
    public static String getPositionSuffix(int position) {
        switch (position) {
            case 1:
                return "st";
            case 2:
                return "nd";
            case 3:
                return "rd";
            default:
                return "th";
        }
    }

    public static int getPositionPoints(int placement) {
        int[] pointsPerPositon = {15, 12, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

        return pointsPerPositon[placement - 1];
    }
}
