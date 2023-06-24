package com.example.contactsmanager.db;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.contactsmanager.db.entity.Contact;

@Database(entities = { Contact.class }, version = 1)
public abstract class ContactsAppDatabase extends RoomDatabase {

    // linking DAO with our db
    public abstract ContactDAO getContactDAO();
}
