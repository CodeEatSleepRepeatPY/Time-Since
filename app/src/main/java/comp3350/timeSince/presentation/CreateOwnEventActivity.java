package comp3350.timeSince.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.widget.AdapterView;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.EventManager;
import comp3350.timeSince.business.exceptions.UserNotFoundException;
import comp3350.timeSince.objects.EventDSO;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.IEventLabelPersistence;

public class CreateOwnEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener
{
    private boolean favorite = false;
    private ArrayList<EventLabelDSO> eventLabels;
    private Bundle extras;
    private TextView eventName;
    private boolean labelNotClicked = true;
    private TextView description;
    private TextView dueDate;
    private TextView dueTime;
    private TextView isFavorite;
    private TextView eventLabelName;
    private Button favoriteBtn;
    private Spinner selectEventLabel;
    private Calendar mCalendar;
    private EventManager eventManager;
    private UserManager userManager;

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
        mCalendar = Calendar.getInstance();
        extras = getIntent().getExtras();
        eventManager = new EventManager(extras.getString("email"), true);

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
    public boolean onSupportNavigateUp(){
        finish();
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        EventLabelDSO eventLabelDSO;
        if(adapterView == findViewById(R.id.select_event_label)){
            eventLabelDSO = (EventLabelDSO) adapterView.getItemAtPosition(position);
            if( labelNotClicked ){
                eventLabels.clear();
                labelNotClicked = false;
            }else{
                eventLabels.add(eventLabelDSO );
                eventLabelName.setText(concatenateLabels());
            }
        }
    }

    private String concatenateLabels(){
        StringBuilder sb = new StringBuilder();

        for(EventLabelDSO eventLabel : eventLabels){
            sb.append(" "+eventLabel.getName() );
        }
        return(sb.toString());
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        eventLabelName.setText("");
    }

    private void loadEventLabelList(){
        SpinnerEventLabelList eventLabelsAdapter;

        userManager = new UserManager(true);
        List<EventLabelDSO> eventLabels = userManager.getUserLabels(extras.get("email").toString());
        if(eventLabels.size() == 0){
            Toast.makeText(this, "The EventLabel list for the user is empty.", Toast.LENGTH_SHORT).show();
        }

        eventLabelsAdapter = new SpinnerEventLabelList(this,
                R.layout.simple_spinner_dropdown_items, eventLabels);

        selectEventLabel.setAdapter(eventLabelsAdapter);
        eventLabelsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_items);
    }

    private void saveContents(){
        extras = getIntent().getExtras();
        EventDSO newEvent;
        String message = "Creation successful! ";
        Intent nextIntent = new Intent(this, ViewEventActivity.class);
        String eventLabelName;

        //if the event is successfully created, save information to the database
        try{
            if( eventLabels.size() == 0 ){
                eventLabelName = "";
            }else {
                eventLabelName = eventLabels.get(eventLabels.size() - 1).getName();
            }
            newEvent = eventManager.insertEvent(mCalendar,
                    eventName.getText().toString(), eventLabelName,
                        description.getText().toString(), favorite);

             if(newEvent != null){
                 Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                 CreateOwnEventActivity.this.startActivity(nextIntent);
             }else{
                 Toast.makeText(this, "The new event is not successfully created.", Toast.LENGTH_SHORT).show();
             }
        }catch(UserNotFoundException exception){
            Toast.makeText(this, exception.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar currentTime = Calendar.getInstance();

        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
        dueTime.setText( mSDF.format(mCalendar.getTime()) );

        if( mCalendar.before(currentTime) ){
            Toast.makeText(this, "The due date is before the current datetime!", Toast.LENGTH_LONG).show();
            dueDate.setText("");
            dueTime.setText("");
        }
    }

    private void showPickTimeDialogue(){
        Calendar mCalendar = Calendar.getInstance();
        int mHour = mCalendar.get(Calendar.HOUR_OF_DAY);
        int mMinute = mCalendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                this, mHour, mMinute, false);
        timePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
        mCalendar.set(Calendar.YEAR, year);
        mCalendar.set(Calendar.MONTH, month);
        mCalendar.set(Calendar.DAY_OF_MONTH, day);
        dueDate.setText(String.format("%d/%d/%d",day, (month+1), year));
    }

    private void showPickDateDialogue(){
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void buttonSetEventOnClick(View v){
        updateFavorite();
    }

    private void updateFavorite() {
        if(favoriteBtn != null) {
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
