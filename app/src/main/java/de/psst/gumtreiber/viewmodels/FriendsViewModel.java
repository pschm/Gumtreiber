package de.psst.gumtreiber.viewmodels;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserDataSync;

public class FriendsViewModel extends AndroidViewModel {

    private String uid;
    private String token;

    //private MutableLiveData<List<String>> friends = new MutableLiveData<>();
    private List<User> friendList; //Local copy of firendlist; No need to fetch the whole list on add or deletion

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        token = UserDataSync.getUserToken();

        friendList = getOrCreateFriends();
    }

    public List<User> getFriendListRef() {
        return friendList;
    }

    private List<User> getOrCreateFriends() {
        //Firebase friendList
        return new ArrayList<>(Objects.requireNonNull(Firebase.getAllFriends(uid, token)));
    }

    /**
     * Delets a user from FriendList
     *
     * @param user user to be deleted
     */
    public void deleteFriend(User user) {
        //Delete from Firebase
        Firebase.deleteUserFromFriendlist(uid, user.getUid());
        friendList.remove(user);
    }

    /**
     * Adds a new Friend to the FriendList
     *
     * @param user user to be added
     */
    public void addFriend(User user) {
        //Add to Firebase
        Firebase.addUserToFriendlist(uid, user.getUid());
        friendList.add(user);
    }

    //Getting and Filtering UserList

    /**
     * Getting the Userlist from Firebase
     *
     * @return the list of useres
     */
    private List<User> getUserList() {
        //Getting online UserList from Firebase
        return Firebase.getAllUsers(UserDataSync.getUserToken());
    }

    /**
     * Filters the Friends out of the userList
     *
     * @return userList without the users who are already in the FriendList
     */
    private List<User> filterUserList(List<User> userList) {
        //filter the Friends out of it
        for (int i = userList.size() - 1; i >= 0; i--) {
            if (Objects.requireNonNull(friendList.contains(userList.get(i).getUid())) || userList.get(i).getUid().equals(uid)) {
                userList.remove(i);
            }
        }
        return userList;
    }

    /**
     * Returns a the userList without the useres who are already in the friendList
     *
     * @return the filtered userList
     */
    public List<User> getFilterdUserList() {
        return filterUserList(getUserList());
    }

}
