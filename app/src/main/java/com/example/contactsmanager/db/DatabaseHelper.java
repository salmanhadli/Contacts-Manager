package com.example.contactsmanager.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.contactsmanager.db.entity.Contact;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "contact_bd";


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Contact.CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + Contact.TABLE_NAME);
        onCreate(db);
    }


    // Insert Data into db
    public long insertContact(String name, String email, String number) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(Contact.COLUMN_NAME, name);
        values.put(Contact.COLUMN_EMAIL, email);
        values.put(Contact.COLUMN_PHONE_NUMBER, number);

        long id = db.insert(Contact.TABLE_NAME, null, values);
        db.close();
        return id;
    }

    // Getting Contact from Database
    public Contact getContact(long id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(Contact.TABLE_NAME, new String[]{
                        Contact.COLUMN_ID,
                        Contact.COLUMN_NAME,
                        Contact.COLUMN_EMAIL,
                        Contact.COLUMN_PHONE_NUMBER
                }, Contact.COLUMN_ID + "=?",
                new String[]{
                        String.valueOf(id)
                },
                null,
                null,
                null,
                null);

        if (cursor != null) {
            cursor.moveToFirst();
            Contact contact = new Contact(cursor.getInt(cursor.getColumnIndexOrThrow(Contact.COLUMN_ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_PHONE_NUMBER))
            );

            cursor.close();
            db.close();
            return contact;
        }
        return null;
    }


    // Getting all Contacts
    public ArrayList<Contact> getAllContacts() {
        ArrayList<Contact> contacts = new ArrayList<Contact>();

        String selectQuery = "SELECT * FROM " + Contact.TABLE_NAME + " ORDER BY " +
                Contact.COLUMN_ID + " DESC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                Contact contact = new Contact(cursor.getInt(cursor.getColumnIndexOrThrow(Contact.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_EMAIL)),
                        cursor.getString(cursor.getColumnIndexOrThrow(Contact.COLUMN_PHONE_NUMBER))
                );

                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return contacts;
    }

    // Update Contact
    public int updateContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Contact.COLUMN_NAME, contact.getName());
        values.put(Contact.COLUMN_EMAIL, contact.getEmail());
        values.put(Contact.COLUMN_PHONE_NUMBER, contact.getNumber());
        int response = db.update(Contact.TABLE_NAME, values, Contact.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(contact.getId())});
        db.close();
        return response;
    }


    // Delete Contact
    public void deleteContact(Contact contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(Contact.TABLE_NAME, Contact.COLUMN_ID + " = ? ",
                new String[]{String.valueOf(contact.getId())});

        db.close();
    }
}
