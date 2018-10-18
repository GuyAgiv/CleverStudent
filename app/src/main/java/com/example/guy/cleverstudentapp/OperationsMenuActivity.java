package com.example.guy.cleverstudentapp;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class OperationsMenuActivity extends Activity
{
    private Button meetup;
    private Button contactUs;
    private Button navigateAcademy;
    private TextView helloUserTV;
    private String userName;
    private String academicName;
    private Button calenderBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_operations_menu);


        helloUserTV = findViewById(R.id.hellouser_text);
        contactUs = findViewById(R.id.contact_us_btn);
        navigateAcademy = findViewById(R.id.navigate_btn);
        calenderBtn = findViewById(R.id.calender_btn);

         userName = getIntent().getStringExtra("user_name");
         academicName = getIntent().getStringExtra("academic_institue_name");


        String fullyMsg = getString(R.string.hello_text)  + userName + " " + getString(R.string.perform_txt);

        helloUserTV.setText(fullyMsg);

        meetup = findViewById(R.id.setSession_btn);

        meetup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent meetUpActivity = new Intent(OperationsMenuActivity.this, MeetupActivity.class);
                startActivity(meetUpActivity);
            }
        });

        contactUs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent contactUsIntent = new Intent(OperationsMenuActivity.this, ContactUsActivity.class);


                contactUsIntent.putExtra("user_name", userName);
                contactUsIntent.putExtra("academic_institue_name", academicName);

                startActivity(contactUsIntent);
            }
        });


        calenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent intent = new Intent(Intent.ACTION_EDIT);
                intent.setData(CalendarContract.Events.CONTENT_URI);

                startActivity(intent);
            }
        });



        navigateAcademy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                try
                {
                    String url = String.format("https://waze.com/ul?q=%s", academicName);
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( url ) );
                    startActivity( intent );
                }
                catch ( ActivityNotFoundException ex  )
                {
                    // If Waze is not installed, open it in Google Play:
                    Intent intent = new Intent( Intent.ACTION_VIEW, Uri.parse( "market://details?id=com.waze" ) );
                    startActivity(intent);
                }
            }
        });
    }
}
