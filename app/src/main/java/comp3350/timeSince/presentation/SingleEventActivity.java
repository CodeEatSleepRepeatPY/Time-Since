package comp3350.timeSince.presentation;

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
import comp3350.timeSince.business.interfaces.IEventManager;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.business.EventManager;

public class SingleEventActivity extends AppCompatActivity {
    private IEventManager IEventManager;
    private Button done_button;
    private Button tags_button;
    private Button favorite_button;
    private EditText name;
    private EditText description;
    private TextView dueDate;
    private EventDSO eventDSO;
    private int eventID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_single_event);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        IEventManager = new EventManager(true);
        Calendar eventFinishTime;
        String dateText;
        Intent i = getIntent();

        // initialize event information
        eventID = i.getIntExtra("eventID", -1);
        eventDSO = IEventManager.getEventByID(eventID);

        // Button fields
        done_button = findViewById(R.id.event_done_button);
        tags_button = findViewById(R.id.event_tags_button);
        favorite_button = findViewById(R.id.event_favorite_button);

        // initialize the colors for the buttons
        setDoneColor();
        setFavoriteColor();

        // EditText fields
        name = findViewById(R.id.event_name);
        description = findViewById(R.id.event_description);
        dueDate = findViewById(R.id.event_due_date);

        // initializing EditText fields
        name.setText(eventDSO.getName());
        description.setText(eventDSO.getDescription());
        eventFinishTime = eventDSO.getTargetFinishTime();
        dateText = String.format("%d-%d-%d",
                eventFinishTime.get(Calendar.YEAR),
                eventFinishTime.get(Calendar.MONTH),
                eventFinishTime.get(Calendar.DAY_OF_MONTH));
        dueDate.setText(dateText);

        // set the color for the due date text to indicate if it's overdue
        setDateColor(eventFinishTime);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonEventDoneOnClick(View v) {
        boolean isDone = IEventManager.isDone(eventID);

        try{
            done_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //eventManager.markEventAsDone(eventID, !isDone);
                    setDoneColor(); // change the button color
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private void setDoneColor(){
        boolean isDone = IEventManager.isDone(eventID);

        // toggle the colour
        if (isDone){
            done_button.setBackgroundColor(Color.BLUE);
        } else {
            done_button.setBackgroundColor(Color.WHITE);
        }
    }

    public void buttonEventTagsOnClick(View v) {
        try{
            tags_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // For next iteration:
                    // The event tag UI isn't created yet.  This will be part
                    // of another iteration.
                    // Here's what clicking this button should do:
                    // It should take you to another page that lists
                    // all of the tags, and allows you to edit the tags
                    // associated with this event, as well as edit the
                    // available tag presets
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    public void buttonEventFavoriteOnClick(View v) {
        boolean isFavorite = eventDSO.isFavorite();

        try{
            favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //eventManager.updateEventFavorite(!isFavorite, eventID);
                    setFavoriteColor(); // change the button color
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private void setFavoriteColor(){
        boolean isFavorite = eventDSO.isFavorite();

        // toggle the colour
        if (isFavorite){
            favorite_button.setBackgroundColor(Color.BLUE);
        } else {
            favorite_button.setBackgroundColor(Color.WHITE);
        }
    }

    public void dueDateOnClick(View v) {
        DatePickerDialog.OnDateSetListener listener;

        try{
            listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    Calendar calendar = Calendar.getInstance();
                    calendar.set(year, month, day);

                    String displayDate = String.format("%d-%d-%d", year, month + 1, day);
                    dueDate.setText(displayDate);
                    IEventManager.updateEventFinishTime(calendar, eventID);
                    setDateColor(calendar);
                }
            };

            dueDate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Calendar calendar = eventDSO.getTargetFinishTime();
                    int year = calendar.get(Calendar.YEAR);
                    int month = calendar.get(Calendar.MONTH);
                    int day = calendar.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog picker = new DatePickerDialog(
                            SingleEventActivity.this,
                            android.R.style.Widget_Holo_ActionBar_Solid,
                            listener, year, month, day
                    );

                    picker.show();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    // sets the date text color based on if the event due date has passed
    void setDateColor(Calendar calendar){
        if (calendar.before(Calendar.getInstance())) { // event is due
            dueDate.setTextColor(Color.RED);
        } else {
            dueDate.setTextColor(Color.BLACK);
        }
    }

    // upon leaving, saves the name and description entered in the UI
    @Override
    public boolean onSupportNavigateUp(){
        IEventManager.updateEventName(name.getText().toString(), eventID);
        IEventManager.updateEventDescription(description.getText().toString(), eventID);

        finish();
        return true;
    }
}
