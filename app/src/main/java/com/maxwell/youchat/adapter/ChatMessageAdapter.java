package com.maxwell.youchat.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.entity.ChatMessage;

import java.util.List;

public class ChatMessageAdapter extends BaseAdapter {

    private Context context;
    private List<ChatMessage> chatMessageList;

    public ChatMessageAdapter(Context context, List<ChatMessage> chatMessageList) {
        this.context = context;
        this.chatMessageList = chatMessageList;
    }

    @Override
    public int getCount() {
        return chatMessageList.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessageList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessage chatMessage = chatMessageList.get(position);
        String content = chatMessage.getContent();
        Long sendUserId = chatMessage.getSendUserId();
        Long userId = YouChatApplication.getInstance().getUserId();
        if(sendUserId == userId) {
            convertView = View.inflate(context, R.layout.send_message_item, null);
            TextView tv = convertView.findViewById(R.id.send_message_content);
            ImageView iv = convertView.findViewById(R.id.message_sender_image);
            tv.setText(content);
            iv.setImageResource(R.drawable.smile_face_24dp);
        } else {
            convertView = View.inflate(context, R.layout.receive_message_item, null);
            TextView tv = convertView.findViewById(R.id.receive_message_content);
            ImageView iv = convertView.findViewById(R.id.message_receiver_image);
            tv.setText(content);
            iv.setImageResource(R.drawable.smile_face_24dp);
        }
        return convertView;
    }
}
