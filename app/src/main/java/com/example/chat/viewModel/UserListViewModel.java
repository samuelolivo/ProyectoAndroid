package com.example.chat.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.chat.data.User;
import com.example.chat.repository.UserRepository;

import java.util.List;

public class UserListViewModel extends ViewModel {
    private final UserRepository repository;
    private final MutableLiveData<List<User>> userList = new MutableLiveData<>();

    public UserListViewModel() {
        repository = new UserRepository();
    }

    public LiveData<List<User>> getUserList() {
        return userList;
    }
    public void changeOnlineStatus(String uid, boolean isOnline){
        repository.updateOnlineStatus(uid, isOnline);

    }

    public void loadAllUsers(String miUid) {
        repository.observeAllUsers(miUid, userList);
    }
}