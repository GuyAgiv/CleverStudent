package com.example.guy.cleverstudentapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class MeetupActivity extends Activity {

    private static final int SPEECH_REQUEST_CODE = 0;
    private static final int CONTACT_LIST_REQUEST_CODE = 1;

    private static final int SEND_SMS_PERMISSION = 1;


    private Button speechRec;
    private TextView outputSpeech;
    private TextView amountOfPhoneNumbersTv;
    private ListView contactsList;
    private Button addContacts;
    private Button submitBtn;

    private ArrayList<String> contactsPhoneNumbers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetup);

        addContacts = findViewById(R.id.addContacts_btn);
        contactsList = findViewById(R.id.contacts_listView);
        speechRec = findViewById(R.id.speech_btn);
        outputSpeech = findViewById(R.id.msg_editText);
        submitBtn = findViewById(R.id.submit_btn);
        amountOfPhoneNumbersTv = findViewById(R.id.amountFriendsAdded_textView);


        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                TestBeforeSend();
            }
        });


        speechRec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                displaySpeechRecognizer();
            }
        });


        addContacts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent friendsInsertionIntent = new Intent(MeetupActivity.this, FriendsInsertionActivity.class);
                startActivityForResult(friendsInsertionIntent, CONTACT_LIST_REQUEST_CODE);
            }
        });



    }

    private void TestBeforeSend()
    {
        if(contactsPhoneNumbers != null && contactsPhoneNumbers.size() > 0)
        {
            String theMessageBody = outputSpeech.getText().toString();

            if(theMessageBody.length() > 0)
            {

                // Runtime permissions required!
                if (Build.VERSION.SDK_INT >= 23)
                {
                    int hasSendSMSPermission = checkSelfPermission(Manifest.permission.SEND_SMS);

                    if (hasSendSMSPermission == PackageManager.PERMISSION_GRANTED)
                    {
                        SendMessageToPhoneNumbers(theMessageBody);
                    }
                    else {
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, SEND_SMS_PERMISSION);
                    }
                }
                else
                {
                    SendMessageToPhoneNumbers(theMessageBody);
                }
            }

            else
            {
                Toast.makeText(MeetupActivity.this,getResources().getString(R.string.empty_message_error),Toast.LENGTH_SHORT).show();
            }
        }

        else
        {
            Toast.makeText(MeetupActivity.this, getResources().getString(R.string.no_added_friends_message_error),Toast.LENGTH_SHORT).show();
        }
    }


    private void SendMessageToPhoneNumbers(String theMessageBody)
    {
        try
        {
            for (String phoneNumber : contactsPhoneNumbers)
            {
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, theMessageBody, null, null);
            }
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sms_successfully_sent),Toast.LENGTH_SHORT).show();
        }
        catch (Exception e)
        {
            //Error Occurred if No Messages Selected
            Toast.makeText(getApplicationContext(),getResources().getString(R.string.sms_failed_error),Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult ( int requestCode, int resultCode, Intent data)
    {
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK)
        {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            outputSpeech.setText(spokenText);
        }

        else if(requestCode == CONTACT_LIST_REQUEST_CODE)
        {
            if(resultCode == RESULT_OK)
            {
                contactsPhoneNumbers = data.getStringArrayListExtra("theContactsArray");

                amountOfPhoneNumbersTv.setText(contactsPhoneNumbers.size() + " " + getResources().getString(R.string.amount_friends_added));

            }
            else if ( resultCode == RESULT_CANCELED )
            {
                amountOfPhoneNumbersTv.setText(getResources().getString(R.string.no_friends_added_question));
            }

        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void displaySpeechRecognizer ()
    {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(requestCode == SEND_SMS_PERMISSION)
        {
            if(grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                TestBeforeSend();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(MeetupActivity.this);
                builder.setTitle(getResources().getString(R.string.gotoSettings)).setMessage(getResources().getString(R.string.permissionsRequired))
                        .setPositiveButton(getResources().getString(R.string.yes_permissions), new OpenAppSettingsDialogListener()).setNegativeButton(getResources().getString(R.string.no_permissions), new OpenAppSettingsDialogListener()).show();
            }
        }
    }

    private class OpenAppSettingsDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
            if (i == DialogInterface.BUTTON_POSITIVE) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else if (i == DialogInterface.BUTTON_NEGATIVE) {

            }
        }
    }
}