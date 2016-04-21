package com.justthejob.jobportal;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class RegisterActivity extends ActionBarActivity {

    private String ENDPOINT_URL = "http://192.168.58.1:8000/android/register";
    private EditText et_username, et_password, et_email;
    private Button button_submit;
    private String ERROR_MESSAGE = "One or more fields are empty.";
    private InputStream inputStream = null;
    private String result = "";
    private String line = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        et_username = (EditText) findViewById(R.id.register_username_edittext);
        et_password = (EditText) findViewById(R.id.register_password_edittext);
        et_email = (EditText) findViewById(R.id.register_email_edittext);
        button_submit = (Button) findViewById(R.id.register_submit_button);

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_username.getText().toString().equals("") ||
                        et_password.getText().toString().equals("") ||
                        et_email.getText().toString().equals("")){
                    Toast.makeText(RegisterActivity.this, ERROR_MESSAGE, Toast.LENGTH_SHORT).show();
                }
                else{
                    List<NameValuePair> nameValuePairList = new ArrayList<NameValuePair>(1);
                    nameValuePairList.add(new BasicNameValuePair("user_name", et_username.getText().toString()));
                    nameValuePairList.add(new BasicNameValuePair("password", et_password.getText().toString()));
                    nameValuePairList.add(new BasicNameValuePair("email_id", et_email.getText().toString()));

                    try{
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(ENDPOINT_URL);
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairList));
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();
                        inputStream = entity.getContent();
                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    try{
                        BufferedReader reader = new BufferedReader(
                                new InputStreamReader(inputStream, "iso-8859-1"), 8);
                        StringBuilder sb = new StringBuilder();
                        while((line=reader.readLine())!=null) sb.append(line+"\n");
                        result = sb.toString();
                        inputStream.close();
                        System.out.println("Response returned by the server : " + result);
                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    try{
                        // The response from the server can be returned in the form of a JSON array
                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }

                    if(result.toLowerCase().contains("registered".toLowerCase())){
                        Intent profileIntent = new Intent(RegisterActivity.this, ProfileActivity.class);
                        startActivity(profileIntent);
                    }
                    else if(result.toLowerCase().contains("already".toLowerCase())){
                        // The username already exists, clear the fields
                        et_username.setText("");
                        et_password.setText("");
                        et_email.setText("");
                    }
                    else if(result.toLowerCase().contains("missing".toLowerCase())){
                        System.out.println("Some parameters are missing in the registration form");
                        Toast.makeText(RegisterActivity.this, "Missing fields", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        // there is some unknown error, unknown exception
                        System.out.println("There is some exception of the unknown type");
                    }
                }
            }
        });

    }

}
