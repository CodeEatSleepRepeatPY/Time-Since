package comp3350.timeSince.presentation;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventDSO;

public class SingleEventActivity extends AppCompatActivity {
    private Button done_button;
    private Button tags_button;
    private Button favorite_button;
    private EditText name;
    private EditText description;
    private EditText dueDate;
    private EventDSO eventDSO;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_single_event);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        done_button = (Button) findViewById(R.id.event_done_button);
        tags_button = (Button) findViewById(R.id.event_tags_button);
        favorite_button = (Button) findViewById(R.id.event_favorite_button);

        name = (EditText) findViewById(R.id.event_name);
        description = (EditText) findViewById(R.id.event_description);
        dueDate = (EditText) findViewById(R.id.event_due_date);

        name.setText(eventDSO.getName());
        description.setText(eventDSO.getDescription());
        calendar = new GregorianCalendar();
        calendar.setTime(eventDSO.getTargetFinishTime());
        dueDate.setText(calendar.toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonEventDoneOnClick(View v) {
        try{
            done_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Figure out what to do when an event is marked as done
                    // probably have to call a logic function in the
                    // EventManager to deal with removing this event from the
                    // user's current event list




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
                    // Figure out what to do when the tags button is clicked
                    // It should probably take you to another page that lists
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
        try{
            favorite_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (eventDSO.isFavorite()){
                        eventDSO.unsetFavorite();
                    } else {
                        eventDSO.setFavorite();
                    }
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    // consider adding an exit / back button that you can click.  This will
    // take you back to the list of all events, and it'll set the text in the
    // eventDSO to whatever the text was changed to in the layout
    // (when a user changes the description, you want to keep it...)
    @Override
    public boolean onSupportNavigateUp(){
        // save the changed name
        eventDSO.setName(name.getText().toString());
        // save the changed description
        eventDSO.setDescription(description.getText().toString());
        // save the changed due date
        // get time returns long time


        // we're going to convert Date into Calendar...  That'll make this easier to set.  Just wait for it a bit...
        eventDSO.setTargetFinishTime(dueDate.getText().toString());

        finish();
        return true;
    }
}
