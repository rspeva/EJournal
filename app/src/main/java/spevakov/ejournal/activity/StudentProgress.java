package spevakov.ejournal.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.backendless.exceptions.BackendlessFault;
import com.backendless.persistence.DataQueryBuilder;

import java.util.List;
import java.util.Map;

import spevakov.ejournal.R;

public class StudentProgress extends AppCompatActivity implements View.OnClickListener {

    ProgressBar progressBar;
    Button tvSumMarks, tvAvrMark, tvMissSum, tvVisit, tvActivity, tvSumLessons, btnViewMarks;
    TextView tvConnecting;
    String groupEng, titleEng, surnameEng;
    String[] marks, theme, dates, types;

    int sumMarks, missSum;
    float sum;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                StudentProgress.super.finish();
            }
        });

        Backendless.setUrl("https://api.backendless.com");
        Backendless.initApp(getApplicationContext(), "7D931783-DF97-9759-FFB4-FFDEF533A900", "3B299654-9E3E-1B25-FF42-7BBA5C135500");

        Intent intent = getIntent();
        setTitle(intent.getExtras().getString("Surname") + " || " + intent.getExtras().getString("Title"));
        groupEng = intent.getExtras().getString("GroupEng");
        titleEng = intent.getExtras().getString("TitleEng");
        surnameEng = intent.getExtras().getString("SurnameEng");

        btnViewMarks = (Button) findViewById(R.id.btnViewMarks);
        btnViewMarks.setOnClickListener(this);
        btnViewMarks.setEnabled(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSubjects);
        tvConnecting = (TextView) findViewById(R.id.tvConnecting);
        tvSumMarks = (Button) findViewById(R.id.tvSumMarks);
        tvAvrMark = (Button) findViewById(R.id.tvAvrMark);
        tvMissSum = (Button) findViewById(R.id.tvMissSum);
        tvVisit = (Button) findViewById(R.id.tvVisit);
        tvActivity = (Button) findViewById(R.id.tvActivity);
        tvSumLessons = (Button) findViewById(R.id.tvSumLessons);

        sum = 0;
        missSum = 0;
        sumMarks = 0;


        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("Dates");
        queryBuilder.setPageSize(100);
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvConnecting.setVisibility(TextView.VISIBLE);
        Backendless.Data.of(groupEng + "_" + titleEng).find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> lessonsCollection) {
                marks = new String[lessonsCollection.size()];
                dates = new String[lessonsCollection.size()];
                theme = new String[lessonsCollection.size()];
                types = new String[lessonsCollection.size()];
                if (lessonsCollection.size() != 0) {
                    for (int i = 0; i < lessonsCollection.size(); i++) {
                        try {
                            marks[i] = lessonsCollection.get(i).get(surnameEng).toString();
                            dates[i] = lessonsCollection.get(i).get("Dates").toString();
                            theme[i] = lessonsCollection.get(i).get("Theme").toString();
                            types[i] = lessonsCollection.get(i).get("Type").toString();
                        } catch (NullPointerException e) {
                            btnViewMarks.setEnabled(false);
                        }
                    }
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                tvConnecting.setVisibility(TextView.INVISIBLE);
                try {
                    for (String mark : marks) {
                        if (mark.equals("H") || mark.equals("Н")) missSum++;
                        else if (!mark.equals("0")) {
                            sumMarks++;
                            sum += Float.valueOf(mark);
                        }
                    }
                } catch (NullPointerException e)
                {

                }
                float avr = sum / sumMarks;
                String s = String.format("%.2f", avr);
                if (sumMarks == 0)
                    tvAvrMark.setText("0");
                else
                    tvAvrMark.setText(s);

                tvSumMarks.setText(String.valueOf(sumMarks));
                tvMissSum.setText(String.valueOf(missSum));
                float vis = (float) missSum / marks.length * 100;
                String visit = String.valueOf(Math.round(100 - vis)) + "%";
                tvVisit.setText(visit);
                tvSumLessons.setText(String.valueOf(marks.length));
                float act = (float) sumMarks / marks.length * 100;
                String activ = String.valueOf(Math.round(act)) + "%";
                tvActivity.setText(activ);
                btnViewMarks.setEnabled(true);

                tvSumLessons.setBackgroundColor(getResources().getColor(R.color.colorNeutral));
                tvSumMarks.setBackgroundColor(getResources().getColor(R.color.colorNeutral));
                tvMissSum.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

                if (avr > 4.2) tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (avr > 3.4) tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorMedium));
                else tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorBad));

                if (Math.round(100 - vis) > 60 ) tvVisit.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (Math.round(100 - vis) > 30) tvVisit.setBackgroundColor(getResources().getColor(R.color.colorMedium));
                else tvVisit.setBackgroundColor(getResources().getColor(R.color.colorBad));

                if (Math.round(act) > 49 ) tvActivity.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (Math.round(act) > 24) tvActivity.setBackgroundColor(getResources().getColor(R.color.colorMedium));
                else tvActivity.setBackgroundColor(getResources().getColor(R.color.colorBad));

            }

            @Override
            public void handleFault(BackendlessFault fault) {
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                tvConnecting.setVisibility(TextView.INVISIBLE);
                if (fault.getCode().equals("1009")) showDialog(1);
                else
                    Toast.makeText(getApplicationContext(), "Нет подключения к БД: " + fault.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(), SPMarks.class);
        intent.putExtra("Type", types);
        intent.putExtra("Theme", theme);
        intent.putExtra("Date", dates);
        intent.putExtra("Marks", marks);
        startActivity(intent);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Нет проведенных пар!");
            adb.setIcon(R.drawable.ic_layers_clear_black_24dp);
            adb.setPositiveButton("Закрыть", myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    finish();
                    break;

            }
        }
    };
}
