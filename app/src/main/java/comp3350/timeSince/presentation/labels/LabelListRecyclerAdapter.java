package comp3350.timeSince.presentation.labels;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class LabelListRecyclerAdapter extends RecyclerView.Adapter<LabelListRecyclerAdapter.MyViewHolder> {
    private List<EventLabelDSO> labelList;
    private List<EventLabelDSO> allLabels;
    private RecyclerViewClickOnListener listener;
    private EventDSO event;
    private EventManager eventManager;
    private UserEventManager userEventManager;

    public LabelListRecyclerAdapter(UserEventManager userEventManager,
                                    List<EventLabelDSO> labelList, List<EventLabelDSO> allLabels,
                                    EventDSO event, RecyclerViewClickOnListener listener) {
        this.userEventManager = userEventManager;
        this.labelList = labelList;
        this.allLabels = allLabels;
        sortLabels();
        this.event = event;
        this.listener = listener;
        eventManager = new EventManager(true);
    }

    @NonNull
    @Override
    public LabelListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                    int viewType) {
        View itemView =
                LayoutInflater.from(parent.getContext()).inflate(R.layout.label_list_items,
                        parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelListRecyclerAdapter.MyViewHolder holder,
                                 int position) {
        EventLabelDSO label = allLabels.get(position);
        String name = label.toString();
        holder.labelName.setText(name);
        if (labelList.contains(label)) {
            holder.selectLabel.setChecked(true);
        }
        if (holder.selectLabel.isChecked() && !labelList.contains(label)) {
            event = eventManager.addLabelToEvent(event, label);
            labelList = event.getEventLabels();
            allLabels = userEventManager.getUserLabels();
            sortLabels();
        }
    }

    private void sortLabels() {
        allLabels.sort(Comparator.comparing(EventLabelDSO::getName, Comparator
                .nullsLast(String::compareToIgnoreCase)));
    }

    @Override
    public int getItemCount() {
        return allLabels.size();
    }

    public interface RecyclerViewClickOnListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView labelName;
        private CheckBox selectLabel;

        public MyViewHolder(final View view) {
            super(view);
            labelName = view.findViewById(R.id.label_tile);
            selectLabel = view.findViewById(R.id.event_has_label_checkbox);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            sortLabels();
            listener.onClick(view, getAdapterPosition());
        }
    }
}
