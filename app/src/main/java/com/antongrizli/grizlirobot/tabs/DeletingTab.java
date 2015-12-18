package com.antongrizli.grizlirobot.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;

import com.antongrizli.grizlirobot.R;
import com.antongrizli.grizlirobot.model.DeleteFriendsAndTweets;
import com.antongrizli.grizlirobot.model.async_task.DeleteModel;

import java.util.Date;

public class DeletingTab extends Fragment {
    private static final String KEY_TITLE = "title";
    private static final String KEY_SCENE_VIEW = "scene_view";

    public static DeletingTab newInstance(CharSequence title, int sceneView) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_SCENE_VIEW, sceneView);

        DeletingTab fragment = new DeletingTab();
        fragment.setArguments(bundle);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.delete_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button bDelete = (Button) view.findViewById(R.id.bRunDelete);
        final CheckBox cbDeleteFriends = (CheckBox) view.findViewById(R.id.cbDeleteFriend);
        final CheckBox cbUnsubscribeTweets = (CheckBox) view.findViewById(R.id.cbUnsubscribeTweets);
        final DatePicker lastDate = (DatePicker) view.findViewById(R.id.datePicker);

        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DeleteModel deleteModel = new DeleteModel();
                deleteModel.setContext(getContext());
                deleteModel.execute(new DeleteFriendsAndTweets(cbDeleteFriends.isChecked(), cbUnsubscribeTweets.isChecked(), new Date(lastDate.getCalendarView().getDate())));
            }
        });
    }
}

