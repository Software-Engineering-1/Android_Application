package com.justthejob.jobportal;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends ActionBarActivity {

    private String ENDPOINT_URL = "http://192.168.58.1:8000/android/login";
    private String LOGIN_ERROR_NULL_FIELDS = "Either username or password is empty";
    private String LOGIN_FAILED = "One or more of username or password is incorrect";
    private EditText et_username, et_password;
    private Button button_submit;
    private int choice = 999;
    private InputStream inputStream = null;
    private String line = "";
    private String result = "";

    // Response returned from the server on login
    private String LOGIN_SUCCESS_COMPLETE = "MAIN";
    private String LOGIN_SUCCESS_INCOMPLETE = "CHAT";
    private String LOGIN_UNSUCCESSFUL = "Invalid Credentials";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().build();
        StrictMode.setThreadPolicy(policy);

        et_username = (EditText) findViewById(R.id.username_edittext);
        et_password = (EditText) findViewById(R.id.password_edittext);
        button_submit = (Button) findViewById(R.id.loginscreen_button);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_username.getText().toString().equals("") ||
                        et_password.getText().toString().equals(""))
                    Toast.makeText(LoginActivity.this, LOGIN_ERROR_NULL_FIELDS,
                            Toast.LENGTH_SHORT).show();
                else{
                    /**
                     * 1. Starts the HttpWebRequest to the Web Server
                     * 2. Response is received from the server
                     * 3. Depending on the response one of the following actions is taken
                     */
                    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(1);
                    nameValuePairList.add(new BasicNameValuePair("user_name",
                            et_username.getText().toString()));
                    nameValuePairList.add(new BasicNameValuePair("password",
                            et_password.getText().toString()));

                    try{
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(ENDPOINT_URL);
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();
                        inputStream = entity.getContent();

                    }catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    try{
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        while ((line=reader.readLine())!=null)
                            sb.append(line+"\n");
                        result = sb.toString();
                        Toast.makeText(LoginActivity.this, "Response received from the server : " + result,
                                Toast.LENGTH_SHORT).show();
                        inputStream.close();

                        // Set the choice based on the response returned from the server
                        if (result.equalsIgnoreCase(LOGIN_SUCCESS_COMPLETE)) choice = 0;
                        else if (result.equalsIgnoreCase(LOGIN_SUCCESS_INCOMPLETE)) choice = 1;
                        else if (result.equalsIgnoreCase(LOGIN_UNSUCCESSFUL)) choice = 2;
                        else choice = 999; // Represents an invalid choice

                    }catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    try{

                    }catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }

                    if (result.toLowerCase().contains("MAIN".toLowerCase())){
                        Intent mainIntent = new Intent(LoginActivity.this, MainPage.class);
                        startActivity(mainIntent);
                    }
                    else if (result.toLowerCase().contains("CHAT".toLowerCase())){
                        Intent profileIntent = new Intent(LoginActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                    }
                    else if (result.toLowerCase().contains("Invalid Credentials".toLowerCase())){
                        et_username.setText("");
                        et_password.setText("");
                        Toast.makeText(LoginActivity.this, LOGIN_FAILED, Toast.LENGTH_SHORT).show();
                    }
                    else{
                        System.out.println(result);
                        Toast.makeText(LoginActivity.this, "INVALID RESPONSE FROM SERVER",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

}
