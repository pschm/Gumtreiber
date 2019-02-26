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
    public static boolean FIRENDS = true;
    public static boolean BOT_FILTER = true;
    //Course flags
    public static boolean INF_FILTER= true;
    public static boolean MB_FILTER = true;
    public static boolean PROF_FILTER = true;


    public static ArrayList<AbstractUser> filterUsers(ArrayList<AbstractUser> users) {
        ArrayList<AbstractUser> filtered = new ArrayList<>();

        for (AbstractUser u : users) {
            if (u instanceof User && isFiltered(((User) u).getCourse())) filtered.add(u);
            if (u instanceof Bot && BOT_FILTER) filtered.add(u);
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
            case INF: return INF_FILTER;
            case MB: return MB_FILTER;
            case PROF: return PROF_FILTER;
            default: return false;
        }
    }
}
