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
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.business.EventManager;

public class SingleEventActivity extends AppCompatActivity {
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
        Calendar eventFinishTime;
        String dateText;
        Intent i = getIntent();
        eventDSO = (EventDSO) i.getExtras("THIS_EVENT");

        eventID = eventDSO.getID();

        // Button fields
        done_button = (Button) findViewById(R.id.event_done_button);
        tags_button = (Button) findViewById(R.id.event_tags_button);
        favorite_button = (Button) findViewById(R.id.event_favorite_button);

        // EditText fields
        name = (EditText) findViewById(R.id.event_name);
        description = (EditText) findViewById(R.id.event_description);
        dueDate = (TextView) findViewById(R.id.event_due_date);

        // initializing EditText fields
        name.setText(eventDSO.getName());
        description.setText(eventDSO.getDescription());
        eventFinishTime = eventDSO.getTargetFinishTime();
        dateText = String.format("%d-%d-%d",
                eventFinishTime.get(Calendar.YEAR),
                eventFinishTime.get(Calendar.MONTH),
                eventFinishTime.get(Calendar.DAY_OF_MONTH));
        dueDate.setText(dateText);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonEventDoneOnClick(View v) {
        boolean isDone = EventManager.isDone(eventID);

        try{
            done_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    EventManager.markEventAsDone(eventID, !isDone);

                    // toggle the colour
                    if (isDone){
                        done_button.setBackgroundColor(Color.BLUE);
                    } else {
                        done_button.setBackgroundColor(Color.WHITE);
                    }
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    public void buttonEventTagsOnClick(View v) {
        try{
            tags_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
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
                    EventManager.updateEventFavorite(!isFavorite, eventID);

                    // toggle the colour
                    if (isFavorite){
                        favorite_button.setBackgroundColor(Color.BLUE);
                    } else {
                        favorite_button.setBackgroundColor(Color.WHITE);
                    }
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    public void dueDateOnClick(View v) {
        DatePickerDialog picker;
        DatePickerDialog.OnDateSetListener listener;

        try{
            listener = new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    month = month + 1;
                    String displayDate = String.format("%d-%d-%d", year, month, day);
                    dueDate.setText(displayDate);
                    EventManager.updateEventFinishTime(datePicker, eventID);
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
                            listener,
                            year,
                            month,
                            day
                    );

                    picker.show();
                }
            });
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    // consider adding an exit / back button that you can click.  This will
    // take you back to the list of all events, and it'll set the text in the
    // eventDSO to whatever the text was changed to in the layout
    // (when a user changes the description, you want to keep it...)
    @Override
    public boolean onSupportNavigateUp(){
        EventManager.updateEventName(name.getText(), eventID);
        EventManager.updateEventDescription(description.getText(), eventID);

        finish();
        return true;
    }
}
