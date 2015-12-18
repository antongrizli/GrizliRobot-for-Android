package com.antongrizli.grizlirobot.tabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputFilter;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.antongrizli.grizlirobot.R;
import com.antongrizli.grizlirobot.model.async_task.ActionModel;
import com.antongrizli.grizlirobot.model.TransferModel;
import com.antongrizli.grizlirobot.model.input_filter.InputDateFilter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import twitter4j.Query;


public class SearchingTab extends Fragment {
    private static final String KEY_TITLE = "title";
    private static final String KEY_SCENE_VIEW = "scene_view";
    private static final String SEARCH_TAB = "search";

    private EditText etDateSince;
    private EditText etDateUntil;

    private Spinner sLang;
    private SeekBar sbCount;
    private TextView twCountSeekBar;
    private RadioButton rbMixed;
    private RadioButton rbRecent;
    private RadioButton rbPopular;


    public static SearchingTab newInstance(CharSequence title, int sceneView) {
        Bundle bundle = new Bundle();
        bundle.putCharSequence(KEY_TITLE, title);
        bundle.putInt(KEY_SCENE_VIEW, sceneView);

        SearchingTab fragment = new SearchingTab();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search_tab, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initComponent(view);
    }

    private void initComponent(View view) {
        Button bSearch = (Button) view.findViewById(R.id.bSearch);
        final EditText etSearch = (EditText) view.findViewById(R.id.etSearch);
        etSearch.setHint(R.string.search);
        etDateSince = (EditText) view.findViewById(R.id.etDateSince);
        etDateSince.setFilters(new InputFilter[]{new InputDateFilter()});
        etDateUntil = (EditText) view.findViewById(R.id.etDateUntil);
        etDateUntil.setFilters(new InputFilter[]{new InputDateFilter()});
        final CheckBox chkCreateFriends = (CheckBox) view.findViewById(R.id.create_friend);
        final CheckBox chkFollowTweet = (CheckBox) view.findViewById(R.id.follow_tweet);
        Log.i(SEARCH_TAB, etSearch.getText().toString());

        String[] countriesNames = getResources().getStringArray(R.array.country_names);
        String[] countriesCodes = getResources().getStringArray(R.array.countries_codes);

        final HashMap<String, String> countriesMap = new HashMap<String, String>();
        List<String> coutriesList = new ArrayList<>();
        for (int i = 0; i < countriesNames.length; i++) {
            countriesMap.put(countriesNames[i], countriesCodes[i]);
            coutriesList.add(countriesNames[i]);
        }
        sLang = (Spinner) view.findViewById(R.id.language);
        ArrayAdapter<String> lang = new ArrayAdapter<String>(getContext(), R.layout.support_simple_spinner_dropdown_item, coutriesList);
        lang.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        sLang.setAdapter(lang);
        sLang.setPrompt("Choice language");

        twCountSeekBar = (TextView) view.findViewById(R.id.countSekBar);
        twCountSeekBar.setText("15");

        sbCount = (SeekBar) view.findViewById(R.id.seekBar);
        sbCount.setMax(85);
        sbCount.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                twCountSeekBar.setText(String.valueOf(seekBar.getProgress() + 15));
            }
        });
        final Query query = new Query();
        rbMixed = (RadioButton) view.findViewById(R.id.radio_mixed);
        rbMixed.setChecked(true);
        //default settings
        query.setResultType(Query.ResultType.mixed);
        query.setLang(Locale.getDefault().getLanguage());
        //end default settings
        rbPopular = (RadioButton) view.findViewById(R.id.radio_popular);
        rbRecent = (RadioButton) view.findViewById(R.id.radio_recent);

        View.OnClickListener buttonListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.radio_mixed:
                        query.setResultType(Query.ResultType.mixed);
                        System.out.println(Query.ResultType.mixed.toString());
                        break;
                    case R.id.radio_popular:
                        query.setResultType(Query.ResultType.popular);
                        System.out.println(Query.ResultType.popular.toString());
                        break;
                    case R.id.radio_recent:
                        query.setResultType(Query.ResultType.recent);
                        System.out.println(Query.ResultType.recent.toString());
                        break;
                    case R.id.bSearch:
                        System.out.println("query type: " + query.getResultType());
                        if (!TextUtils.isEmpty(etSearch.getText().toString())) {

                            query.setQuery(etSearch.getText().toString());
                            System.out.println("query text: " + etSearch.getText().toString());
                            System.out.println("query lang: " + query.getLang());

                            query.setCount(Integer.parseInt(twCountSeekBar.getText().toString()));
                            if (!TextUtils.isEmpty(etDateSince.getText().toString())) {
                                query.setSince(formatDate(etDateSince.getText().toString()));
                            }
                            if (!TextUtils.isEmpty(etDateUntil.getText().toString())) {
                                query.setUntil(formatDate(etDateUntil.getText().toString()));
                            }
                            Log.i(SEARCH_TAB, "Run search" + query);
                            ActionModel model = new ActionModel();
                            model.setContext(getContext());
                            model.execute(new TransferModel(query, chkCreateFriends.isChecked(), chkFollowTweet.isChecked()));
                        }
                        break;
                }
            }
        };
        rbRecent.setOnClickListener(buttonListener);
        rbPopular.setOnClickListener(buttonListener);
        rbMixed.setOnClickListener(buttonListener);
        sLang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    query.setLang(Locale.getDefault().getLanguage());
                } else {
                    query.setLang(countriesMap.get(parent.getSelectedItem().toString()));
                }
                System.out.println("position " + position);
                System.out.println("id " + id);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        bSearch.setOnClickListener(buttonListener);
    }

    private String formatDate(String date) {
        if (!date.isEmpty()) {
            SimpleDateFormat format = new SimpleDateFormat("ddmmyyyy");
            SimpleDateFormat converTo = new SimpleDateFormat("yyyy-mm-dd");
            String result = null;
            Date dateConverted;
            try {
                dateConverted = format.parse(date);
                result = converTo.format(dateConverted);
            } catch (ParseException e) {
                Log.e(SEARCH_TAB, e.toString());
            }
            return result;
        }
        return null;
    }
}