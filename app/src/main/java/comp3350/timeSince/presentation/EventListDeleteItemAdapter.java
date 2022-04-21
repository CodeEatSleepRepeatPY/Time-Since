package comp3350.timeSince.presentation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventDSO;

public class EventListDeleteItemAdapter extends RecyclerView.Adapter<EventListDeleteItemAdapter.MyViewHolder> {
    private final List<EventDSO> eventList;
    private final RecyclerViewClickOnListener listener;

    public EventListDeleteItemAdapter(List<EventDSO> eventList, RecyclerViewClickOnListener listener) {
        this.eventList = eventList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventListDeleteItemAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_items_for_deletion, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull EventListDeleteItemAdapter.MyViewHolder holder, int position) {
        String name = eventList.get(position).getName();
        holder.eventName.setText(name);
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public interface RecyclerViewClickOnListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final TextView eventName;
        private final Button deleteEvent;

        public MyViewHolder(final View view) {
            super(view);
            eventName = view.findViewById(R.id.event_title2);
            deleteEvent = view.findViewById(R.id.delete_event2);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
