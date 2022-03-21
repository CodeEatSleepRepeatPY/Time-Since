package comp3350.timeSince.presentation;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import comp3350.timeSince.R;

public class ViewEventActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_overview);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
