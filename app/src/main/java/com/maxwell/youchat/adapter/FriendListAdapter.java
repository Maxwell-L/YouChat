package com.maxwell.youchat.adapter;

import android.content.Context;
import android.widget.SimpleAdapter;

import com.maxwell.youchat.R;

import java.util.List;
import java.util.Map;

public class FriendListAdapter extends SimpleAdapter {
    private static String[] FROM = new String[]{"icon", "username", "online"};
    private static int[] TO = new int[]{R.id.friend_icon, R.id.friend_name, R.id.is_online};

    public FriendListAdapter(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.friend_item, FROM, TO);
    }
}
