package comp3350.timeSince.presentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import comp3350.timeSince.R;
import comp3350.timeSince.application.Services;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.UserLoginFailedException;
import comp3350.timeSince.objects.UserDSO;
import comp3350.timeSince.persistence.fakes.UserPersistence;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private String email;
    private String password;
    private UserManager userManager = new UserManager();
    private UserPersistence userPersistence;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_view);
        login = (Button)findViewById(R.id.login);

        //TODO: the codes is for testing the login button, this will be replaced by User Manager
        userPersistence = (UserPersistence) Services.getUserPersistence();
        userPersistence.insertUser(new UserDSO("uid1", "hash1"));
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = ( (EditText)findViewById(R.id.username) ).getText().toString();
                password = ( (EditText)findViewById(R.id.password) ).getText().toString();
                userLogin();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void userLogin() {
        Intent nextIntent;
        try{
            //Some pre-set user list in the fake database for reference:
            //uid1 hash1
            if(userManager.accountCheck( email, password )){
                String message = "Welcome! "+email;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                nextIntent = new Intent(this, CreateOwnEventActivity.class);
                nextIntent.putExtra("email", email);
                nextIntent.putExtra("password", password);
                ( (EditText)findViewById(R.id.username) ).setText("");
                ( (EditText)findViewById(R.id.password) ).setText("");
                LoginActivity.this.startActivity(nextIntent);
            }else{
                nextIntent = new Intent(this, LoginActivity.class);
                LoginActivity.this.startActivity(nextIntent);
                throw new UserLoginFailedException("");
            }
        }catch (UserLoginFailedException error){
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
