package com.maxwell.youchat.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatActivityViewModel extends ViewModel {
    private MutableLiveData<List<HashMap<String, Object>>> itemList;

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
