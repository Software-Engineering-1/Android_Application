package com.justthejob.jobportal;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Button;
import android.widget.EditText;

public class RegisterActivity extends ActionBarActivity {

    EditText et_username, et_password, et_email;
    Button button_submit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = (EditText) findViewById(R.id.register_username_edittext);
        et_password = (EditText) findViewById(R.id.register_password_edittext);
        et_email = (EditText) findViewById(R.id.register_email_edittext);
        button_submit = (Button) findViewById(R.id.register_submit_button);
    }

}
