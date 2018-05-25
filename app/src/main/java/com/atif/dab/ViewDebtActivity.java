package com.atif.dab;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewDebtActivity extends AppCompatActivity {

    DebtDatabaseHandler debtDatabaseHandler;
    TextView debtorNameTV, amountTV, debtTitleTV, creditorNameTV, ongoingStatusTV, reminderDetailsTV, reminderStatusTV, creationDateTV;
    Button settledBtn, editBtn, deleteBtn;
    ImageView ongoingImg, reminderImg;
    Debt d;
    String viewId;
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_debt);
        setTitle(R.string.view_debt_lbl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        viewId = getIntent().getExtras().getString("com.atif.dab.viewDebtId");
        Log.i(TAG, "View Debt id: " +viewId);

        debtorNameTV = findViewById(R.id.debtorNameTV);
        amountTV = findViewById(R.id.amountTV);
        debtTitleTV = findViewById(R.id.debtTitleTV);
        creditorNameTV = findViewById(R.id.creditorNameTV);
        ongoingStatusTV = findViewById(R.id.ongoingStatusTV);
        reminderDetailsTV = findViewById(R.id.reminderDetailsTV);
        reminderStatusTV = findViewById(R.id.reminderStatusTV);
        creationDateTV = findViewById(R.id.creationDateTV);
        ongoingImg = findViewById(R.id.ongoingImg);
        reminderImg = findViewById(R.id.reminderImg);
        settledBtn = findViewById(R.id.settledBtn);
        editBtn = findViewById(R.id.editBtn);
        deleteBtn = findViewById(R.id.deleteBtn);;

        debtDatabaseHandler = new DebtDatabaseHandler(ViewDebtActivity.this);
        setDebtDetails();

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), AddEditActivity.class);
                i.putExtra("com.atif.dab.addEdit", "edit");
                i.putExtra("com.atif.dab.editItemId", viewId);
                startActivity(i);
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDeleteDialogBox();
            }
        });
        settledBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDialogBox();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDebtDetails();
    }

    private void setDebtDetails() {
        d = debtDatabaseHandler.getDebt(viewId);

        debtorNameTV.setText(d.getDebtorName());
        amountTV.setText(getSharedPreferences("com.atif.dab",MODE_PRIVATE).getString("currency",null) + " " + String.format ("%.2f", d.getAmount()));
        debtTitleTV.setText(d.getDebtTitle());
        creditorNameTV.setText(getText(R.string.creditor_lbl) + ": " +d.getCreditorName());
        if(!d.isOngoing()) {
            // Debt settled
            ongoingStatusTV.setText(getText(R.string.settled_on_lbl) + "\n" + d.getSettleDate());
            ongoingImg.setImageResource(R.drawable.ic_settled);
            settledBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
        }
        String s = "";
        if(d.getReminderType() == null) {
            s = "Not set";
        }
        else {
            if(!d.isReminderActive()) {
                reminderStatusTV.setText(R.string.reminder_off_lbl);
                s = "" + getText(R.string.repeat_lbl) + ": " + d.getReminderType() + "\n" + d.getReminderDate() + ", " + d.getReminderTime();
                reminderImg.setImageResource(R.drawable.ic_reminder_off);
                reminderImg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.disabledGray), PorterDuff.Mode.SRC_IN);
            }
            else if(d.isReminderActive()) {
                reminderStatusTV.setText(R.string.reminder_on_lbl);
                s = "" + getText(R.string.repeat_lbl) + ": " + d.getReminderType() + "\n" + d.getReminderDate() + ", " + d.getReminderTime();
                reminderImg.setImageResource(R.drawable.ic_reminder_on);
                reminderImg.setColorFilter(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
            }
        }
        reminderDetailsTV.setText(s);
        creationDateTV.setText(getText(R.string.created_on_lbl) +" " + d.getCreationDate());
    }

    private void showDeleteDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(ViewDebtActivity.this).create();
        alertDialog.setTitle(getText(R.string.delete_debt_lbl));
        alertDialog.setMessage(getText(R.string.del_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.delete_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Database operation
                        debtDatabaseHandler.deleteDebt(viewId);
                        Log.i(TAG, "Successfully delete data with id: " +viewId);
                        Toast.makeText(getApplicationContext(), getText(R.string.deleted_lbl), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        finish();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.cancel_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void showConfirmDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(ViewDebtActivity.this).create();
        alertDialog.setTitle(getText(R.string.confirm_lbl));
        alertDialog.setMessage(getText(R.string.confirm_msg));
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, getText(R.string.set_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Database operation
                        debtDatabaseHandler.setDebtAsSettled(viewId);
                        debtDatabaseHandler.setReminder(viewId, false);
                        // cancel alarm if it is set
                        cancelAlarm(viewId);
                        setDebtDetails();
                        Log.i(TAG, "Successfully update data (set settled) with id: " +viewId);
                        Toast.makeText(getApplicationContext(), getText(R.string.debt_updated_lbl), Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                });
        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, getText(R.string.cancel_lbl).toString().toUpperCase(),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    private void cancelAlarm(String id) {
        Intent intent = new Intent(ViewDebtActivity.this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), Integer.parseInt(id), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        if(alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.i(TAG, "Cancelled alarm for Debt id: " + id);
        }
    }

}
