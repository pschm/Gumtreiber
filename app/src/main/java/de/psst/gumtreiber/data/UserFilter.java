package de.psst.gumtreiber.data;

import java.util.ArrayList;

/**
 * UserFilter provides flags for every filter-option displayed in the ui
 * it can also filter a user list according to the flags
 *
 * The UserFilter is used from the {@link de.psst.gumtreiber.map.MapControl}
 */
public class UserFilter {
    // Filter flags
    public static boolean FRIEND_FILTER = true;
    public static boolean BOT_FILTER = true;
    // Course flags
    public static boolean NONE_FILTER = true;
    public static boolean INF_FILTER = true;
    public static boolean ING_FILTER = true;
    public static boolean PROF_FILTER = true;

    private static ArrayList<String> friendList = new ArrayList<>();

    public static ArrayList<AbstractUser> filterUsers(ArrayList<AbstractUser> users) {
        ArrayList<AbstractUser> filtered = new ArrayList<>();

        for (AbstractUser u : users) {
            if (friendList.contains(u.getUid()) && FRIEND_FILTER) {
                filtered.add(u);
                continue;
            }
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
            case NONE: return NONE_FILTER;
            case INF: return INF_FILTER;
            case ING: return ING_FILTER;
            case PROF: return PROF_FILTER;
            default: return false;
        }
    }

    public static void setFriendList(ArrayList<String> friendList) {
        UserFilter.friendList = friendList;
    }
}
