package com.maxwell.youchat.adapter;

import android.content.Context;
import android.widget.SimpleAdapter;

import com.maxwell.youchat.R;

import java.util.List;
import java.util.Map;

public class FriendChatAdapter extends SimpleAdapter {

    private static String[] FROM = new String[]{"username", "message", "time", "icon"};
    private static int[] TO = new int[]{R.id.chat_receiver_name, R.id.chat_last_message, R.id.chat_time, R.id.chat_receiver_icon};


    public FriendChatAdapter(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.chat_item, FROM, TO);
    }
}
