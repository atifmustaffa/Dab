package com.atif.dab;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddEditActivity extends AppCompatActivity {

    DebtDatabaseHandler debtDatabaseHandler;
    Switch reminderSwitch;
    Spinner repeatSpinner;
    LinearLayout[] linearLayout;
    EditText debtorNameET, creditorNameET, debtTitleET, amountET, dateET, timeET;
    TextView dateTV;
    Calendar myCalendar;
    DatePickerDialog.OnDateSetListener date;
    TimePickerDialog.OnTimeSetListener time;
    Button addBtn, saveBtn,  cancelBtn;
    String dateFormat = "EE, MMM dd yyyy";
    String timeFormat = "hh:mm a";
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit);
        reminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        repeatSpinner = (Spinner) findViewById(R.id.repeatSpinner);
        linearLayout = new LinearLayout[2];
        linearLayout[0] = (LinearLayout) findViewById(R.id.linearLayout1);
        linearLayout[1] = (LinearLayout) findViewById(R.id.linearLayout2);

        // Disable all child elements in linLayouts of reminder option
        for (LinearLayout layout: linearLayout)
            disableChild(layout, true);

        debtorNameET = (EditText) findViewById(R.id.debtorNameET);
        creditorNameET = (EditText) findViewById(R.id.creditorNameET);
        debtTitleET = (EditText) findViewById(R.id.debtTitleET);
        amountET = (EditText) findViewById(R.id.amountET);
        dateET = (EditText) findViewById(R.id.dateET);
        timeET = (EditText) findViewById(R.id.timeET);
        dateTV = (TextView) findViewById(R.id.dateTV);
        addBtn = (Button) findViewById(R.id.addBtn);
        saveBtn = (Button) findViewById(R.id.saveBtn);
        cancelBtn = (Button) findViewById(R.id.cancelBtn);

        debtDatabaseHandler = new DebtDatabaseHandler(AddEditActivity.this);

        myCalendar = Calendar.getInstance();
        date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateLabel();
            }

        };
        time = new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                myCalendar.set(Calendar.HOUR_OF_DAY, selectedHour);
                myCalendar.set(Calendar.MINUTE, selectedMinute);
                updateTimeLabel();
            }
        };

        reminderSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    for (LinearLayout layout: linearLayout)
                        disableChild(layout, false);
                }
                else {
                    for (LinearLayout layout: linearLayout)
                        disableChild(layout, true);
//                    dateET.setText("");
//                    timeET.setText("");
//                    repeatSpinner.setSelection(0);
                }
            }
        });

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                switch (i) {
                    case 0: dateTV.setText(R.string.date_lbl); break;
                    default: dateTV.setText(R.string.start_date_lbl);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                dateTV.setText(R.string.date_lbl);
            }
        });

        dateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(AddEditActivity.this, R.style.DabTheme_DialogTheme, date,
                        myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        timeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new TimePickerDialog(AddEditActivity.this, R.style.DabTheme_DialogTheme, time,
                        myCalendar.get(Calendar.HOUR_OF_DAY),
                        myCalendar.get(Calendar.MINUTE), false).show();
            }
        });

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValid()) {
                    SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
                    String creationDate = sdf.format(Calendar.getInstance().getTime());

                    // Insert debt to database
                    long newRowId = debtDatabaseHandler.insertDebt(debtorNameET.getText().toString(),
                            creditorNameET.getText().toString(),
                            debtTitleET.getText().toString(),
                            Double.parseDouble(amountET.getText().toString()),
                            creationDate,
                            reminderSwitch.isChecked(),
                            repeatSpinner.getSelectedItem().toString(),
                            dateET.getText().toString(),
                            timeET.getText().toString()
                    );
                    setAlarm("" + newRowId);
                    Log.i(TAG, "Successfully inserted new data with id: " + newRowId);
                    Toast.makeText(getApplicationContext(), "New debt created", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else showEmptyFillDialogBox();

            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(isValid()) {
                    // Update data in db
                    String editItemId = getIntent().getExtras().getString("com.atif.dab.editItemId");
                    debtDatabaseHandler.updateDebt(editItemId,
                            debtorNameET.getText().toString(),
                            creditorNameET.getText().toString(),
                            debtTitleET.getText().toString(),
                            Double.parseDouble(amountET.getText().toString()),
                            reminderSwitch.isChecked(),
                            repeatSpinner.getSelectedItem().toString(),
                            dateET.getText().toString(),
                            timeET.getText().toString()
                    );
                    setAlarm(editItemId);
                    Log.i(TAG, "Successfully update data (edit data) with id: " + editItemId);
                    Toast.makeText(getApplicationContext(), "Saved", Toast.LENGTH_SHORT).show();
                    finish();
                }
                else showEmptyFillDialogBox();

            }
        });

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        // Check intent extras (source of intent) then execute accordingly
        if(getIntent().getExtras().getString("com.atif.dab.addEdit").equals("add")) {
            saveBtn.setVisibility(View.GONE);
            setTitle(R.string.add_debt_lbl);
        }
        else {
            addBtn.setVisibility(View.GONE);
            setTitle(R.string.edit_debt_lbl);
            fillDetails();
        }
        if(getIntent().hasExtra("com.atif.dab.addEditOption")) {
            if (getIntent().getExtras().getString("com.atif.dab.addEditOption").equals("creditor")) {
                creditorNameET.setText(R.string.you_lbl);
                creditorNameET.setEnabled(false);
            } else {
                debtorNameET.setText(R.string.you_lbl);
                debtorNameET.setEnabled(false);
                creditorNameET.requestFocus();
            }
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private void disableChild(LinearLayout layout, boolean disable) {
        for (int i = 0; i < layout.getChildCount(); i++){
            View view = layout.getChildAt(i);
            if (view instanceof LinearLayout){
                disableChild((LinearLayout) view, disable);
            }
            else
                view.setEnabled(!disable);
        }
    }

    private void updateDateLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat, Locale.getDefault());
        dateET.setText(sdf.format(myCalendar.getTime()));
    }

    private void updateTimeLabel() {
        SimpleDateFormat sdf = new SimpleDateFormat(timeFormat, Locale.getDefault());
        timeET.setText(sdf.format(myCalendar.getTime()));
    }

    private void fillDetails() {
        String editItemId = getIntent().getExtras().getString("com.atif.dab.editItemId");
        Debt d = debtDatabaseHandler.getDebt(editItemId);
        if(d.getCreditorName().equals(getText(R.string.you_lbl))) {
            creditorNameET.setText(R.string.you_lbl);
            creditorNameET.setEnabled(false);
            debtorNameET.setText(d.getDebtorName());
        } else {
            debtorNameET.setText(R.string.you_lbl);
            debtorNameET.setEnabled(false);
            creditorNameET.setText(d.getCreditorName());
        }
        debtTitleET.setText(d.getDebtTitle());
        amountET.setText(String.format ("%.2f", d.getAmount()));
        dateET.setText(d.getReminderDate());
        timeET.setText(d.getReminderTime());

        if(d.isReminderActive()) reminderSwitch.setChecked(true);
        String type = d.getReminderType();
        String[] types = getResources().getStringArray(R.array.repeat_options);
        if(type != null)
            for (int i = 0; i < types.length; i++) {
                String s = types[i];
                if (type.equals(s)) repeatSpinner.setSelection(i);
            }
    }

    private boolean isValid() {
        // Check for empty fill
        if(debtorNameET.getText().toString().trim().equals("") ||
                creditorNameET.getText().toString().trim().equals("") ||
                debtTitleET.getText().toString().trim().equals("") ||
                amountET.getText().toString().trim().equals("")) {
            return false;
        }
        if(reminderSwitch.isChecked())
            if(dateET.getText().toString().equals("") ||
                    timeET.getText().toString().equals(""))
                return false;
        return true;
    }

    private void showEmptyFillDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(AddEditActivity.this).create();
        alertDialog.setTitle(getText(R.string.empty_lbl));
        alertDialog.setMessage(getText(R.string.empty_fill_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(android.R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void setAlarm(String id) {
        Intent intent = new Intent(AddEditActivity.this, AlertReceiver.class);
        intent.putExtra("com.atif.dab.viewDebtId", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(id), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        if(reminderSwitch.isChecked()) {
            SimpleDateFormat sdf = new SimpleDateFormat(dateFormat +" " +timeFormat, Locale.ENGLISH);
            try {
                myCalendar.setTime(sdf.parse(dateET.getText().toString() +" " +timeET.getText().toString()));
            } catch (ParseException e) {
                e.printStackTrace();
            }

            // Overrides previous alarm
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), pendingIntent);
            Log.i(TAG, "Set alarm on: " +myCalendar.getTime() +" for Debt id: "+id);
        }
        else {
            // Disable/cancel alarm with intent of id
            if(alarmManager != null) {
                alarmManager.cancel(pendingIntent);
                Log.i(TAG, "Cancelled alarm for Debt id: "+id);
            }
        }
    }

}
