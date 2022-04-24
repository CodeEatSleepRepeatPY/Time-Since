package comp3350.timeSince.presentation.labels;

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

public class LabelListRecyclerAdapter extends RecyclerView.Adapter<LabelListRecyclerAdapter.MyViewHolder> {
    private List<EventLabelDSO> labelList;
    private RecyclerViewClickOnListener listener;

    public LabelListRecyclerAdapter(List<EventLabelDSO> labelList, RecyclerViewClickOnListener listener) {
        this.labelList = labelList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public LabelListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.label_list_items, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull LabelListRecyclerAdapter.MyViewHolder holder, int position) {
        String name = labelList.get(position).getName();
        if (name != null) {
            holder.labelName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        return labelList.size();
    }

    public interface RecyclerViewClickOnListener {
        void onClick(View view, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView labelName;

        public MyViewHolder(final View view) {
            super(view);
            labelName = view.findViewById(R.id.label_tile);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }
}
