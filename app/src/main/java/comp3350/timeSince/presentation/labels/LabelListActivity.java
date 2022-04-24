package comp3350.timeSince.presentation.labels;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import comp3350.timeSince.presentation.eventsList.ViewOwnEventListActivity;

public class LabelListActivity extends AppCompatActivity {
    private List<EventLabelDSO> labelList;
    private List<EventLabelDSO> allLabels;
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

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        // TODO go through the temporary list, and compare with the labelList then save the changes to the database (way faster to save once)
        return true;
    }

    private void setAdapter() {
        setOnClickListener();
        LabelListRecyclerAdapter adapter = new LabelListRecyclerAdapter(userEventManager,
                labelList, allLabels, event, listener);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setOnClickListener() {
        listener = new LabelListRecyclerAdapter.RecyclerViewClickOnListener() {
            @Override
            public void onClick(View view, int position) {
                // TODO figure out what to do here.  Change the colors of the item in the list when clicked, and save to the event
                // make a separate temporary list based off what was clicked
            }
        };
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
