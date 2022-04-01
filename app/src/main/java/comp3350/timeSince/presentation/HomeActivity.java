package comp3350.timeSince.presentation;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import comp3350.timeSince.R;
import comp3350.timeSince.persistence.utils.DBHelper;
import comp3350.timeSince.presentation.RegisterActivity;
import comp3350.timeSince.presentation.LoginActivity;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        DBHelper.copyDatabaseToDevice(getApplicationContext(), "db");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public void buttonSignUpOnClick(View v) {
        Intent userIntent = new Intent(HomeActivity.this, RegisterActivity.class);
        HomeActivity.this.startActivity(userIntent);
    }

    public void buttonLoginOnClick(View v) {
        Intent userIntent = new Intent(HomeActivity.this, LoginActivity.class);
        HomeActivity.this.startActivity(userIntent);
    }

}
