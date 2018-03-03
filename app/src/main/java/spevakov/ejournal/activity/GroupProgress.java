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

public class GroupProgress extends AppCompatActivity implements View.OnClickListener {


    Button tvSumMarks, tvMissSum, tvAvrMark, tvVisit, tvActivity, tvSumLessons, btnViewMarks;

    ProgressBar progressBar;
    TextView tvConnecting;
    String groupEng, titleEng, group, title;
    String[]  theme, dates, surnameEngArr, surnameArr, types, markList, dz;

    int sumMarks, missSum,sum, lessons;
    float allMarks;


    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_progress);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GroupProgress.super.finish();
            }
        });

        Backendless.setUrl("https://api.backendless.com");
        Backendless.initApp(getApplicationContext(), "7D931783-DF97-9759-FFB4-FFDEF533A900", "3B299654-9E3E-1B25-FF42-7BBA5C135500");

        Intent intent = getIntent();
        title = intent.getExtras().getString("Title");
        group = intent.getExtras().getString("Groups");
        groupEng = intent.getExtras().getString("GroupEng");
        titleEng = intent.getExtras().getString("TitleEng");
        surnameEngArr = intent.getExtras().getStringArray("SurnameEngArr");
        surnameArr = intent.getExtras().getStringArray("SurnameArr");
        setTitle(group + " || " + title);

        btnViewMarks = (Button) findViewById(R.id.btnViewMarksGroup);
        btnViewMarks.setOnClickListener(this);
        btnViewMarks.setEnabled(false);

        progressBar = (ProgressBar) findViewById(R.id.progressBarSubjects);
        tvConnecting = (TextView) findViewById(R.id.tvConnecting);

        tvSumLessons = (Button) findViewById(R.id.tvSumLessonsGr);
        tvAvrMark = (Button) findViewById(R.id.tvAvrMarkGr);
        tvMissSum = (Button) findViewById(R.id.tvMissSumGr);
        tvVisit = (Button) findViewById(R.id.tvVisitGr);
        tvActivity = (Button) findViewById(R.id.tvActivityGr);
        tvSumLessons = (Button) findViewById(R.id.tvSumLessonsGr);
        tvSumMarks = (Button) findViewById(R.id.tvSumMarksGr) ;

        sum = 0;
        missSum = 0;
        sumMarks = 0;
        allMarks = 0;

        DataQueryBuilder queryBuilder = DataQueryBuilder.create();
        queryBuilder.setSortBy("Dates");
        progressBar.setVisibility(ProgressBar.VISIBLE);
        tvConnecting.setVisibility(TextView.VISIBLE);
        queryBuilder.setPageSize(100);
        Backendless.Data.of(groupEng + "_" + titleEng).find(queryBuilder, new AsyncCallback<List<Map>>() {
            @Override
            public void handleResponse(List<Map> lessonsCollection) {
                dates = new String[lessonsCollection.size()];
                theme = new String[lessonsCollection.size()];
                types = new String[lessonsCollection.size()];
                dz = new String[lessonsCollection.size()];
                markList = new String[lessonsCollection.size()*surnameArr.length];
                String mark;
                int k = 0;
                if (lessonsCollection.size() != 0) {

                    for (int i = 0; i < lessonsCollection.size(); i++) {
                        for (int j = 0; j < surnameEngArr.length; j++) {
                            mark = lessonsCollection.get(i).get(surnameEngArr[j]).toString();
                                markList[k] = mark; k++;
                            if (mark.equals("H") || mark.equals("Н")) missSum++;
                            else if (!mark.equals("0")) {
                                sumMarks++;
                                allMarks += Float.valueOf(mark);
                            }

                        }

                        dates[i] = lessonsCollection.get(i).get("Dates").toString();
                        theme[i] = lessonsCollection.get(i).get("Theme").toString();
                        types[i] = lessonsCollection.get(i).get("Type").toString();
                        dz[i] = lessonsCollection.get(i).get("DZ").toString();
                    }
                }
                progressBar.setVisibility(ProgressBar.INVISIBLE);
                tvConnecting.setVisibility(TextView.INVISIBLE);


                tvSumLessons.setText(String.valueOf(dates.length));

                sum = dates.length*surnameEngArr.length;
                float avr = allMarks / sumMarks;
                String s = String.format("%.2f", avr);
                if (sumMarks == 0)
                    tvAvrMark.setText("0");
                else
                    tvAvrMark.setText(s);

                float vis = (float) missSum / sum * 100;
                String visit = String.valueOf(Math.round(100 - vis)) + "%";
                tvVisit.setText(visit);

                float act = (float) sumMarks / sum * 100;
                String activ = String.valueOf(Math.round(act)) + "%";
                tvActivity.setText(activ);
                btnViewMarks.setEnabled(true);

                tvSumMarks.setText(String.valueOf(sumMarks));
                tvMissSum.setText(String.valueOf(missSum));

                if (avr > 4.2) tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (avr > 3.4) tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorMedium));
                else tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorBad));

                if (Math.round(100 - vis) > 60 ) tvVisit.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (Math.round(100 - vis) > 30) tvVisit.setBackgroundColor(getResources().getColor(R.color.colorMedium));
                else tvVisit.setBackgroundColor(getResources().getColor(R.color.colorBad));

                if (Math.round(act) > 39 ) tvActivity.setBackgroundColor(getResources().getColor(R.color.colorGood));
                else if (Math.round(act) > 19) tvActivity.setBackgroundColor(getResources().getColor(R.color.colorMedium));
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
        Intent intent = new Intent(getApplicationContext(), ListMarksGroup.class);
        intent.putExtra("Theme", theme);
        intent.putExtra("Date", dates);
        intent.putExtra("Type", types);
        intent.putExtra("Surname", surnameArr);
        intent.putExtra("MarkList", markList);
        intent.putExtra("Group", group);
        intent.putExtra("Title", title);
        intent.putExtra("DZ", dz);
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