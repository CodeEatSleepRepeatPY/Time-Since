package comp3350.timeSince.presentation;

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
        String name = eventList.get(position).getName();
        holder.eventName.setText(name);

        Calendar dueDateTime = eventList.get(position).getTargetFinishTime();
        if (dueDateTime != null) {
            holder.eventDueDateTime.setText(new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(dueDateTime.getTime()));
        } else {
            holder.eventDueDateTime.setText("Not set");
        }

        if (eventList.get(position).checkDueClosing()) {
            holder.eventName.setTextColor(Color.RED);
            holder.eventDueDateTime.setTextColor(Color.RED);
        }
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

        public MyViewHolder(final View view) {
            super(view);
            eventName = view.findViewById(R.id.event_tile);
            eventDueDateTime = view.findViewById(R.id.event_due_time);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
