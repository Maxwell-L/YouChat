package com.maxwell.youchat.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class HomeViewModel extends ViewModel {

    private MutableLiveData<String> mutableLiveData;

    public HomeViewModel() {
        mutableLiveData = new MutableLiveData<>();
        mutableLiveData.setValue("This is home fragment");
    }

    public LiveData<String> getText() {
        return mutableLiveData;
    }
}
