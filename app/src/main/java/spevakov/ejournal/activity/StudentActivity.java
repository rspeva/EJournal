package spevakov.ejournal.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessException;
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

public class StudentActivity extends AppCompatActivity implements View.OnClickListener {

    String surname, name, lastname, surnameEng, status, groupEng, group;
    ProgressBar progressBar;
    TextView tvConnecting;
    String[] titleEng, avarage, title, teacher;
    ArrayList<String> allMarks;
    int sumMarks, missSum, x;
    float sum, vis, act, avr;
    Button tvAvrMark, tvMissSum, tvVisit, tvActivity, btnViewMarks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentActivity.super.finish();
            }
        });

        Intent intent = getIntent();
        surname = intent.getExtras().getString("Surname");
        name = intent.getExtras().getString("Name");
        lastname = intent.getExtras().getString("Lastname");
        surnameEng = intent.getExtras().getString("SurnameEng");
        status = intent.getExtras().getString("Status");
        groupEng = intent.getExtras().getString("GroupEng");
        group = intent.getExtras().getString("Group");

        setTitle(group + " | " + surname);

        TextView tvNameStudent = (TextView) findViewById(R.id.tvNameStudent);
        TextView tvStatusStudent = (TextView) findViewById(R.id.tvStatusStudent);

        tvActivity = (Button) findViewById(R.id.tvActivityStudent);
        tvAvrMark = (Button) findViewById(R.id.tvAvrMarkStudents);
        tvMissSum = (Button) findViewById(R.id.tvMissSumStudent);
        tvVisit = (Button) findViewById(R.id.tvVisitStudent);
        btnViewMarks = (Button) findViewById(R.id.btnViewMarksStudent);
        btnViewMarks.setOnClickListener(this);
        String fio = surname + " " + name + " " + lastname;
        tvNameStudent.setText(fio);

        tvStatusStudent.setText(status);
        progressBar = (ProgressBar) findViewById(R.id.progressBarSubjects);
        tvConnecting = (TextView) findViewById(R.id.tvConnecting);

        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper.openDataBase();

        String selection = "GroupEng = ?";
        String selectionArgs[] = {groupEng};
        Cursor cursor = myDbHelper.query("SUBJECT", null, selection, selectionArgs, null);
        if (cursor.getCount() == 0) showDialog(1);
        else {
            titleEng = new String[cursor.getCount()];
            title = new String[cursor.getCount()];
            teacher = new String[cursor.getCount()];
            avarage = new String[cursor.getCount()];
            int n = 0;
            if (cursor.moveToFirst()) {
                do {
                    titleEng[n] = cursor.getString(4);
                    title[n] = cursor.getString(0);
                    teacher[n] = cursor.getString(1);
                    n++;
                } while (cursor.moveToNext());
            }

            progressBar.setVisibility(ProgressBar.VISIBLE);
            tvConnecting.setVisibility(TextView.VISIBLE);

            allMarks = new ArrayList<String>();
            x = 0;


            final Handler myHandler = new Handler();
            new Thread(new Runnable() {
                public void run() {
                    while (x < titleEng.length) {
                        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
                        queryBuilder.setSortBy("Dates");
                        queryBuilder.setPageSize(100);
                        try {
                            List<Map> result = Backendless.Data.of(groupEng + "_" + titleEng[x]).find(queryBuilder);

                            sum = 0;
                            sumMarks = 0;
                            for (int i = 0; i < result.size(); i++) {
                                allMarks.add(result.get(i).get(surnameEng).toString());
                                if (!result.get(i).get(surnameEng).toString().equals("H") &&
                                        !result.get(i).get(surnameEng).toString().equals("Н") &&
                                        !result.get(i).get(surnameEng).toString().equals("0")) {
                                    sumMarks++;
                                    sum += Float.valueOf(result.get(i).get(surnameEng).toString());
                                }
                            }

                            if (sumMarks != 0)
                                avarage[x] = String.valueOf(sum / sumMarks);
                            else
                                avarage[x] = "0";
                            x++;
                            if (x == titleEng.length) {
                                myHandler.post(new Runnable() {  // используя Handler, привязанный к UI-Thread
                                    @Override
                                    public void run() {
                                        fillField();
                                    }
                                });

                            }
                        } catch (BackendlessException e) {
                            avarage[x] = "0";
                            x++;
                            if (x >= titleEng.length)
                                myHandler.post(new Runnable() {  // используя Handler, привязанный к UI-Thread
                                    @Override
                                    public void run() {
                                        fillField();
                                    }
                                });
                        }
                    }
                }

            }).start();
        }
    }

    public void fillField() {
        sum = 0;
        missSum = 0;
        sumMarks = 0;
        vis = 0;
        act = 0;
        avr = 0;
        for (int i = 0; i < allMarks.size(); i++) {
            if (allMarks.get(i).equals("H") || allMarks.get(i).equals("Н")) missSum++;
            else if (!allMarks.get(i).equals("0")) {
                sumMarks++;
                sum += Float.valueOf(allMarks.get(i));
            }
        }

        avr = sum / sumMarks;
        String s = String.format("%.2f", avr);
        if (sumMarks == 0)
            tvAvrMark.setText("0");
        else
            tvAvrMark.setText(s);

        tvMissSum.setText(String.valueOf(missSum));
        vis = (float) missSum / allMarks.size() * 100;
        String visit = String.valueOf(Math.round(100 - vis)) + "%";
        tvVisit.setText(visit);
        act = (float) sumMarks / allMarks.size() * 100;
        String activity = String.valueOf(Math.round(act)) + "%";
        tvActivity.setText(activity);

        tvMissSum.setBackgroundColor(getResources().getColor(R.color.colorNeutral));
        if (avr > 4.2) tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (avr > 3.4)
            tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorBad));

        if (Math.round(100 - vis) > 60)
            tvVisit.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (Math.round(100 - vis) > 30)
            tvVisit.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvVisit.setBackgroundColor(getResources().getColor(R.color.colorBad));

        if (Math.round(act) > 49)
            tvActivity.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (Math.round(act) > 24)
            tvActivity.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvActivity.setBackgroundColor(getResources().getColor(R.color.colorBad));

        progressBar.setVisibility(ProgressBar.INVISIBLE);
        tvConnecting.setVisibility(TextView.INVISIBLE);
        btnViewMarks.setEnabled(true);
    }


    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()) {
            case R.id.btnViewMarksStudent:
                intent = new Intent(getApplicationContext(), StudentListMark.class);
                intent.putExtra("Title", title);
                intent.putExtra("SurnameEng", surnameEng);
                intent.putExtra("TitleEng", titleEng);
                intent.putExtra("GroupEng", groupEng);
                intent.putExtra("Teacher", teacher);
                intent.putExtra("Avr", avarage);
                intent.putExtra("Surname", surname);
                startActivity(intent);
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Нет предметов!");
            adb.setIcon(R.drawable.ic_layers_clear_black_24dp);
            adb.setPositiveButton("Ок", myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    break;

            }
        }
    };
}
