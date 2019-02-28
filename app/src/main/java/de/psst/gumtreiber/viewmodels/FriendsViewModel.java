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
    private List<User> friendlist;

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        token = UserDataSync.getUserToken();
        fetchFriends();

    }

    public ArrayList<User> getFriendList() {
        return new ArrayList<>(friendlist);
    }

    private void fetchFriends() {
        friendlist = getOrCreateFriends();
    }

    private List<User> getOrCreateFriends() {
        //Firebase FriendList
        ArrayList<String> friendIdList = new ArrayList<>(Firebase.getFriendlist(uid, token));
        ArrayList<User> friendList = new ArrayList<>();
        for (String id : friendIdList) friendIdList.add(Firebase.getUser(id));
        return new ArrayList<>(Objects.requireNonNull(friendList));
    }


    /**
     * Delets a user from FriendList
     *
     * @param id of the user to be deleted
     */
    public void deleteFriend(String id) {
        //Delete from Firebase
        Firebase.deleteUserFromFriendlist(uid, id);
        //fetchFriends();
    }

    /**
     * Adds a new Friend to the FriendList
     *
     * @param id of the user to be added
     */
    public void addFriend(String id) {
        //Add to Firebase
        Firebase.addUserToFriendlist(uid, id);
        //fetchFriends();
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
            if (Objects.requireNonNull(friendlist.contains(userList.get(i).getUid()))) {
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
