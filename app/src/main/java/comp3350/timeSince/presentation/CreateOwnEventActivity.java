package comp3350.timeSince.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
//import comp3350.timeSince.business.exceptions.UserLoginFailedException;

public class CreateOwnEventActivity extends AppCompatActivity implements View.OnClickListener {
    private Button favoriteBtn;
    //private
    private TextView eventName;
    private TextView dueDateTime;
    private Bundle extras;
    private boolean favourite = false;
    private boolean update = false;

    //private UserManager userManager = new UserManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_own_event_view);

        eventName = findViewById(R.id.event_name);
        dueDateTime = findViewById(R.id.due_date);
        favoriteBtn = findViewById(R.id.favorate_btn);

        favoriteBtn.setOnClickListener(this);

        extras = getIntent().getExtras();

    }

    public void buttonSetEventOnClick(){

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
                //favoriteBtn.setBackground(R.drawable.heart_filled);
            } else {
                //favoriteBtn.setImageResource(R.drawable.heart_empty);
            }
        }
    }


}
