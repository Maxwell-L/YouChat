package com.maxwell.youchat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;

import com.maxwell.youchat.R;
import com.maxwell.youchat.model.Message;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageAdapter extends SimpleAdapter {

    private Context context;
    private final static String[] FROM = new String[]{"content", "image"};
    private final static int[] TO = new int[]{R.id.message_content, R.id.message_sender_image};

    public MessageAdapter(Context context, List<? extends Map<String, ?>> data) {
        super(context, data, R.layout.message_item, FROM, TO);
    }
}
