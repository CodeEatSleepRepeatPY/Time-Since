package comp3350.timeSince.presentation;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import comp3350.timeSince.R;
import comp3350.timeSince.business.UserManager;
import comp3350.timeSince.business.exceptions.UserLoginFailedException;

public class LoginActivity extends AppCompatActivity {
    private Button login;
    private String email;
    private String password;
    private UserManager userManager = new UserManager();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_login_view);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void buttonLoginOnClick(View v) {
        try{
            login = (Button)findViewById(R.id.login);
            email = ( (EditText)findViewById(R.id.username) ).getText().toString();
            password = ( (EditText)findViewById(R.id.password) ).getText().toString();

            login.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if( email != null && password != null  ){
                        userLogin();
                    }
                }
            });
        }catch(Exception exception){
            exception.printStackTrace();
        }
    }

    private void userLogin() {
        Intent nextIntent;
        try{
            //TODO: if the user account is in the database
            //if(userManager.accountCheck( email.getText().toString(), password.getText().toString() )){
              if(email.equals("admin") && password.equals("admin")){
                String message = "Welcome! "+email;
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
                nextIntent = new Intent(this, ViewEventActivity.class);
                nextIntent.putExtra("email", email);
                nextIntent.putExtra("password", password);
                  ( (EditText)findViewById(R.id.username) ).setText("");
                  ( (EditText)findViewById(R.id.password) ).setText("");
                startActivity(nextIntent);
                finish();
            }else{
                nextIntent = new Intent(this, LoginActivity.class);
                startActivity(nextIntent);
                finish();
                throw new UserLoginFailedException("");
            }
        }catch (UserLoginFailedException error){
                Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

}
