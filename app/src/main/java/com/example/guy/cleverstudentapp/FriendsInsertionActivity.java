package com.example.guy.cleverstudentapp;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeMap;


public class FriendsInsertionActivity extends Activity {

    private TreeMap<String, String> contactListDictionary = new TreeMap<>();
    private ArrayList<String> filteredContactsPhoneNumbers = new ArrayList<>();
    private Set<String> contactNameSet;
    private final ArrayList<Contact> contactArrayList = new ArrayList<>();
    private ProgressBar progressBar;
    private ListView contactListView;
    private Handler handler = new Handler();
    private LinearLayout progressLayout;
    private static int READ_WRITE_CONTACTS_PERMISSION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friends_insertion);


        contactListView = findViewById(R.id.contacts_listView);
        Button addFriendsButton = findViewById(R.id.addContactsFriends_btn);
        progressBar = findViewById(R.id.importFriends_progressBar);
        progressLayout = findViewById(R.id.progressbarLayout);

        // Runtime permissions required!
        if (Build.VERSION.SDK_INT >= 23)
        {
            int hasRCPermission = checkSelfPermission(Manifest.permission.READ_CONTACTS);
            int hasWCPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);

            if (hasRCPermission == PackageManager.PERMISSION_GRANTED && hasWCPermission == PackageManager.PERMISSION_GRANTED )
            {
                ImportContactListAsync();
            }
            else
                {
                requestPermissions(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_CONTACTS}, READ_WRITE_CONTACTS_PERMISSION);
            }
        }
        else
        {
            ImportContactListAsync();
        }

            addFriendsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {


                    for(Contact contact : contactArrayList)
                    {
                        if(contact.getAdded())
                        {
                            filteredContactsPhoneNumbers.add(contact.getPhoneNumber());
                        }
                    }


                    Intent intent = new Intent();

                    intent.putStringArrayListExtra("theContactsArray",filteredContactsPhoneNumbers);
                    setResult(RESULT_OK,intent);

                    finish();
                }
            });
    }

        private void ImportContactListAsync()
        {
            new Thread()
            {
                @Override
                public void run()
                {
                    super.run();

                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            progressLayout.setVisibility(View.VISIBLE);
                            progressBar.setVisibility(View.VISIBLE);
                        }
                    });

                    PopulateContactList();

                    handler.post(new Runnable() {
                        @Override
                        public void run()
                        {
                            progressLayout.setVisibility(View.GONE);
                            progressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }.start();
        }
        private void PopulateContactList()
        {

            Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
            while (cursor.moveToNext()) {
                String contactId = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
                String hasPhone = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));

                if ("1".equals(hasPhone) || Boolean.parseBoolean(hasPhone)) {
                    // You know it has a number so now query it like this
                    Cursor phones = this.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " + contactId, null, null);
                    while (phones.moveToNext()) {
                        String phoneNumber = phones.getString(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                        int itype = phones.getInt(phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                        final boolean isMobile =
                                itype == ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE ||
                                        itype == ContactsContract.CommonDataKinds.Phone.TYPE_WORK_MOBILE;

                        // Do something here with 'phoneNumber' such as saving into
                        // the List or Array that will be used in your 'ListView'.
                        contactListDictionary.put(name, phoneNumber);
                    }


                    phones.close();
                }
            }

            contactNameSet = contactListDictionary.keySet();

            for(String name : contactNameSet)
            {
                String phoneNumber = contactListDictionary.get(name);
                // Later.. try to find how to import the picture of the contact in the user's mobile...
                contactArrayList.add(new Contact(name, phoneNumber, false, R.drawable.avatar));
            }

            handler.post(new Runnable() {
                @Override
                public void run()
                {
                    ContactAdapter contactAdapter = new ContactAdapter(contactArrayList, FriendsInsertionActivity.this);
                    contactListView.setAdapter(contactAdapter);
                }
            });



        }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,  int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == READ_WRITE_CONTACTS_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED)
            {
                ImportContactListAsync();
            }
            else
            {
                AlertDialog.Builder builder = new AlertDialog.Builder(FriendsInsertionActivity.this);
                builder.setTitle(getResources().getString(R.string.gotoSettings)).setMessage(getResources().getString(R.string.permissionsRequired))
                        .setPositiveButton(getResources().getString(R.string.yes_permissions), new OpenAppSettingsDialogListener()).setNegativeButton(getResources().getString(R.string.no_permissions), new OpenAppSettingsDialogListener()).show();
            }
        }
    }

    private class OpenAppSettingsDialogListener implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialogInterface, int i)
        {
            progressLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.GONE);

            if (i == DialogInterface.BUTTON_POSITIVE)
            {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);

                finish();
            }
            else if (i == DialogInterface.BUTTON_NEGATIVE)
            {
                finish();
            }
        }
    }
}

