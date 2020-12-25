package com.maxwell.youchat.ui.me;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.maxwell.youchat.R;
import com.maxwell.youchat.YouChatApplication;
import com.maxwell.youchat.activity.LoginActivity;
import com.maxwell.youchat.service.WebSocketClientService;

public class MeFragment extends Fragment {

    private View root;

    private Button logoutButton;
    private TextView textView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        root = inflater.inflate(R.layout.fragment_me, container, false);
        textView = root.findViewById(R.id.my_username);
        logoutButton = root.findViewById(R.id.logout_button);
        String username = "用户" + ((YouChatApplication) getActivity().getApplication()).getUserId();
        textView.setText(username);
        logoutButton.setOnClickListener(v -> {
            // 退出登录
            AlertDialog alertDialog = new AlertDialog.Builder(getActivity())
                    .setTitle("退出登录")
                    .setMessage("您确定要退出登录吗?")
                    .setPositiveButton("确定", (dialog, which) -> {
                        ((YouChatApplication) getActivity().getApplication()).getClient().close();
                        ((YouChatApplication) getActivity().getApplication()).setClient(null);
                        Intent stopWebSocketClientService = new Intent(getActivity(), WebSocketClientService.class);
                        getActivity().stopService(stopWebSocketClientService);
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        getActivity().finish();
                    })
                    .setNegativeButton("取消", ((dialog, which) -> {
                        return;
                    }))
                    .create();
            alertDialog.show();
        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
