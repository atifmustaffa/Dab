package com.atif.dab;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FeedbackActivity extends AppCompatActivity {

    EditText nameET, emailET, messageET;
    RadioGroup radioGroup;
    RadioButton checkedBtn;
    Button sendBtn;
    CheckBox showOnWebCB;
    String TAG = "DAB_APP_MSG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        setTitle(R.string.send_feedback_lbl);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        nameET = (EditText) findViewById(R.id.nameET);
        emailET = (EditText) findViewById(R.id.emailET);
        messageET = (EditText) findViewById(R.id.messageET);
        radioGroup = (RadioGroup) findViewById(R.id.cateoryRadioGroup);
        sendBtn = (Button) findViewById(R.id.sendBtn);
        showOnWebCB = (CheckBox) findViewById(R.id.showOnWebCB);

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkedBtn = (RadioButton) findViewById(radioGroup.getCheckedRadioButtonId());
                String show = "no";
                if(showOnWebCB.isChecked())
                    show = "yes";

                // start new thread (webservice:send)
                if(isValid()) {
                    new PostDataTask(nameET.getText().toString(),
                            emailET.getText().toString(),
                            checkedBtn.getText().toString(),
                            messageET.getText().toString(),
                            show).execute();

                    Toast.makeText(getApplicationContext(), "Feedback sent", Toast.LENGTH_SHORT).show();
                    onBackPressed();
                }
                else showEmptyFillDialogBox();
            }
        });

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private boolean isValid() {
        // Check for empty fill
        if(nameET.getText().toString().trim().equals("") ||
                emailET.getText().toString().trim().equals("") ||
                messageET.getText().toString().trim().equals("")) {
            return false;
        }
        return true;
    }

    private void showEmptyFillDialogBox() {
        AlertDialog alertDialog = new AlertDialog.Builder(FeedbackActivity.this).create();
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

    private class PostDataTask extends AsyncTask<String, Void, String> {

        String name, email, category, message, show;

        public PostDataTask(String name, String email, String category, String message, String show){
            this.name = name;
            this.email = email;
            this.category = category;
            this.message = message;
            this.show = show;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(getText(R.string.website_url).toString() +"/php/send_feedback.php");
                JSONObject params = new JSONObject();
                params.put("name", name);
                params.put("email", email);
                params.put("category", category);
                params.put("message", message);
                params.put("showonweb", show);

                byte[] postDataBytes = params.toString().getBytes("UTF-8");
                HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                conn.setDoOutput(true);
                conn.getOutputStream().write(postDataBytes);

                Reader in = null;
                in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));

                // Read response
                StringBuilder sb = new StringBuilder();
                for (int c; (c = in.read()) >= 0;)
                    sb.append((char)c);
                String response = sb.toString();
                Log.i(TAG, response);
                if(response.equals("Success")){
                    Log.i(TAG, "Sent: "+params.toString());
                }
                return "Success";

            } catch (IOException|JSONException e) {
                e.printStackTrace();
                return "Unable to send data to web.";
            }
        }
    }
}
