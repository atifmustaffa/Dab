package com.atif.dab;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

/**
 * Created by Atif Mustaffa on 17/5/2018.
 */
public class DebtDatabaseHandler {

    DebtDbHelper debtDbHelper;

    public DebtDatabaseHandler(Context context) {
        this.debtDbHelper = new DebtDbHelper(context);
    }

    public void close() {
        this.debtDbHelper.close();
    }

    public List<Debt> getAllDebt(boolean ongoing) {
        SQLiteDatabase db = debtDbHelper.getReadableDatabase();
        // SQL query elements
        String table = Debt.DebtEntry.TABLE_NAME;
//        String[] columns = {};
        String selection = Debt.DebtEntry.COLUMN_NAME_ONGOING + " = ?";
        String[] selectionArgs;
        if(ongoing) selectionArgs = new String[]{"1"};
        else selectionArgs = new String[]{"0"};
//        String groupBy = "";
//        String having = "";
//        String orderBy = "";

        // Execute sql query
        Cursor cursor = db.query(
                table,              // The table to query
                null,           // The array of columns to return (pass null to get all)
                selection,            // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null           // The sort order
        );

        List<Debt> debtArray = new ArrayList<Debt>();
        while(cursor.moveToNext()) {
            Debt debt = new Debt(
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREATION_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_SETTLE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_ONGOING)) == 1,
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE)) == 1,
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TYPE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TIME)));
            debtArray.add(debt);
        }
        cursor.close();
        close();
        return debtArray;
    }

    public long insertDebt(String debtorName, String creditorName, String debtTitle, double amount, String creationDate,
                           boolean isReminderOn, String repeatType, String reminderDate, String reminderTime) {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME, debtorName);
        values.put(Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME, creditorName);
        values.put(Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE, debtTitle);
        values.put(Debt.DebtEntry.COLUMN_NAME_AMOUNT, amount);
        values.put(Debt.DebtEntry.COLUMN_NAME_CREATION_DATE, creationDate);
        values.put(Debt.DebtEntry.COLUMN_NAME_ONGOING, 1); // Default value 1 for ongoing debt (newly created)
        if(isReminderOn) {
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 1); // Default value 1 for reminder on
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_TYPE, repeatType);
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_DATE, reminderDate);
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_TIME, reminderTime);
        }
        else
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 0); // Default value 0 for reminder off
        long id = db.insert(Debt.DebtEntry.TABLE_NAME, null, values);
        close();
        return id;
    }

    public void updateDebt(String rowId, String debtorName, String creditorName, String debtTitle, double amount,
                           boolean isReminderOn, String repeatType, String reminderDate, String reminderTime) {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME, debtorName);
        values.put(Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME, creditorName);
        values.put(Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE, debtTitle);
        values.put(Debt.DebtEntry.COLUMN_NAME_AMOUNT, amount);
        if(isReminderOn) {
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 1);
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_TYPE, repeatType);
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_DATE, reminderDate);
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_TIME, reminderTime);
        }
        else
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 0);

        String table = Debt.DebtEntry.TABLE_NAME;
        String selection = Debt.DebtEntry._ID + " = ?";
        String[] selectionArgs = new String[]{rowId};
        db.update(table, values, selection, selectionArgs);

        close();
    }

    public Debt getDebt(String id) {
        SQLiteDatabase db = debtDbHelper.getReadableDatabase();

        // SQL query elements
        String table = Debt.DebtEntry.TABLE_NAME;
        String selection = Debt.DebtEntry._ID + " = ?";
        String[] selectionArgs = new String[]{id};

        // Execute sql query
        Cursor cursor = db.query(table, null, selection, selectionArgs, null, null, null);
        Debt debt = null;
        while(cursor.moveToNext()) {
            debt = new Debt(
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREATION_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_SETTLE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_ONGOING)) == 1,
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE)) == 1,
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TYPE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TIME)));
        }
        cursor.close();
        close();
        return debt;
    }

    public void deleteDebt(String id) {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();

        // SQL query elements
        String table = Debt.DebtEntry.TABLE_NAME;
        String selection = Debt.DebtEntry._ID + " = ?";
        String[] selectionArgs = new String[]{id};

        // Execute sql query
        db.delete(table, selection, selectionArgs);
        close();
    }

    public void setDebtAsSettled(String id) {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();
        String dateFormat = "EE, MMM dd yyyy";
        String timeFormat = "hh:mm a";
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat +", " +timeFormat, Locale.getDefault());
        String settledDate = sdf.format(Calendar.getInstance().getTime());

        ContentValues values = new ContentValues();
        values.put(Debt.DebtEntry.COLUMN_NAME_ONGOING, 0);
        values.put(Debt.DebtEntry.COLUMN_NAME_SETTLE_DATE, settledDate);

        String table = Debt.DebtEntry.TABLE_NAME;
        String selection = Debt.DebtEntry._ID + " = ?";
        String[] selectionArgs = new String[]{id};

        db.update(table, values, selection, selectionArgs);
        close();
    }

    public void setReminder(String id, boolean option) {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        if(option)
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 1);
        else
            values.put(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE, 0);

        String table = Debt.DebtEntry.TABLE_NAME;
        String selection = Debt.DebtEntry._ID + " = ?";
        String[] selectionArgs = new String[]{id};

        db.update(table, values, selection, selectionArgs);
        close();
    }

    public void deleteAllDebt() {
        SQLiteDatabase db = debtDbHelper.getWritableDatabase();

        // Truncate table
        db.delete(Debt.DebtEntry.TABLE_NAME,null,null);
        close();
    }

    public List<Debt> getSearchResult(String query, boolean ongoing) {
        SQLiteDatabase db = debtDbHelper.getReadableDatabase();
        // SQL query elements
        String table = Debt.DebtEntry.TABLE_NAME;
//        String[] columns = {};
        String selection = "";
        if(ongoing) selection += Debt.DebtEntry.COLUMN_NAME_ONGOING + " = '1' AND (";
        else selection += Debt.DebtEntry.COLUMN_NAME_ONGOING + " = '0' AND (";
        selection += Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME + " LIKE LOWER('%"+ query + "%') OR "
                +Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME + " LIKE LOWER('%"+ query + "%') OR "
                +Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE + " LIKE LOWER('%"+ query + "%') OR "
                +Debt.DebtEntry.COLUMN_NAME_AMOUNT + " LIKE LOWER('%"+ query + "%'))";
//        String[] selectionArgs = new String[]{"%"+ query + "%"};
//        String groupBy = "";
//        String having = "";
//        String orderBy = "";

        // Execute sql query
        Cursor cursor = db.query(
                true,
                table,              // The table to query
                null,           // The array of columns to return (pass null to get all)
                selection,            // The columns for the WHERE clause
                null,//selectionArgs,              // The values for the WHERE clause
                null,          // don't group the rows
                null,           // don't filter by row groups
                null,           // The sort order
                null
        );

        List<Debt> debtArray = new ArrayList<Debt>();
        while(cursor.moveToNext()) {
            Debt debt = new Debt(
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry._ID)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBTOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREDITOR_NAME)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_DEBT_TITLE)),
                    cursor.getDouble(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_AMOUNT)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_CREATION_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_SETTLE_DATE)),
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_ONGOING)) == 1,
                    cursor.getInt(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_ACTIVE)) == 1,
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TYPE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_DATE)),
                    cursor.getString(cursor.getColumnIndex(Debt.DebtEntry.COLUMN_NAME_REMINDER_TIME)));
            debtArray.add(debt);
        }
        cursor.close();
        close();
        return debtArray;
    }
}
