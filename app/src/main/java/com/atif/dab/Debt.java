package com.atif.dab;

import android.provider.BaseColumns;

/**
 * Created by Atif Mustaffa on 13/4/2018.
 */
public class Debt {

    private int id;
    private String debtorName, creditorName, debtTitle, reminderType, creationDate, settleDate, reminderDate, reminderTime;
    private double amount;
    private boolean ongoing, reminderActive;

    public Debt() {
    }

    public Debt(int id, String debtorName, String creditorName, String debtTitle, double amount, String creationDate, String settleDate, boolean ongoing, boolean reminderActive, String reminderType, String reminderDate, String reminderTime) {
        this.id = id;
        this.debtorName = debtorName;
        this.creditorName = creditorName;
        this.debtTitle = debtTitle;
        this.amount = amount;
        this.creationDate = creationDate;
        this.settleDate = settleDate;
        this.ongoing = ongoing;
        this.reminderActive = reminderActive;
        this.reminderType = reminderType;
        this.reminderDate = reminderDate;
        this.reminderTime = reminderTime;
    }

    public int getId() {
        return id;
    }

    public String getDebtorName() {
        return debtorName;
    }

    public void setDebtorName(String debtorName) {
        this.debtorName = debtorName;
    }

    public String getCreditorName() {
        return creditorName;
    }

    public void setCreditorName(String creditorName) {
        this.creditorName = creditorName;
    }

    public String getDebtTitle() {
        return debtTitle;
    }

    public void setDebtTitle(String debtTitle) {
        this.debtTitle = debtTitle;
    }

    public String getReminderType() {
        return reminderType;
    }

    public void setReminderType(String reminderType) {
        this.reminderType = reminderType;
    }

    public String getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(String creationDate) {
        this.creationDate = creationDate;
    }

    public String getReminderDate() {
        return reminderDate;
    }

    public void setReminderDate(String reminderDate) {
        this.reminderDate = reminderDate;
    }

    public String getReminderTime() {
        return reminderTime;
    }

    public void setReminderTime(String reminderTime) {
        this.reminderTime = reminderTime;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public boolean isOngoing() {
        return ongoing;
    }

    public void setOngoing(boolean ongoing) {
        this.ongoing = ongoing;
    }

    public boolean isReminderActive() {
        return reminderActive;
    }

    public void setReminderActive(boolean reminderActive) {
        this.reminderActive = reminderActive;
    }

    public String getSettleDate() {
        return settleDate;
    }

    public void setSettleDate(String settleDate) {
        this.settleDate = settleDate;
    }

    public static class DebtEntry implements BaseColumns {
        public static final String TABLE_NAME = "Debt";
        public static final String COLUMN_NAME_DEBTOR_NAME = "DebtorName";
        public static final String COLUMN_NAME_CREDITOR_NAME = "CreditorName";
        public static final String COLUMN_NAME_DEBT_TITLE = "DebtTitle";
        public static final String COLUMN_NAME_AMOUNT = "Amount";
        public static final String COLUMN_NAME_CREATION_DATE = "CreationDate";
        public static final String COLUMN_NAME_SETTLE_DATE = "SettleDate";
        public static final String COLUMN_NAME_ONGOING = "Ongoing";
        public static final String COLUMN_NAME_REMINDER_ACTIVE = "ReminderActive";
        public static final String COLUMN_NAME_REMINDER_TYPE = "ReminderType";
        public static final String COLUMN_NAME_REMINDER_DATE = "ReminderDate";
        public static final String COLUMN_NAME_REMINDER_TIME = "ReminderTime";

    }

    public static final String SQL_CREATE_TABLE =
            "CREATE TABLE " + DebtEntry.TABLE_NAME + " (" +
                DebtEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                DebtEntry.COLUMN_NAME_DEBTOR_NAME + " TEXT NOT NULL, " +
                DebtEntry.COLUMN_NAME_CREDITOR_NAME + " TEXT NOT NULL, " +
                DebtEntry.COLUMN_NAME_DEBT_TITLE + " TEXT NOT NULL, " +
                DebtEntry.COLUMN_NAME_AMOUNT + " DECIMAL(18,2) NOT NULL, " +
                DebtEntry.COLUMN_NAME_CREATION_DATE + " TEXT NOT NULL, " +
                DebtEntry.COLUMN_NAME_SETTLE_DATE + " TEXT, " +
                DebtEntry.COLUMN_NAME_ONGOING + " INTEGER DEFAULT 1 NOT NULL, " +
                DebtEntry.COLUMN_NAME_REMINDER_ACTIVE + " INTEGER DEFAULT 0 NOT NULL, " +
                DebtEntry.COLUMN_NAME_REMINDER_TYPE + " TEXT, " +
                DebtEntry.COLUMN_NAME_REMINDER_DATE + " TEXT, " +
                DebtEntry.COLUMN_NAME_REMINDER_TIME + " TEXT)";

    public static final String SQL_DROP_TABLE =
            "DROP TABLE IF EXISTS " + DebtEntry.TABLE_NAME;
}
