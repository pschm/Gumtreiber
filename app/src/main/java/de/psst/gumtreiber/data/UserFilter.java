package de.psst.gumtreiber.data;

import java.util.ArrayList;

/**
 * UserFilter provides flags for every filter-option displayed in the ui
 * it can also filter a user list according to the flags
 *
 * The UserFilter is used from the {@link de.psst.gumtreiber.map.MapControl}
 * TODO add/delete flags according to the UI-implementation
 */
public class UserFilter {
    // Filter flags
    public static final boolean FIRENDS = true;
    // IT
    public static final boolean INF = true;
    public static final boolean AI = true;
    public static final boolean MI = true;
    public static final boolean WI = true;
    public static final boolean ITM = true;
    public static final boolean TI = true;
    // Engineer
    public static final boolean ING = true;
    public static final boolean MA = true;

    public static ArrayList<User> filterUsers(ArrayList<User> users) {
        ArrayList<User> filtered = new ArrayList<>();

        for (User u : users) {
            if (isFiltered(u.getCourse())) filtered.add(u);
        }

        return filtered;
    }

    /**
     * Checks if the given course is
     * @param course course to check if it should be shown
     * @return true, if the course flag is set
     */
    private static boolean isFiltered(Course course) {
        if (course == null) return true; // TODO delete later
        switch (course) {
            case AI: if (AI) return true;
            case MI: if (MI) return true;
            case WI: if (WI) return true;
            case ITM: if (ITM) return true;
            case TI: if (TI) return true;
        }
        return false;
    }
}
