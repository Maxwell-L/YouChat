package com.maxwell.youchat.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.maxwell.youchat.entity.DaoSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivityViewModel extends ViewModel {
    private MutableLiveData<List<HashMap<String, Object>>> itemList;

    private DaoSession daoSession;

    public ChatActivityViewModel() {
        if(itemList == null) {
            this.itemList = new MutableLiveData<>();
            itemList.setValue(new ArrayList<>());
        }
    }

    public  List<HashMap<String, Object>> getItemList() {
        return itemList.getValue();
    }

    public void addItem(HashMap<String, Object> newItem) {
        itemList.getValue().add(newItem);
    }
}
