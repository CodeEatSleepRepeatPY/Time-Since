package comp3350.timeSince.presentation;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

import comp3350.timeSince.objects.EventLabelDSO;

public class SpinnerEventLabelList extends ArrayAdapter<EventLabelDSO> {
    private Context mContext;
    private ArrayList<EventLabelDSO> eventTags;

    public SpinnerEventLabelList(Context context,
                                 int textViewResourceId,
                                 ArrayList<EventLabelDSO> objects){
        super(context, textViewResourceId, objects);
        this.mContext = context;
        this.eventTags = objects;
    }

    @Override
    public int getCount(){
        return eventTags.size();
    }

    @Override
    public EventLabelDSO getItem(int position){
        return eventTags.get(position);
    }

    @Override
    public long getItemId(int position){
        return position;
    }

    @Override
    public View getView(int position, View newView, ViewGroup parent){
        //default state
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        label.setText(" "); //+eventTags.get(position).getName());
        label.setHeight(50);
        label.setGravity(Gravity.LEFT | Gravity.CENTER);

        return label;
    }

    @Override
    public View getDropDownView(int position, View newView, ViewGroup parent){
        //get the list
        TextView label = new TextView(mContext);
        label.setTextColor(Color.BLACK);
        label.setTextSize(16);
        label.setText(" "+eventTags.get(position).getName());
        label.setHeight(50);
        label.setGravity(Gravity.LEFT | Gravity.CENTER);

        return label;

    }


}
