package comp3350.timeSince.presentation.events;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.presentation.eventsList.ViewOwnEventListActivity;
import comp3350.timeSince.presentation.labels.LabelListActivity;

public class SingleEventActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener {
    private EventManager eventManager;
    private Button done_button;
    private Button tags_button;
    private Button favorite_button;
    private EditText name;
    private EditText description;
    private TextView dueDate;
    private EventDSO eventDSO;
    private int eventID;
    private String email;
    Calendar eventFinishTime;
    DatePickerDialog.OnDateSetListener dateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_single_event);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        Intent i = getIntent();

        eventManager = new EventManager(true);

        // initialize event information
        //TODO add try catch here
        eventID = i.getIntExtra("eventID", -1);
        eventDSO = eventManager.getEventByID(eventID);
        email = i.getStringExtra("email");

        setupButtons();
        setupEditTextFields();
        initializeListeners();
    }

    private void setupButtons(){
        // Button fields
        done_button = findViewById(R.id.event_done_button);
        tags_button = findViewById(R.id.event_tags_button);
        favorite_button = findViewById(R.id.event_favorite_button);

        // initialize the colors for the buttons
        setDoneColor();
        setFavoriteColor();
    }

    private void setupEditTextFields(){
        // EditText fields
        name = findViewById(R.id.event_name);
        description = findViewById(R.id.event_description);
        dueDate = findViewById(R.id.event_due_date);

        // initializing EditText fields
        name.setText(eventDSO.getName());
        description.setText(eventDSO.getDescription());
        eventFinishTime = eventDSO.getTargetFinishTime();

        setupDate();
    }

    private void setupDate(){
        int day;
        int month;
        int year;

        if (eventFinishTime != null){
            day = eventFinishTime.get(Calendar.DAY_OF_MONTH);
            month = eventFinishTime.get(Calendar.MONTH);
            year = eventFinishTime.get(Calendar.YEAR);

            dueDate.setText(getDateString(day, month, year));

            // set the color for the due date text to indicate if it's overdue
            setDateColor(eventFinishTime);
        }
    }

    private String getDateString(int day, int month, int year){
        return String.format("%d/%d/%d", day, (month + 1), year);
    }

    private void initializeListeners(){
        setupDoneListener();
        setupTagsListener();
        setupFavoriteListener();
        setupDueDateListener();
    }

    private void setupDoneListener(){
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEventDoneOnClick(view);
            }
        });
    }

    private void setupTagsListener(){
        tags_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEventTagsOnClick(view);
            }
        });
    }

    private void setupFavoriteListener() {
        favorite_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buttonEventFavoriteOnClick(view);
            }
        });
    }

    private void setupDueDateListener() {
        setupDueDatePopup();

        dueDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dueDateOnClick(view);
            }
        });
    }

    private void setupDueDatePopup(){
        dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(year, month, day);

                String displayDate = getDateString(day, month, year);
                dueDate.setText(displayDate);
                eventManager.updateEventFinishTime(calendar, eventID);
                setDateColor(calendar);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonEventDoneOnClick(View v) {
        boolean isDone = eventDSO.isDone();

        eventDSO.setIsDone(!isDone);
        setDoneColor(); // change the button color
    }

    private void setDoneColor(){
        boolean isDone = eventDSO.isDone();

        // toggle the colour
        if (isDone){
            done_button.setBackgroundColor(Color.GREEN);
        } else {
            done_button.setBackgroundColor(Color.WHITE);
        }
    }

    public void buttonEventTagsOnClick(View v) {
        Intent intent = new Intent(SingleEventActivity.this, LabelListActivity.class);
        intent.putExtra("eventID", eventID);
        intent.putExtra("email", email);
        SingleEventActivity.this.startActivity(intent);
    }

    public void buttonEventFavoriteOnClick(View v) {
        boolean isFavorite = eventDSO.isFavorite();

        eventDSO.setFavorite(!isFavorite);
        setFavoriteColor(); // change the button color
    }

    private void setFavoriteColor(){
        boolean isFavorite = eventDSO.isFavorite();

        // toggle the colour
        if (isFavorite){
            favorite_button.setBackgroundColor(Color.GREEN);
        } else {
            favorite_button.setBackgroundColor(Color.WHITE);
        }
    }

    public void dueDateOnClick(View v) {
        Calendar calendar = eventDSO.getTargetFinishTime();

        if(calendar == null){
            calendar = Calendar.getInstance();
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        if (eventFinishTime == null){
            eventFinishTime = Calendar.getInstance();
        }

        eventFinishTime.set(Calendar.YEAR, year);
        eventFinishTime.set(Calendar.MONTH, month);
        eventFinishTime.set(Calendar.DAY_OF_MONTH, day);
        dueDate.setText(getDateString(day, month, year));
        setDateColor(eventFinishTime);
    }

    // sets the date text color based on if the event due date has passed
    public void setDateColor(Calendar calendar){
        if (calendar.before(Calendar.getInstance())) { // event is due
            dueDate.setTextColor(Color.RED);
        } else {
            dueDate.setTextColor(Color.BLACK);
        }
    }

    // saves the name, description, time, done, and favorite info to the database
    private void saveState(){
        if(name != null){
            eventManager.updateEventName(name.getText().toString(), eventID);
        }
        if(description != null){
            eventManager.updateEventDescription(description.getText().toString(), eventID);
        }

        eventManager.updateEventFinishTime(eventFinishTime, eventID);
        eventManager.markEventAsDone(eventID, eventDSO.isDone());
        eventManager.updateEventFavorite(eventDSO.isFavorite(), eventID);
    }

    // when leaving, save the state and restart the ViewEventListActivity
    @Override
    public boolean onSupportNavigateUp(){
        saveState();
        Intent intent = new Intent(getApplicationContext(), ViewOwnEventListActivity.class);
        intent.putExtra("email", email);
        finish();  // end this activity before starting the next
        startActivity(intent);
        return true;
    }
}
