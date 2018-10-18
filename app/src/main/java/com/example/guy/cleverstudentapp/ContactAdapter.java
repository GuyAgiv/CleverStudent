package com.example.guy.cleverstudentapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class ContactAdapter extends BaseAdapter implements CompoundButton.OnCheckedChangeListener
{
    private List<Contact> contacts;
    private Context context;

    public ContactAdapter(List<Contact> contacts, Context context) {
        this.contacts = contacts;
        this.context = context;
    }

    @Override
    public int getCount() {
        return contacts.size();
    }

    @Override
    public Object getItem(int i) {
        return contacts.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(view == null)
        {
            LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = layoutInflater.inflate(R.layout.contact_cell, viewGroup, false);
        }

        Contact contact = contacts.get(i);

        TextView contactName = view.findViewById(R.id.contact_name);
        CheckBox isAddedCheckBox = view.findViewById(R.id.addContact_checkBox);
        isAddedCheckBox.setTag(i);
        isAddedCheckBox.setOnCheckedChangeListener(this);
        ImageView contactImage = view.findViewById(R.id.contact_icon);

        contactName.setText(contact.getName());
        isAddedCheckBox.setChecked(contact.getAdded());
        contactImage.setImageResource(contact.getProfileImageResId());

        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        int position = (Integer)compoundButton.getTag();
        Contact contact = contacts.get(position);
        contact.setAdded(b);
    }
}
