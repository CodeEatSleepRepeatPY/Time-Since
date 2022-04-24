package comp3350.timeSince.presentation.labels;

import static comp3350.timeSince.R.color.lightGreen;
import static comp3350.timeSince.R.color.time_since_green;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventLabelDSO;

public class LabelListRecyclerAdapter extends RecyclerView.Adapter<LabelListRecyclerAdapter.MyViewHolder> {
    private final List<EventLabelDSO> labelList;
    private final List<EventLabelDSO> allLabels;
    private final RecyclerViewClickOnListener listener;

    public LabelListRecyclerAdapter(List<EventLabelDSO> labelList, List<EventLabelDSO> allLabels,
                                    RecyclerViewClickOnListener listener) {
        this.labelList = labelList;
        this.allLabels = allLabels;
        this.listener = listener;
        sortLabels();
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

        Context context = holder.labelCard.getContext();
        EventLabelDSO label = allLabels.get(position);
        String name = label.toString();
        holder.labelName.setText(name); // the label name
        holder.labelCard.setTag(position); // to be able to identify which label

        // set the label color
        if (labelList.contains(label)) {
            holder.labelCard.setBackgroundColor(ContextCompat.getColor(context, time_since_green));
        } else {
            holder.labelCard.setBackgroundColor(ContextCompat.getColor(context, lightGreen));
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
        private final TextView labelName;
        private final CardView labelCard;

        public MyViewHolder(final View view) {
            super(view);
            labelName = view.findViewById(R.id.label_tile);
            labelCard = view.findViewById(R.id.label_card);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

}
