package com.antongrizli.grizlirobot.tabs;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.antongrizli.grizlirobot.R;
import com.antongrizli.grizlirobot.SomeTweet;
import com.antongrizli.grizlirobot.model.Model;
import com.antongrizli.grizlirobot.model.adapter.ListViewAdapter;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;


public class MainTab extends Fragment {

    private static final String KEY_TITLE = "title";
    private static final String KEY_SCENE_VIEW = "scene_view";
    private List<Model> modelDatas = new ArrayList<>();
    private ListView listItems;

    public static MainTab newInstance(CharSequence title, int sceneView) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_SCENE_VIEW, sceneView);

        MainTab fragment = new MainTab();
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.main_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        listItems = (ListView) view.findViewById(R.id.listView);
        updateListView();
    }

    public void updateListView() {
        if (!modelDatas.isEmpty()) {
            listItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent openTweet = new Intent(getContext(), SomeTweet.class);
                    openTweet.setAction(Intent.ACTION_SEND);
                    //openTweet.putExtra(Intent.EXTRA_TEXT, ((Model) parent.getAdapter().getItem(position)).getTweetID().toString());
                    Bundle serializableValueStatus = new Bundle();
                    serializableValueStatus.putSerializable("status",((Model) parent.getAdapter().getItem(position)).getStatus());
                    openTweet.putExtras(serializableValueStatus);
                    //openTweet.setType("text/plain");
                    startActivity(openTweet);
                }
            });
            listItems.setAdapter(new ListViewAdapter(getActivity(), modelDatas));
            System.out.println("update list");
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    public void onEvent(List<Model> models) {
        this.modelDatas = models;
        updateListView();
        System.out.println("onEvent: set data to list " + models.isEmpty());
    }
}