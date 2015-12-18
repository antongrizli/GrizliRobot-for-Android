package com.antongrizli.grizlirobot.model.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.antongrizli.grizlirobot.R;
import com.antongrizli.grizlirobot.model.Model;

import java.util.List;

public class ListViewAdapter extends ArrayAdapter<Model> {
    private final List<Model> list;
    private final Activity context;

    public ListViewAdapter(Activity context, List<Model> list) {
        super(context, R.layout.list_main_activity, list);
        this.context = context;
        this.list = list;
    }

    static class ViewHolder {
        protected TextView text;
        protected TextView twName;
        protected CheckBox checkbox;
        protected TextView twCreatedDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView == null) {
            LayoutInflater inflator = context.getLayoutInflater();
            view = inflator.inflate(R.layout.list_main_activity, null);
            final ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) view.findViewById(R.id.label);
            viewHolder.checkbox = (CheckBox) view.findViewById(R.id.check);

            WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Display display = wm.getDefaultDisplay();

            viewHolder.text.setWidth(display.getWidth() - 100);

            viewHolder.twName = (TextView) view.findViewById(R.id.user_name);

            viewHolder.twCreatedDate = (TextView) view.findViewById(R.id.twCreatedDate);


            viewHolder.checkbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Model element = (Model) viewHolder.checkbox.getTag();
                    element.setSelected(buttonView.isChecked());
                }
            });
            view.setTag(viewHolder);
            viewHolder.checkbox.setTag(list.get(position));
        } else {
            view = convertView;
            ((ViewHolder) view.getTag()).checkbox.setTag(list.get(position));
        }
        ViewHolder holder = (ViewHolder) view.getTag();
        holder.text.setText(list.get(position).getTweetText());

       /*RelativeLayout.LayoutParams paramsText = (RelativeLayout.LayoutParams) holder.twName.getLayoutParams();
        paramsText.setMargins(0, holder.text.getHeight(), 0, 0);
        holder.twName.setLayoutParams(paramsText);*/

        holder.twName.setText("Screen name: " + list.get(position).getUserName());
        holder.twCreatedDate.setText("Date "+list.get(position).getDate_tweet());
        holder.checkbox.setChecked(list.get(position).isSelected());
        return view;
    }
}
