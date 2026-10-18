package com.example.spendora;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DBHelper extends SQLiteOpenHelper {

    // Database Information
    private static final String DATABASE_NAME = "Spendora.db";
    private static final int DATABASE_VERSION = 1;

    // Table Name
    public static final String TABLE_NAME = "expenses";

    // Column Names
    public static final String COL_ID = "id";
    public static final String COL_NAME = "expense_name";
    public static final String COL_AMOUNT = "amount";
    public static final String COL_CATEGORY = "category";
    public static final String COL_TYPE = "expense_type";
    public static final String COL_ESSENTIAL = "is_essential";
    public static final String COL_DATE = "expense_date";

    // Constructor
    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // Create Database Table
    @Override
    public void onCreate(SQLiteDatabase db) {

        String createTable = "CREATE TABLE " + TABLE_NAME + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT, " +
                COL_AMOUNT + " REAL, " +
                COL_CATEGORY + " TEXT, " +
                COL_TYPE + " TEXT, " +
                COL_ESSENTIAL + " INTEGER, " +
                COL_DATE + " TEXT)";

        db.execSQL(createTable);
    }

    // Upgrade Database
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    // CREATE - Add New Expense
    public boolean addExpense(String name,
                              double amount,
                              String category,
                              String type,
                              boolean essential,
                              String date) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_AMOUNT, amount);
        values.put(COL_CATEGORY, category);
        values.put(COL_TYPE, type);
        values.put(COL_ESSENTIAL, essential ? 1 : 0);
        values.put(COL_DATE, date);

        long result = db.insert(TABLE_NAME, null, values);

        return result != -1;
    }

    // READ - Get All Expenses
    public Cursor getAllExpenses() {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " ORDER BY " + COL_ID + " DESC",
                null
        );
    }

    // READ - Get Expense By ID
    public Cursor getExpenseById(int id) {

        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT * FROM " + TABLE_NAME +
                        " WHERE " + COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
    }

    // UPDATE - Update Existing Expense
    public boolean updateExpense(int id,
                                 String name,
                                 double amount,
                                 String category,
                                 String type,
                                 boolean essential) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(COL_NAME, name);
        values.put(COL_AMOUNT, amount);
        values.put(COL_CATEGORY, category);
        values.put(COL_TYPE, type);
        values.put(COL_ESSENTIAL, essential ? 1 : 0);

        int result = db.update(
                TABLE_NAME,
                values,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // DELETE - Delete Expense
    public boolean deleteExpense(int id) {

        SQLiteDatabase db = this.getWritableDatabase();

        int result = db.delete(
                TABLE_NAME,
                COL_ID + " = ?",
                new String[]{String.valueOf(id)}
        );

        return result > 0;
    }

    // Calculate Total Expenses
    public double getTotalExpenses() {

        SQLiteDatabase db = this.getReadableDatabase();

        double total = 0;

        try (Cursor cursor = db.rawQuery(
                "SELECT SUM(" + COL_AMOUNT + ") FROM " + TABLE_NAME,
                null
        )) {

            if (cursor.moveToFirst() && !cursor.isNull(0)) {
                total = cursor.getDouble(0);
            }
        }

        return total;
    }

    // Count Total Transactions
    public int getTransactionCount() {

        SQLiteDatabase db = this.getReadableDatabase();

        int count = 0;

        try (Cursor cursor = db.rawQuery(
                "SELECT COUNT(*) FROM " + TABLE_NAME,
                null
        )) {

            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }

        return count;
    }
}