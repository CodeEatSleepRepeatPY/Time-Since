package comp3350.timeSince.presentation;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
//import comp3350.timeSince.business.exceptions.UserLoginFailedException;

public class CreateOwnEventActivity extends AppCompatActivity implements
        View.OnClickListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener

{
    private Button favoriteBtn;
    //private
    private TextView eventName;
    private TextView dueDate;
    private TextView dueTime;
    private Bundle extras;
    private boolean favourite = false;
    private boolean update = false;
    private Button selectDatetime;
    private Button selectTime;
    private Button selectEventTag;




    //private UserManager userManager = new UserManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_own_event_view);

        eventName = findViewById(R.id.event_name);
        dueDate = findViewById(R.id.due_date);
        dueTime = findViewById(R.id.due_datetime);
        favoriteBtn = findViewById(R.id.favorite_btn);
        selectDatetime = findViewById(R.id.select_date);
        selectTime = findViewById(R.id.select_datetime);
        selectEventTag = findViewById(R.id.select_event_tag);

        favoriteBtn.setOnClickListener(this);

        selectDatetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickDateDialogue();
            }
        });

        selectTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showPickTimeDialogue();
            }
        });

        selectEventTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickEventLabelList();
            }
        });

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

    }

    public void buttonSetEventOnClick(View v){
        updateFavourite();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        updateFavourite();

    }

    private void updateFavourite() {
        if(favoriteBtn != null) {
            favourite = !favourite;
            update = true;
            setFavouriteImage();
        }
    }
    private void setFavouriteImage() {
        if(favoriteBtn != null) {
            if (favourite) {
                favoriteBtn.setBackgroundResource(R.drawable.heart_filled);
            } else {
                favoriteBtn.setBackgroundResource(R.drawable.heart_empty);
            }
        }
    }



}
