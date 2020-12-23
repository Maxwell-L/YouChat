package com.maxwell.youchat.ui.friend;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.activity.ChatActivity;
import com.maxwell.youchat.adapter.FriendListAdapter;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Friend;
import com.maxwell.youchat.entity.FriendDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FriendFragment extends Fragment {

    private View root;
    private ListView listView;
    private List<HashMap<String, Object>> itemList;
    private FriendListAdapter friendListAdapter;
    private DaoSession daoSession;
    private FriendDao friendDao;
    private Long userId;
    private FriendStateReceiver receiver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friend, container, false);
        userId = ((YouChatApplication) getActivity().getApplication()).getUserId();
        listView = root.findViewById(R.id.friend_list);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });
        initDb();
        initFriendList();
        receiver = new FriendStateReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.maxwell.youchat.service.WebSocketClientService");
        getActivity().registerReceiver(receiver, filter);
        return root;
    }

    private void initDb() {
        daoSession = ((YouChatApplication) getActivity().getApplication()).getDaoSession();
        friendDao = daoSession.getFriendDao();
    }

    private void initFriendList() {
        itemList = new ArrayList<>();
        List<Friend> friendList = friendDao.queryRaw("WHERE _id != " + userId);
        if (friendList.size() == 0) {
            Long friendId = userId ^ 1L;
            Friend newFriend = new Friend(friendId, "用户" + friendId, 0, null, null);
            friendDao.insert(newFriend);
        }
        friendList = friendDao.queryRaw("WHERE _id != " + userId);
        for (Friend friend : friendList) {
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("icon", R.drawable.smile_face_24dp);
            newItem.put("username", friend.getUsername());
            boolean isFriendOnline = ((YouChatApplication) getActivity().getApplication()).getIsFriendOnlineMap().get(friend.getId());
            String isFriendOnlineText = isFriendOnline ? "在线" : "离线";
            newItem.put("online", isFriendOnlineText);
            itemList.add(newItem);
        }
        friendListAdapter = new FriendListAdapter(this.getActivity(), itemList);
        listView.setAdapter(friendListAdapter);
    }

    private class FriendStateReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            String message = bundle.getString("message");
            if (message.charAt(0) == '{') {
                return;
            }
            YouChatApplication application = YouChatApplication.getInstance();
            if (application == null) {
                return;
            }
            itemList = new ArrayList<>();
            List<Friend> friendList = friendDao.queryRaw("WHERE _id != " + userId);
            if (friendList.size() == 0) {
                Long friendId = userId ^ 1L;
                Friend newFriend = new Friend(friendId, "用户" + friendId, 0, null, null);
                friendDao.insertOrReplace(newFriend);
            }
            friendList = friendDao.queryRaw("WHERE _id != " + userId);
            for (Friend friend : friendList) {
                HashMap<String, Object> newItem = new HashMap<>();
                newItem.put("icon", R.drawable.smile_face_24dp);
                newItem.put("username", friend.getUsername());
                Boolean isFriendOnline = application.getIsFriendOnlineMap().get(friend.getId());
                String isFriendOnlineText = (isFriendOnline != null && isFriendOnline) ? "在线" : "离线";
                newItem.put("online", isFriendOnlineText);
                itemList.add(newItem);
            }
            friendListAdapter = new FriendListAdapter(context, itemList);
            listView.setAdapter(friendListAdapter);
        }
    }
}
