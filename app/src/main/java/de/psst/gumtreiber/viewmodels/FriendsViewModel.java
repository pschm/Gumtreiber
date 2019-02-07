package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeSet;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

public class FriendsViewModel extends AndroidViewModel {

    private static final String PREFERENCES_KEY = "de.psst.gumtreiber";
    private static final String FRIENDLIST_KEY = "friendList";

    //TODO Später erstezen -> Name Überdenken
    private MutableLiveData<List<String>> friends;

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
        friends.setValue(new ArrayList<>(getPreferences().getStringSet(FRIENDLIST_KEY, new TreeSet<String>())));
    }

    //Momentan noch name
    public void deleteFriend(String id) {
        //Aus der SharedPreference löschen
        List<String> friendList = friends.getValue();
        friendList.remove(id);
        SharedPreferences.Editor editor = getPreferences().edit();
        editor.remove(FRIENDLIST_KEY);
        editor.putStringSet(FRIENDLIST_KEY, new HashSet<>(friendList));
        editor.apply();
        friends.setValue(friendList);

    }
}
