package comp3350.timeSince.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.UserEventManager;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;

public class CreateOwnEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener {
    private boolean favorite = false;
    private ArrayList<EventLabelDSO> eventLabels;
    private List<EventLabelDSO> candidateEventLabels;
    private Bundle extras;
    private TextView eventName;
    private boolean labelNotClicked;
    private TextView description;
    private TextView dueDate;
    private TextView dueTime;
    private TextView isFavorite;
    private TextView eventLabelName;
    private Button favoriteBtn;
    private Spinner selectEventLabel;
    private Calendar mCalendar;
    private EventManager eventManager;
    private UserEventManager userEventManager;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_own_event_view);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        eventName = findViewById(R.id.event_name);
        description = findViewById(R.id.event_description);
        dueDate = findViewById(R.id.due_date);
        dueTime = findViewById(R.id.due_datetime);
        favoriteBtn = findViewById(R.id.favorite_btn);
        selectEventLabel = findViewById(R.id.select_event_label);
        isFavorite = findViewById(R.id.favorite);
        eventLabelName = findViewById(R.id.event_label);
        eventLabels = new ArrayList<EventLabelDSO>();
        extras = getIntent().getExtras();
        userID = extras.get("email").toString();
        eventManager = new EventManager(true);
        labelNotClicked = true;

        try {
            userEventManager = new UserEventManager(userID, true);
        } catch (UserNotFoundException e) {
            Toast.makeText(this, "The user is not found.", Toast.LENGTH_SHORT).show();
            Intent homeIntent = new Intent(this, HomeActivity.class);
            startActivity(homeIntent);
        }

        favoriteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateFavorite();
            }
        });

        findViewById(R.id.select_date).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickDateDialogue();
            }
        });

        findViewById(R.id.select_datetime).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickTimeDialogue();
            }
        });

        selectEventLabel.setOnItemSelectedListener(this);

        findViewById(R.id.clear_event_labels).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                eventLabels.clear();
                eventLabelName.setText(concatenateLabels());
            }
        });

        findViewById(R.id.save_event).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveContents();
            }
        });

        //load the eventLabel database from the database
        loadEventLabelList();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        EventLabelDSO eventLabelDSO;

        if (adapterView == findViewById(R.id.select_event_label)) {
            eventLabelDSO = (EventLabelDSO) adapterView.getItemAtPosition(position);
            if (labelNotClicked) {
                eventLabels.clear();
                labelNotClicked = false;
            } else {
                eventLabels.add(eventLabelDSO);
            }
            eventLabelName.setText(concatenateLabels());
        }
    }

    private String concatenateLabels() {
        StringBuilder sb = new StringBuilder();

        for (EventLabelDSO eventLabel : eventLabels) {
            sb.append(" " + eventLabel.getName());
        }
        return (sb.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        eventLabelName.setText("");
    }

    private void loadEventLabelList() {
        SpinnerEventLabelList eventLabelsAdapter;
        candidateEventLabels = userEventManager.getUserLabels();

        if (candidateEventLabels.size() == 0) {
            candidateEventLabels.add(new EventLabelDSO(1, "You have no labels yet"));
        }
        eventLabelsAdapter = new SpinnerEventLabelList(this,
                R.layout.simple_spinner_dropdown_items, candidateEventLabels);

        selectEventLabel.setAdapter(eventLabelsAdapter);
        eventLabelsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_items);
    }

    private void saveContents() {
        extras = getIntent().getExtras();
        EventDSO newEvent;
        String message = "Creation successful! ";
        Intent nextIntent = new Intent(this, ViewOwnEventListActivity.class);
        nextIntent.putExtra("email", userID);

        //if the event is successfully created, save information to the database
        try {
            newEvent = eventManager.createEvent(eventName.getText().toString(),
                    description.getText().toString(), mCalendar, favorite);
            userEventManager.addUserEvent(newEvent);
            eventManager.addLabelsToEvent(newEvent, eventLabels);

            if (newEvent != null) {
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                CreateOwnEventActivity.this.startActivity(nextIntent);
            } else {
                Toast.makeText(this, "The new event is not successfully created.", Toast.LENGTH_SHORT).show();
            }
        } catch (UserNotFoundException exception) {
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar currentTime = Calendar.getInstance();
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
        dueTime.setText(mSDF.format(mCalendar.getTime()));

        if (mCalendar.before(currentTime)) {
            Toast.makeText(this, "The due date is before the current datetime!", Toast.LENGTH_LONG).show();
            dueDate.setText("");
            dueTime.setText("");
        }
    }

    private void showPickTimeDialogue() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                false);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (mCalendar == null) {
            mCalendar = Calendar.getInstance();
        }
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        dueDate.setText(String.format("%d/%d/%d", day, (month + 1), year));
    }

    private void showPickDateDialogue() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void buttonSetEventOnClick(View v) {
        updateFavorite();
    }

    private void updateFavorite() {
        if (favoriteBtn != null) {
            favorite = !favorite;
            if (favorite) {
                favoriteBtn.setBackgroundResource(R.drawable.heart_filled);
                isFavorite.setText("Favorite: yes");
            } else {
                favoriteBtn.setBackgroundResource(R.drawable.heart_empty);
                isFavorite.setText("Favorite: no");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
