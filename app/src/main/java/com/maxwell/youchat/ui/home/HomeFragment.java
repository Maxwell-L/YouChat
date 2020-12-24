package com.maxwell.youchat.ui.home;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.icu.text.DateFormat;
import android.icu.text.SimpleDateFormat;
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
import com.maxwell.youchat.adapter.FriendChatAdapter;
import com.maxwell.youchat.entity.ChatMessage;
import com.maxwell.youchat.entity.ChatMessageDao;
import com.maxwell.youchat.entity.DaoSession;
import com.maxwell.youchat.entity.Friend;
import com.maxwell.youchat.entity.FriendDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class HomeFragment extends Fragment {

    private View root;
    private ListView listView;
    private List<HashMap<String, Object>> itemList;
    private FriendChatAdapter friendChatAdapter;
    private DaoSession daoSession;
    private FriendDao friendDao;
    private ChatMessageDao chatMessageDao;
    private Long userId;
//    private MessageReceiver receiver;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        initDb();
        userId = ((YouChatApplication) getActivity().getApplication()).getUserId();
        root = inflater.inflate(R.layout.fragment_home, container, false);
//        receiver = new MessageReceiver();
        initView();
//        IntentFilter filter = new IntentFilter();
//        filter.addAction("com.maxwell.youchat.service.WebSocketClientService");
//        getActivity().registerReceiver(receiver, filter);
        return root;
    }

    /**
     * 初始化 Db 相关
     */
    private void initDb() {
        daoSession = ((YouChatApplication) getActivity().getApplication()).getDaoSession();
        friendDao = daoSession.getFriendDao();
        chatMessageDao = daoSession.getChatMessageDao();
    }

    /**
     * 初始化 View
     */
    private void initView() {
        itemList = new ArrayList<>();
        listView = root.findViewById(R.id.chat_listview);
        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(getActivity(), ChatActivity.class);
            startActivity(intent);
        });
        setListViewAdapter();
    }

    /**
     * 从其它 activity 返回时更新数据
     */
    @Override
    public void onResume() {
        super.onResume();
        itemList = new ArrayList<>();
        setListViewAdapter();
    }

    /**
     * ListView 绑定 adpater
     */
    private void setListViewAdapter() {
        List<Friend> friendList = friendDao.queryRaw("WHERE LAST_MESSAGE_ID IS NOT null AND _id != " + userId);
        if (friendList.isEmpty()) {
            List<ChatMessage> chatMessages = chatMessageDao.queryRaw("WHERE SEND_USER_ID != " + userId);
            Set<Long> friendSet = new HashSet<>();
            for (int i = chatMessages.size() - 1; i >= 0; i--) {
                Long friendId = chatMessages.get(i).getSendUserId();
                if (!friendSet.contains(friendId)) {
                    List<Friend> friends = friendDao.queryRaw("WHERE _id = " + friendId);
                    if (friends.isEmpty()) {
                        Friend newFriend = new Friend(friendId, "用户" + friendId, 0, null, chatMessages.get(i).getId());
                        friendDao.insertOrReplace(newFriend);
                        friendList.add(newFriend);
                    } else {
                        friendList.add(friends.get(0));
                    }
                }
            }
        }
        for(Friend friend : friendList) {
            List<ChatMessage> chatMessages = chatMessageDao.queryRaw("WHERE RECEIVE_USER_ID = " + userId + " OR SEND_USER_ID = " + userId);

            ChatMessage chatMessage = chatMessages.get(chatMessages.size() - 1);
            friend.setLastMessageId(chatMessage.getId());
            HashMap<String, Object> newItem = new HashMap<>();
            newItem.put("username", friend.getUsername());
            newItem.put("message", chatMessage.getContent());
            Long createTime = chatMessage.getCreateTime();
            String date = timeFormat(createTime);
            newItem.put("time", date);
            newItem.put("icon", R.drawable.smile_face_24dp);
            itemList.add(newItem);
        }
        friendChatAdapter = new FriendChatAdapter(getActivity(), itemList);
        listView.setAdapter(friendChatAdapter);
    }

    /**
     * 时间格式转换
     * @param time
     * @return
     */
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

//    private class MessageReceiver extends BroadcastReceiver {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            Bundle bundle = intent.getExtras();
//            String message = bundle.getString("message");
//            if (message.charAt(0) != '{') {
//                return;
//            }
//
//        }
//    }
}
