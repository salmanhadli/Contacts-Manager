package com.example.contactsmanager.db.entity;

public class Contact {

    // Constants for Database
    public static final String TABLE_NAME = "contacts";
    public static final String COLUMN_ID = "contactId";
    public static final String COLUMN_NAME = "contactName";
    public static final String COLUMN_EMAIL = "contactEmail";
    public static final String COLUMN_PHONE_NUMBER = "contactNumber";
    // SQL QUERY: Creating a Table
    public static final String CREATE_TABLE = "CREATE TABLE " +
            TABLE_NAME + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
            COLUMN_NAME + " TEXT," + COLUMN_EMAIL + " DATETIME DEFAULT CURRENT_TIMESTAMP," +
            COLUMN_PHONE_NUMBER + " TEXT" +
            ")";


    // Variables
    private int id;
    private String name;
    private String email;
    private String number;

    public Contact() {
    }

    public Contact(int id, String name, String email, String number) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.number = number;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
