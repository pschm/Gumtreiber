package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FriendsViewModel extends AndroidViewModel {

    //TODO Echte firebase Userliste Abrufen!

    private static final String PREFERENCES_KEY = "de.psst.gumtreiber";
    private static final String FRIENDLIST_KEY = "friendList";

    //TODO Später vielleicht erstezen -> Name Überdenken
    private MutableLiveData<List<String>> friends = new MutableLiveData<>();

    public FriendsViewModel(@NonNull Application application) {
        super(application);
        fetchFriends();

    }

    public SharedPreferences getPreferences() {
        return getApplication().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    public LiveData getFriends() {
        return friends;
    }

    private void fetchFriends() {
        friends.setValue(getOrCreateFriends());
    }

    private List<String> getOrCreateFriends() {
        Set<String> dbFriends = getPreferences().getStringSet(FRIENDLIST_KEY, new HashSet<>());
        return new ArrayList<>(Objects.requireNonNull(dbFriends));
    }

    private List<String> saveFriends(List<String> friends) {
        getPreferences().edit().putStringSet(FRIENDLIST_KEY, new HashSet<>(friends)).apply();
        return friends;
    }

    //Momentan noch name
    public void deleteFriend(String id) {
        //Aus der SharedPreference löschen
        List<String> friendList = friends.getValue();
        if (friendList != null)
            friendList.remove(id);
        friends.setValue(saveFriends(friendList));

    }
}
