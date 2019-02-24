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
    public final boolean FIRENDS = true;
    // IT
    public final boolean INF = true;
    public final boolean AI = true;
    public final boolean MI = true;
    public final boolean WI = true;
    public final boolean ITM = true;
    public final boolean TI = true;
    // Engineer
    public final boolean ING = true;
    public final boolean MA = true;

    public ArrayList<User> filterUsers(ArrayList<User> users) {
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
    private boolean isFiltered(Course course) {
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
