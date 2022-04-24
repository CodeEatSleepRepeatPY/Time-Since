package comp3350.timeSince.presentation.labels;

import static comp3350.timeSince.R.color.*;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.presentation.HomeActivity;
import comp3350.timeSince.presentation.events.SingleEventActivity;
import comp3350.timeSince.presentation.eventsList.ViewOwnEventListActivity;

public class LabelListActivity extends AppCompatActivity {
    private List<EventLabelDSO> labelList;
    private List<EventLabelDSO> allLabels;
    private List<EventLabelDSO> labelsToAdd;
    private List<EventLabelDSO> labelsToRemove;
    private RecyclerView recyclerView;
    private EventManager eventManager;
    private UserEventManager userEventManager;
    private Bundle extras;
    private LabelListRecyclerAdapter.RecyclerViewClickOnListener listener;
    private String userID;
    private int eventID;
    private UserDSO user;
    private EventDSO event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.labelRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        extras = getIntent().getExtras();
        userID = extras.get("email").toString();
        eventID = extras.getInt("eventID");

        eventManager = new EventManager(true);

        try {
            userEventManager = new UserEventManager(userID, true);
            event = eventManager.getEventByID(eventID);
            if (event != null) {
                labelList = event.getEventLabels();
                allLabels = userEventManager.getUserLabels();
                labelsToAdd = new ArrayList<>();
                labelsToRemove = new ArrayList<>();
                setAdapter();
            }
        } catch (UserNotFoundException ue) {
            ue.printStackTrace();
            moveBackToHome();
        } catch (Exception e) {
            e.printStackTrace();
            moveBackToList();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_labels, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        boolean ret_value = true;

        if (item.getItemId() == R.id.add_new_label) {
            Intent intent = new Intent(LabelListActivity.this, CreateOwnLabelActivity.class);
            intent.putExtra("email", userID);
            intent.putExtra("eventID", eventID);
            finish();
            LabelListActivity.this.startActivity(intent);
        } else {
            ret_value = super.onOptionsItemSelected(item);
        }
        return ret_value;
    }

    private void saveState() {
        for (EventLabelDSO label : allLabels) {
            if (labelsToAdd.contains(label)) {
                event = eventManager.addLabelToEvent(event, label);
            }
            if (labelsToRemove.contains(label)) {
                event = eventManager.removeLabelFromEvent(event, label);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        saveState();
        Intent intent = new Intent(getApplicationContext(), SingleEventActivity.class);
        intent.putExtra("email", userID);
        intent.putExtra("eventID", event.getID());
        finish();  // end this activity before starting the next
        startActivity(intent);
        return true;
    }

    private void setAdapter() {
        setOnClickListener();
        LabelListRecyclerAdapter adapter = new LabelListRecyclerAdapter(labelList,
                allLabels,  listener);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new LabelListRecyclerAdapter.RecyclerViewClickOnListener() {
            @Override
            public void onClick(View view, int position) {
                cardOnClick(view);
            }
        };
    }

    private void setCardOnClickListener() {

    }

    public void cardOnClick(View view) {
        CardView card = view.findViewById(R.id.label_card);
        EventLabelDSO label = allLabels.get((int) card.getTag());
        if (!labelList.contains(label) || labelsToRemove.contains(label)) {
            labelsToAdd.add(label);
            labelsToRemove.remove(label);
            card.setBackgroundColor(getResources().getColor(mediumGreen, null));
        } else if (labelList.contains(label) || labelsToAdd.contains(label)) {
            labelsToRemove.add(label);
            labelsToAdd.remove(label);
            card.setBackgroundColor(getResources().getColor(lightGreen, null));
        }
    }

    private void setLabelColor(View view) {
        CardView card = view.findViewById(R.id.label_card);
        for (EventLabelDSO label : allLabels) {
            if (labelList.contains(label) || labelsToAdd.contains(label)) {
                card.setBackgroundColor(getResources().getColor(mediumGreen, null));
            }
            if (!labelList.contains(label) || labelsToRemove.contains(label)) {
                card.setBackgroundColor(getResources().getColor(lightGreen, null));
            }
        }
    }

    private void moveBackToList() {
        Intent intent = new Intent(LabelListActivity.this, ViewOwnEventListActivity.class);
        intent.putExtra("email", userID);
        intent.putExtra("eventID", eventID);
        finish();
        LabelListActivity.this.startActivity(intent);
    }

    private void moveBackToHome() {
        Intent intent = new Intent(LabelListActivity.this, HomeActivity.class);
        finish();
        LabelListActivity.this.startActivity(intent);
    }

}
