package com.atif.dab;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.Context.ALARM_SERVICE;
import static android.content.Context.MODE_PRIVATE;

/**
 * Created by Atif Mustaffa on 17/5/2018.
 */
public class AlertReceiver extends BroadcastReceiver {

    DebtDatabaseHandler debtDatabaseHandler;
    Context context;
    Debt debt;
    String TAG = "DAB_APP_MSG";

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;
        debtDatabaseHandler = new DebtDatabaseHandler(context);
        String id = intent.getExtras().getString("com.atif.dab.viewDebtId");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Intent repeatingIntent = new Intent(context, ViewDebtActivity.class);
        repeatingIntent.putExtra("com.atif.dab.viewDebtId", id);
        repeatingIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        // Setup notification & message
        debt = debtDatabaseHandler.getDebt(id);
        String currency = this.context.getSharedPreferences("com.atif.dab",MODE_PRIVATE).getString("currency",null);
        String msg = debt.getDebtorName();
        if(debt.getDebtorName().equals("You")) msg += " owe ";
        else msg += " owes ";
        msg += debt.getCreditorName() +" " +currency +" " +debt.getAmount() +" for " +debt.getDebtTitle();

        PendingIntent pendingIntent = PendingIntent.getActivity(context, Integer.parseInt(id), repeatingIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "channel1")
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(this.context.getResources(), R.mipmap.ic_launcher))
                .setContentTitle(this.context.getText(R.string.app_name) +" | " +this.context.getText(R.string.app_desc_name))
                .setContentText(msg)
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setAutoCancel(true);
        notificationManager.notify(Integer.parseInt(id), builder.build());

        if(debt.isReminderActive() && !debt.getReminderType().equals("Once"))
            setAlarm(id);
        else if(debt.isReminderActive() && debt.getReminderType().equals("Once"))
            debtDatabaseHandler.setReminder(id, false);

    }

    private void setAlarm(String id) {
        String dateFormat = "EE, MMM dd yyyy";
        String timeFormat = "hh:mm a";
        Calendar myCalendar = Calendar.getInstance();

        Intent intent = new Intent(this.context, AlertReceiver.class);
        intent.putExtra("com.atif.dab.viewDebtId", id);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.context, Integer.parseInt(id), intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarmManager = (AlarmManager) this.context.getSystemService(ALARM_SERVICE);

        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat +" " +timeFormat, Locale.ENGLISH);
        SimpleDateFormat sdf2 = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        try {
            String newDate = sdf2.format(new Date());
            myCalendar.setTime(sdf.parse(newDate +" " +debt.getReminderTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if(debt.getReminderType().equals("Daily"))
            myCalendar.add(Calendar.DATE,1);
        else if(debt.getReminderType().equals("Weekly"))
            myCalendar.add(Calendar.DATE,7);
        else if(debt.getReminderType().equals("Monthly"))
            myCalendar.add(Calendar.MONTH, 1);

        alarmManager.setExact(AlarmManager.RTC_WAKEUP, myCalendar.getTimeInMillis(), pendingIntent);
        Log.i(TAG, "Set next alarm on: " +myCalendar.getTime() +" for Debt id: "+id);

    }
}
