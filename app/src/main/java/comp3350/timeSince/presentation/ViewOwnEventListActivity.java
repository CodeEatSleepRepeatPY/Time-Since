package comp3350.timeSince.presentation;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.EventNotFoundException;
import comp3350.timeSince.objects.EventDSO;

public class ViewOwnEventListActivity extends AppCompatActivity {
    private List<EventDSO> eventList;
    private RecyclerView recyclerView;
    private EventManager eventManager;
    private UserEventManager userEventManager;
    private Bundle extras;
    private EventListRecyclerAdapter.RecyclerViewClickOnListener listener;
    private EventListDeleteItemAdapter.RecyclerViewClickOnListener listenerDelete;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_own_event_list);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.eventRecyclerView);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        extras = getIntent().getExtras();
        userID = extras.get("email").toString();

        eventManager = new EventManager(true);
        userEventManager = new UserEventManager(userID, true);
        eventList = userEventManager.getUserEvents();

        //sort and highlight any event if they are closed (7 days)
        checkComingEvents();
        setAdapter();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.events_list_actions, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.add_new_event:
                Intent intent = new Intent(getApplicationContext(), CreateOwnEventActivity.class);
                intent.putExtra("email", userID);
                startActivity(intent);
                return true;
            case R.id.sort_by_title:
                eventList = userEventManager.sortByName(true);
                setAdapter();
                return true;
            case R.id.sort_by_due_time:
                eventList = userEventManager.sortByFinishTime(true);
                setAdapter();
                return true;
            case R.id.delete_event:
                setDeleteAdapter();
                return true;
            case R.id.coming_events_alert:
                eventList = userEventManager.sortByFinishTime(true);
                setAdapter();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void checkComingEvents() {
        List<EventDSO> comingEventsList = userEventManager.checkClosingEvents();
        eventList = userEventManager.sortByFinishTime(true);

        Drawable unwrappedDrawable = AppCompatResources.getDrawable(this, R.drawable.ic_alert_white);
        Drawable wrappedDrawable = DrawableCompat.wrap(unwrappedDrawable);

        //change the color of the events background
        if (comingEventsList.size() > 0) {
            DrawableCompat.setTint(wrappedDrawable, Color.RED);
        } else {
            DrawableCompat.setTint(wrappedDrawable, Color.WHITE);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void setAdapter() {
        setOnClickListener();
        EventListRecyclerAdapter adapter = new EventListRecyclerAdapter(eventList, listener);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setDeleteAdapter() {
        setDeleteOnClickListener();
        EventListDeleteItemAdapter adapter = new EventListDeleteItemAdapter(eventList, listenerDelete);

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }

    private void setDeleteOnClickListener() {
        listenerDelete = new EventListDeleteItemAdapter.RecyclerViewClickOnListener() {
            @Override
            public void onClick(View view, int position) {
                try {
                    confirmDelete(position);
                } catch (EventNotFoundException error) {
                    error.getMessage();
                }
            }
        };
    }

    private void confirmDelete(int position) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setMessage("Are you sure to delete this event?")
                .setTitle("Remove Event")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        eventManager.deleteEvent(eventList.get(position).getID());
                        dialogInterface.dismiss();

                        Intent intent2 = new Intent(getApplicationContext(), ViewOwnEventListActivity.class);
                        intent2.putExtra("email", userID);
                        startActivity(intent2);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        setAdapter();
                    }
                })
                .create();
        dialog.show();
    }

    private void setOnClickListener() {
        listener = new EventListRecyclerAdapter.RecyclerViewClickOnListener() {
            @Override
            public void onClick(View view, int position) {
                Intent intent = new Intent(getApplicationContext(), SingleEventActivity.class);
                intent.putExtra("email", userID);
                intent.putExtra("eventID", eventList.get(position).getID());
                startActivity(intent);
            }
        };
    }

}
