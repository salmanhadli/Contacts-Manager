package com.example.contactsmanager;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.contactsmanager.db.ContactsAppDatabase;
import com.example.contactsmanager.db.entity.Contact;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    private ContactsAdapter contactsAdapter;
    private ArrayList<Contact> contactArrayList = new ArrayList<Contact>();
    private RecyclerView recyclerView;

    private ContactsAppDatabase contactsAppDatabase;
//    private DatabaseHelper db;

    // Menu Bar
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.actionSetting) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("My Contacts");

        // Recycler View
        recyclerView = findViewById(R.id.recyclerViewContacts);

        // Callbacks
        RoomDatabase.Callback dbCallback = new RoomDatabase.Callback() {
            @Override
            public void onCreate(@NonNull SupportSQLiteDatabase db) {
                super.onCreate(db);

                // These are 4 contacts already created in the app when installed (Built-in contacts)
//                createContact("Bill Gates", "billGates@microsoft.com", "1234567890");
//                createContact("Nicoli Tesla", "tesla@gmail.com", "1112223334");
//                createContact("Mark Zukerberg", "markZuck@facebook.com", "44455556667");
//                createContact("Elon Tusk", "elonTusk@microsoft.com", "0987654321");
                Log.i("TAG", "Database has been created");
            }

            @Override
            public void onOpen(@NonNull SupportSQLiteDatabase db) {
                super.onOpen(db);

                Log.i("TAG", "Database has been opened");
            }
        };


        contactsAppDatabase = Room.databaseBuilder(
                getApplicationContext(),
                ContactsAppDatabase.class,
                "ContactDB"
        ).addCallback(dbCallback).build();
//        db = new DatabaseHelper(this);

        // Contact List
//        contactArrayList.addAll(db.getAllContacts());
//        contactArrayList.addAll(contactsAppDatabase.getContactDAO().getAllContacts());
        displayAllContactsInBackground();

        contactsAdapter = new ContactsAdapter(this, contactArrayList, MainActivity.this);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(contactsAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addAndEditContacts(false, null, -1);
            }
        });
    }

    public void addAndEditContacts(final boolean isUpdated, final Contact contact, final int position) {

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        View view = layoutInflater.inflate(R.layout.add_contact, null);

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
        alertDialog.setView(view);

        TextView contactTitle = view.findViewById(R.id.newContactTitle);
        final EditText newContactName = view.findViewById(R.id.name);
        final EditText newContactEmail = view.findViewById(R.id.email);
        final EditText newContactNumber = view.findViewById(R.id.number);

        contactTitle.setText(isUpdated ? "Edit Contact" : "Add New Contact");

        if (isUpdated && contact != null) {
            newContactName.setText(contact.getName());
            newContactEmail.setText(contact.getEmail());
            newContactNumber.setText(contact.getNumber());
        }

        alertDialog.setCancelable(false).setPositiveButton(isUpdated ? "UPDATE" : "SAVE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setNegativeButton(isUpdated ? "DELETE" : "CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (isUpdated) {
                            DeleteContact(contact, position);
                        } else {
                            dialog.cancel();
                        }
                    }
                });

        final AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
        alertDialog1.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(newContactName.getText().toString())) {
                    Toast.makeText(MainActivity.this, "Please Enter the Name", Toast.LENGTH_SHORT).show();
                } else {
                    alertDialog1.dismiss();
                }

                if (isUpdated && contact != null) {
                    updateContact(newContactName.getText().toString(), newContactEmail.getText().toString(), newContactNumber.getText().toString(), position);
                } else {
                    createContact(newContactName.getText().toString(), newContactEmail.getText().toString(), newContactNumber.getText().toString());
                }
            }
        });
    }

    private void createContact(String name, String email, String number) {
//        long id = db.insertContact(name, email, number);
        long id = contactsAppDatabase.getContactDAO().addContact(new Contact(0, name, email, number));
//        Contact contact = db.getContact(id);
        Contact contact = contactsAppDatabase.getContactDAO().getContact(id);

        if (contact != null) {
            contactArrayList.add(0, contact);
            contactsAdapter.notifyDataSetChanged();
        }
    }

    private void updateContact(String name, String email, String number, int position) {
        Contact contact = contactArrayList.get(position);
        contact.setName(name);
        contact.setEmail(email);
        contact.setNumber(number);

//        db.updateContact(contact);
        contactsAppDatabase.getContactDAO().updateContact(contact);
        contactArrayList.set(position, contact);
        contactsAdapter.notifyDataSetChanged();
    }

    private void DeleteContact(Contact contact, int position) {
        contactArrayList.remove(position);
//        db.deleteContact(contact);
        contactsAppDatabase.getContactDAO().deleteContact(contact);
        contactsAdapter.notifyDataSetChanged();
    }

    private void displayAllContactsInBackground() {
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        Handler handler = new Handler(Looper.getMainLooper());
        executorService.execute(new Runnable() {
            @Override
            public void run() {
                // execute the background work
                contactArrayList.addAll(contactsAppDatabase.getContactDAO().getAllContacts());

                // executed after gb work finished
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        contactsAdapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }


    // NO ROOM DB
    // SQLITE DB
}