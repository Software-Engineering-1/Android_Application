package com.justthejob.jobportal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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

public class ProfileActivity extends ActionBarActivity {

    private String ENDPOINT = "http://192.168.58.1:8000/android/chatbot_details";
    private EditText et_firstname, et_lastname, et_age, et_dob, et_address, et_phone;
    private Button button_submit;
    private RadioGroup radioGroupGender;
    private RadioButton radioMale, radioFemale;
    private RadioButton radioSex;
    private InputStream inputStream = null;
    private String result = "";
    private String line = "";

    private String SUCCESS_REGISTER_MESSAGE = "Successfully updated details";
    private String SUCCESS_ALREADY_EXISTS = "Already filled in the details";

    private String EMPTY_FIELDS_ERROR_MESSAGE = "One or more fields are empty in Profile";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        et_firstname = (EditText) findViewById(R.id.profile_firstname);
        et_lastname = (EditText) findViewById(R.id.profile_lastname);
        et_age = (EditText) findViewById(R.id.profile_age);
        et_dob = (EditText) findViewById(R.id.profile_dob);
        et_address = (EditText) findViewById(R.id.profile_address);
        et_phone = (EditText) findViewById(R.id.profile_phone);
        button_submit = (Button) findViewById(R.id.profile_submit);
        radioGroupGender = (RadioGroup) findViewById(R.id.profile_gender);
        /**radioMale = (RadioButton) findViewById(R.id.profile_male);
        radioFemale = (RadioButton) findViewById(R.id.profile_female);*/

        button_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (et_firstname.getText().toString().equals("") ||
                        et_lastname.getText().toString().equals("") ||
                        et_age.getText().toString().equals("") ||
                        et_dob.getText().toString().equals("") ||
                        et_phone.getText().toString().equals("") ||
                        et_address.getText().toString().equals(""))
                    Toast.makeText(ProfileActivity.this, EMPTY_FIELDS_ERROR_MESSAGE,
                            Toast.LENGTH_SHORT).show();
                else{
                    // Send the information to the server
                    List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                    int selectedGender = radioGroupGender.getCheckedRadioButtonId();
                    radioSex = (RadioButton) findViewById(selectedGender);
                    nameValuePairs.add(new BasicNameValuePair("user_name", LoginActivity.USER_NAME));
                    nameValuePairs.add(new BasicNameValuePair("password", LoginActivity.USER_PASS));
                    nameValuePairs.add(new BasicNameValuePair("first_name", et_firstname.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("last_name", et_lastname.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("age", et_age.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("dob", et_dob.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("phone_number", et_phone.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("address", et_address.getText().toString()));
                    nameValuePairs.add(new BasicNameValuePair("gender",
                            radioSex.getText().toString().toLowerCase().equals("Male".toLowerCase())? "M" : "F"));

                    try{
                        HttpClient httpClient = new DefaultHttpClient();
                        HttpPost httpPost = new HttpPost(ENDPOINT);
                        httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        HttpResponse response = httpClient.execute(httpPost);
                        HttpEntity entity = response.getEntity();
                        inputStream = entity.getContent();
                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    try{
                        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream,
                                "iso-8859-1"), 8);
                        StringBuilder builder = new StringBuilder();
                        while((line=reader.readLine())!=null)
                            builder.append(line+"\n");
                        result = builder.toString();
                        inputStream.close();
                        Toast.makeText(ProfileActivity.this, "Successful login : " + result,
                                Toast.LENGTH_SHORT).show();

                        // Clear the fields
                        et_firstname.setText("");
                        et_lastname.setText("");
                        et_age.setText("");
                        et_dob.setText("");
                        et_address.setText("");
                        et_phone.setText("");
                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }
                    /**try{

                    }
                    catch(Exception e){
                        System.out.println("Exception of the type : " + e.toString());
                    }*/

                    if(result.toLowerCase().equals("Successfully updated details".toLowerCase())){
                        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(ProfileActivity.this);
                        dialogBuilder.setTitle("Profile Information Updated Successfully");
                        dialogBuilder.setMessage("You will be redirected to the Main Page");
                        dialogBuilder.setPositiveButton("Sure, take me there!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent mainIntent = new Intent(ProfileActivity.this, MainPage.class);
                                startActivity(mainIntent);
                            }
                        });
                        AlertDialog dialog = dialogBuilder.create();
                        dialog.show();
                    }
                    else if(result.toLowerCase().equals("Already filled in the details".toLowerCase())){
                        System.out.println("Details have already been filled in");
                    }
                    else if(result.toLowerCase().contains("Missing parameters".toLowerCase())){
                        // error of unknown type
                        System.out.println("Some parameters are missing");
                    }
                }
            }
        });
    }
}
