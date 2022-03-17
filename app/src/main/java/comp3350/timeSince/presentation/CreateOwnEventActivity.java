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
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import comp3350.timeSince.application.Services;

import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.objects.EventLabelDSO;
import comp3350.timeSince.persistence.I_Database;

public class CreateOwnEventActivity extends AppCompatActivity implements
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener,
        AdapterView.OnItemSelectedListener

{
    private boolean favourite = false;
    private boolean update = false;
    private Bundle extras;
    String eventLabelName;

    private TextView eventName;
    private TextView dueDate;
    private TextView dueTime;

    private Button favoriteBtn;
    //private Button selectDate;
    //private Button selectTime;
    private Spinner selectEventLabel;


    private I_Database dbPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_own_event_view);

        eventName = findViewById(R.id.event_name);
        dueDate = findViewById(R.id.due_date);
        dueTime = findViewById(R.id.due_datetime);
        favoriteBtn = findViewById(R.id.favorite_btn);
        selectEventLabel = findViewById(R.id.select_event_tag);

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
        //selectEventLabel.setOnClickListener(new View.OnClickListener() {
        //    @Override
        //    public void onClick(View view) {
        //    }
        //});

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
    public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
        if(adapterView == findViewById(R.id.select_event_tag)){
            EventLabelDSO eventLabel = (EventLabelDSO) adapterView.getItemAtPosition(position);
            eventLabelName = eventLabel.getName();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        eventLabelName = "";
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.memu_event_labels, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        //if(id == R.id.action_settings)
            //return true;
        return super.onOptionsItemSelected(item);
    }

    private void loadEventLabelList(){
        SpinnerEventLabelList eventLabelsAdapter;
        //ArrayAdapter<String> spinnerAdapter;
        dbPersistence = Services.getDatabase();
        List<EventLabelDSO> eventLabels = dbPersistence.getAllEventLabels();
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

    private void pickEventLabelList(){
        //TODO implement spinner of event list dropdown menu

    }

    public void buttonSetEventOnClick(View v){
        updateFavorite();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    private void updateFavorite() {
        if(favoriteBtn != null) {
            favourite = !favourite;
            update = true;
            setFavoriteImage();
        }
    }

    private void setFavoriteImage() {
        if(favoriteBtn != null) {
            if (favourite) {
                favoriteBtn.setBackgroundResource(R.drawable.heart_filled);
            } else {
                favoriteBtn.setBackgroundResource(R.drawable.heart_empty);
            }
        }
    }

}
