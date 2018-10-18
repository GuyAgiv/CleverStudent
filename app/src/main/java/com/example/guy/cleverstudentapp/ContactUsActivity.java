package com.example.guy.cleverstudentapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ContactUsActivity extends Activity
{

    private EditText nameEt;
    private EditText subjectEt;
    private EditText mailAddressEt;
    private EditText messageBodyEt;
    private Button sendMailBtn;

    private String mailBody;
    private String mailSubject;
    private String userMailAddress;
    private String userName;
    private final String developerMail = "guyagiv@gmail.com";

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);

        nameEt = findViewById(R.id.sender_name_editText);
        subjectEt = findViewById(R.id.email_subject_editText);
        mailAddressEt = findViewById(R.id.sender_email_editText);
        messageBodyEt = findViewById(R.id.email_message_editText);
        sendMailBtn = findViewById(R.id.submit_email_btn);

        userName = getIntent().getStringExtra("user_name").toString();
        nameEt.setText(userName);


        sendMailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                String sentby = getResources().getString(R.string.sentby_txt);
                userMailAddress = mailAddressEt.getText().toString();
                mailBody = messageBodyEt.getText().toString();
                mailSubject = subjectEt.getText().toString();

                Boolean isFormFieldsOK = checkFormFields(mailBody, mailSubject, userMailAddress);
                if(isFormFieldsOK)
                {
                    mailBody = mailBody + "\n" + sentby + " " + userName;

                    Intent intent = new Intent(Intent.ACTION_SEND);
                    intent.putExtra(Intent.EXTRA_TEXT, mailBody);
                    intent.putExtra(Intent.EXTRA_SUBJECT, mailSubject);
                    intent.putExtra(Intent.EXTRA_EMAIL, new String[] {developerMail});
                    intent.setType("text/*");

                    startActivity(intent);
                }
                else
                {
                    Toast.makeText(ContactUsActivity.this,getResources().getString(R.string.fill_form_message), Toast.LENGTH_LONG).show();
                };

            }
        });
    }

    private Boolean checkFormFields(String mailBody, String mailSubject, String userMailAddress)
    {
        Boolean isValid = true;

        if(mailBody.matches("") || mailSubject.matches("") || userMailAddress.matches(""))
        {
            isValid = false;
        }

        return isValid;
    }
}
