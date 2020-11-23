package com.maxwell.youchat.ui.home;

import android.content.Intent;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.activity.ChatActivity;
import com.maxwell.youchat.adapter.FriendChatAdapter;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Friend;
import com.maxwell.youchat.entity.FriendDao;
import com.maxwell.youchat.entity.Message;
import com.maxwell.youchat.entity.MessageDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class HomeFragment extends Fragment {

//    private HomeViewModel homeViewModel;
    private View root;
    private Button button;
    private ListView listView;
    private List<HashMap<String, Object>> itemList;
    private FriendChatAdapter friendChatAdapter;
    private DaoSession daoSession;
    private FriendDao friendDao;
    private MessageDao messageDao;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
//        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        root = inflater.inflate(R.layout.fragment_home, container, false);
        daoSession = ((YouChatApplication)getActivity().getApplication()).getDaoSession();
        friendDao = daoSession.getFriendDao();
        messageDao = daoSession.getMessageDao();
        itemList = new ArrayList<>();
        listView = root.findViewById(R.id.chat_listview);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });
        setListViewAdapter();
        button = root.findViewById(R.id.button);
        button.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });
        return root;
    }

    @Override
    public void onResume() {
        super.onResume();
        itemList = new ArrayList<>();
        setListViewAdapter();
    }

    private void setListViewAdapter() {
        List<Friend> friendList = friendDao.queryRaw("WHERE LAST_MESSAGE_ID IS NOT null");
        for(Friend friend : friendList) {
            Long messageId = friend.getLastMessageId();
            List<Message> messages = messageDao.queryRaw("WHERE _id = " + messageId);
            Message message = messages.get(0);
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("username", friend.getUsername());
            newItem.put("message", message.getContent());
            Long createTime = message.getCreateTime();
            String date = timeFormat(createTime);
            newItem.put("time", date);
            newItem.put("icon", R.drawable.smile_face_24dp);
            itemList.add(newItem);
        }
        friendChatAdapter = new FriendChatAdapter(getActivity(), itemList);
        listView.setAdapter(friendChatAdapter);
    }

    private String timeFormat(Long time) {
        StringBuilder res = new StringBuilder();
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = dateFormat.format(time);
        String[] dates = date.split(" ");
        String[] monthAndDay = dates[0].split("-");
        res.append(monthAndDay[1] + "月" + monthAndDay[2] + "日 ");
        String[] hourAndMinute = dates[1].split(":");
        res.append(hourAndMinute[0] + ":" + hourAndMinute[1]);
        return res.toString();
    }
}
