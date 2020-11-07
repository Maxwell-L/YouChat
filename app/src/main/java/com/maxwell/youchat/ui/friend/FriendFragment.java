package com.maxwell.youchat.ui.friend;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.maxwell.youchat.R;

public class FriendFragment extends Fragment {

    private FriendViewModel friendViewModel;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        friendViewModel = new ViewModelProvider(this).get(FriendViewModel.class);
        View root = inflater.inflate(R.layout.fragment_friend, container, false);
        final TextView textView = root.findViewById(R.id.text_friend);
        friendViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(String s) {
                textView.setText(s);
            }
        });
        return root;
    }
}
