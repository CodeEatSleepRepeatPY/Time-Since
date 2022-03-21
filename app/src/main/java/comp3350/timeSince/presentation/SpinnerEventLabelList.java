package comp3350.timeSince.presentation;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import comp3350.timeSince.objects.EventLabelDSO;

public class SpinnerEventLabelList extends ArrayAdapter<EventLabelDSO> {
    private final Context mContext;
    private final ArrayList<EventLabelDSO> eventTags;
    private final static int TEXT_SIZE = 16;
    private final static int TEXT_HEIGHT = 50;

    public SpinnerEventLabelList(Context context, int textViewResourceId, ArrayList<EventLabelDSO> objects) {
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.eventTags = objects;
    }

    @Override
    public int getCount() {
        return eventTags.size();
    }

    @Override
    public EventLabelDSO getItem(int position) {
        return eventTags.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View newView, ViewGroup parent) {
        //default state
        return new TextView(mContext);
    }

    @Override
    public View getDropDownView(int position, View newView, ViewGroup parent) {
        //get the list, setting of the labels
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setTextSize(TEXT_SIZE);
        label.setText(eventTags.get(position).getName());
        label.setHeight(TEXT_HEIGHT);
        label.setGravity(Gravity.LEFT | Gravity.CENTER);

        return label;
    }

}

