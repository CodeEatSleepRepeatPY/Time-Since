package comp3350.timeSince.presentation.eventsList;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class EventListRecyclerAdapter extends RecyclerView.Adapter<EventListRecyclerAdapter.MyViewHolder> {
    private List<EventDSO> eventList;
    private RecyclerViewClickOnListener listener;

    public EventListRecyclerAdapter(List<EventDSO> eventList, RecyclerViewClickOnListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListRecyclerAdapter.MyViewHolder holder, int position) {
        EventDSO event = eventList.get(position);
        String name = event.getName();
        holder.eventName.setText(name);

        Calendar dueDateTime = event.getTargetFinishTime();
        if (dueDateTime != null) {
            holder.eventDueDateTime.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dueDateTime.getTime()));
        } else {
            holder.eventDueDateTime.setText("Not set");
        }
        holder.labelList.setText(setLabelList(event));
        if (event.checkDueClosing()) {
            holder.eventName.setTextColor(Color.RED);
            holder.eventDueDateTime.setTextColor(Color.RED);
        }
    }

    private String setLabelList(EventDSO event) {
        String showLabels = "";
        List<EventLabelDSO> labels = event.getEventLabels();
        for (EventLabelDSO label : labels) {
            showLabels += label.toString() + " ";
        }
       return showLabels;
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface RecyclerViewClickOnListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView eventName;
        private TextView eventDueDateTime;
        private TextView labelList;

        public MyViewHolder(final View view) {
            super(view);
            eventName = view.findViewById(R.id.event_tile);
            eventDueDateTime = view.findViewById(R.id.event_due_time);
            labelList = view.findViewById(R.id.list_event_labels);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
