package comp3350.timeSince.presentation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.security.NoSuchAlgorithmException;

import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.DuplicateUserException;
import comp3350.timeSince.business.exceptions.PasswordErrorException;

public class RegisterActivity extends AppCompatActivity {
    private final UserManager userManager = new UserManager(true);

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
        Intent userIntent;

        //Save the value that users entered: username and password
        EditText editUsername = findViewById(R.id.username);
        EditText editPassword = findViewById(R.id.password);
        EditText editConfirmPassword = findViewById(R.id.confirm_password);

        String strUsername = editUsername.getText().toString();
        String strPassword = editPassword.getText().toString();
        String strConfirmPassword = editConfirmPassword.getText().toString();

        try {
            if (userManager.insertUser(strUsername, strPassword, strConfirmPassword, null)) {
                Toast.makeText(this, "Registration success!", Toast.LENGTH_LONG).show();
                userIntent = new Intent(RegisterActivity.this, ViewEventActivity.class);
            } else {
                userIntent = new Intent(this, RegisterActivity.class);
            }
            startActivity(userIntent);
            finish();
        }catch (PasswordErrorException | DuplicateUserException | NoSuchAlgorithmException e){
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
            editUsername.setText("");
            editPassword.setText("");
            editConfirmPassword.setText("");
        }
    }
}
