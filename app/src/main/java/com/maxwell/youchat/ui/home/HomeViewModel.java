package com.maxwell.youchat.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.HashMap;
import java.util.List;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveData;

    private MutableLiveData<List<HashMap<String, Object>>> itemList;

    public HomeViewModel() {
        mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mutableLiveData;
    }
}
