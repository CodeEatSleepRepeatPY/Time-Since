package comp3350.timeSince.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import comp3350.timeSince.R;

public class RegisterActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_registration_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonRegisterOnClick(View v) {
        Intent userIntent = new Intent(RegisterActivity.this, ViewEventActivity.class);
        RegisterActivity.this.startActivity(userIntent);
    }
}
