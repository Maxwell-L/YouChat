package com.maxwell.youchat.ui.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
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

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_friend, container, false);
        userId = ((YouChatApplication) getActivity().getApplication()).getUserId();
        initDb();
        initFriendList();
        return root;
    }

    private void initDb() {
        daoSession = ((YouChatApplication) getActivity().getApplication()).getDaoSession();
        friendDao = daoSession.getFriendDao();
    }

    private void initFriendList() {
        listView = root.findViewById(R.id.friend_list);
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
            newItem.put("online", "离线");
            itemList.add(newItem);
        }
        friendListAdapter = new FriendListAdapter(this.getActivity(), itemList);
        listView.setAdapter(friendListAdapter);
    }
}
