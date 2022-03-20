package comp3350.timeSince.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

import comp3350.timeSince.application.Services;

import comp3350.timeSince.R;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.fakes.EventLabelPersistence;

public class CreateOwnEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener
{
    private boolean favorite = false;
    private boolean update = false;
    private ArrayList<EventLabelDSO>  eventLabels;
    private Bundle extras;
    private TextView eventName;
    private TextView dueDate;
    private TextView dueTime;
    private TextView isFavorite;
    private TextView eventLabelName;
    private Button favoriteBtn;
    private Spinner selectEventLabel;
    private EventLabelPersistence evenLabelPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_own_event_view);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        eventName = findViewById(R.id.event_name);
        dueDate = findViewById(R.id.due_date);
        dueTime = findViewById(R.id.due_datetime);
        favoriteBtn = findViewById(R.id.favorite_btn);
        selectEventLabel = findViewById(R.id.select_event_label);
        isFavorite = findViewById(R.id.favorite);
        eventLabelName = findViewById(R.id.event_label);
        eventLabels = new ArrayList<EventLabelDSO>();

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
            eventLabels.add(eventLabelDSO );
            eventLabelName.setText( eventLabelDSO.getName() );
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        eventLabelName.setText("");
    }

    /* menu on the top right, might be used later
    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_event_labels, menu);
        return true;
    }
    */

    private void loadEventLabelList(){
        SpinnerEventLabelList eventLabelsAdapter;

        //TODO this will be replaced by Logic layer function; now it is only for test
        evenLabelPersistence = (EventLabelPersistence) Services.getEventLabelPersistence();
        //dummy data for test
        evenLabelPersistence.insertEventLabel(new EventLabelDSO("label1"));
        evenLabelPersistence.insertEventLabel(new EventLabelDSO("label2"));
        evenLabelPersistence.insertEventLabel(new EventLabelDSO("label3"));

        List<EventLabelDSO> eventLabels = evenLabelPersistence.getEventLabelList();
        eventLabelsAdapter = new SpinnerEventLabelList(this,
                R.layout.simple_spinner_dropdown_items, (ArrayList<EventLabelDSO>) eventLabels);

        selectEventLabel.setAdapter(eventLabelsAdapter);
        eventLabelsAdapter.setDropDownViewResource(R.layout.simple_spinner_dropdown_items);
    }

    private void saveContents(){
        //TODO save the user input, let the logic handles the data and update the DB
        extras = getIntent().getExtras();

    }

    @Override
    public void onTimeSet(TimePicker timePicker, int hour, int minute) {
        Calendar mCalendar = Calendar.getInstance();
        mCalendar.set(Calendar.HOUR_OF_DAY, hour);
        mCalendar.set(Calendar.MINUTE, minute);
        SimpleDateFormat mSDF = new SimpleDateFormat("hh:mm a");
        dueTime.setText( mSDF.format(mCalendar.getTime()) );
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
        dueDate.setText(String.format("%d/%d/%d",day, month, year));
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
            update = true;
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
