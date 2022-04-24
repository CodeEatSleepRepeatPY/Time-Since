package comp3350.timeSince.presentation.labels;

import static comp3350.timeSince.R.color.lightGreen;
import static comp3350.timeSince.R.color.time_since_green;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.presentation.HomeActivity;
import comp3350.timeSince.presentation.events.SingleEventActivity;
import comp3350.timeSince.presentation.eventsList.ViewOwnEventListActivity;

public class LabelListActivity extends AppCompatActivity {
    private List<EventLabelDSO> eventLabels;
    private List<EventLabelDSO> userLabels;
    private List<EventLabelDSO> tempList;
    private RecyclerView recyclerView;
    private EventManager eventManager;
    private LabelListRecyclerAdapter.RecyclerViewClickOnListener listener;
    private String userID;
    private int eventID;
    private EventDSO event;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_label_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.labelRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        Intent intent = getIntent();
        userID = intent.getStringExtra("email");
        eventID = intent.getIntExtra("eventID", -1);

        setUpManagers();
        setAdapter();
    }

    private void setUpManagers() {
        eventManager = new EventManager(true);
        try {
            UserEventManager userEventManager = new UserEventManager(userID, true);
            event = eventManager.getEventByID(eventID);
            if (event != null) {
                setUpLists(userEventManager);
            }
        } catch (UserNotFoundException ue) {
            String message = "Something went wrong with your account, sorry! Please try logging " +
                    "in again.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            moveBackToHome();
        } catch (EventNotFoundException e) {
            String message = "Something went wrong with the event, sorry! Please try again.";
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            moveBackToEventList();
        }
    }

    private void setUpLists(UserEventManager userEventManager) {
        eventLabels = event.getEventLabels();
        userLabels = userEventManager.getUserLabels();
        tempList = new ArrayList<>();
        tempList.addAll(eventLabels);
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
            moveToCreateLabel();
        } else {
            ret_value = super.onOptionsItemSelected(item);
        }
        return ret_value;
    }

    private void setAdapter() {
        setOnClickListener();
        LabelListRecyclerAdapter adapter = new LabelListRecyclerAdapter(eventLabels,
                userLabels, listener);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new LabelListRecyclerAdapter.RecyclerViewClickOnListener() {
            @Override
            public void onClick(View view, int position) {
                setLabelOnClick(view);
            }
        };
    }

    private void setLabelOnClick(View view) {
        CardView card = view.findViewById(R.id.label_card);
        EventLabelDSO label = userLabels.get((int) card.getTag());
        if (tempList.contains(label)) {
            tempList.remove(label);
            card.setBackgroundColor(ContextCompat.getColor(this, lightGreen));
        } else if (!tempList.contains(label)) {
            tempList.add(label);
            card.setBackgroundColor(ContextCompat.getColor(this, time_since_green));
        }
    }

    private void saveState() {
        for (EventLabelDSO label : userLabels) {
            if (tempList.contains(label)) {
                eventManager.addLabelToEvent(event, label);
            }
            if (!tempList.contains(label)) {
                eventManager.removeLabelFromEvent(event, label);
            }
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        moveBackToSingleEvent();
        return true;
    }

    private void moveToCreateLabel() {
        saveState();
        Intent intent = new Intent(getApplicationContext(), CreateOwnLabelActivity.class);
        intent.putExtra("email", userID);
        intent.putExtra("eventID", eventID);
        finish(); // end this activity before starting the next
        startActivity(intent);
    }

    private void moveBackToSingleEvent() {
        saveState();
        Intent intent = new Intent(getApplicationContext(), SingleEventActivity.class);
        intent.putExtra("email", userID);
        intent.putExtra("eventID", eventID);
        finish(); // end this activity before starting the next
        startActivity(intent);
    }

    private void moveBackToEventList() {
        Intent intent = new Intent(getApplicationContext(), ViewOwnEventListActivity.class);
        intent.putExtra("email", userID);
        intent.putExtra("eventID", eventID);
        finish(); // end this activity before starting the next
        startActivity(intent);
    }

    private void moveBackToHome() {
        Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
        finish(); // end this activity before starting the next
        startActivity(intent);
    }

}
