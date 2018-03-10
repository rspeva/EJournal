package spevakov.ejournal.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

public class GroupProgress extends AppCompatActivity implements View.OnClickListener {


    Button tvSumMarks, tvMissSum, tvAvrMark, tvVisit, tvActivity, tvSumLessons, btnViewMarks;
    ProgressBar progressBar;
    TextView tvConnecting;
    String groupEng, titleEng, group, title;
    String[] theme, dates, surnameEngArr, surnameArr, types, markList, dz;
    DBHelper dbHelper;
    int sumMarks, missSum, sum;
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
        tvSumMarks = (Button) findViewById(R.id.tvSumMarksGr);

        sum = 0;
        missSum = 0;
        sumMarks = 0;
        allMarks = 0;

        dbHelper = new DBHelper(this);
        dbHelper.openDataBase();
        progressBar.setVisibility(ProgressBar.VISIBLE);
        if (dbHelper.tableExists(groupEng, titleEng)) {
            Cursor cursor = dbHelper.query(groupEng + "_" + titleEng, null, null, null, "Timestamp");
            if (cursor.moveToFirst()) {
                dates = new String[cursor.getCount()];
                theme = new String[cursor.getCount()];
                types = new String[cursor.getCount()];
                dz = new String[cursor.getCount()];
                markList = new String[cursor.getCount() * surnameArr.length];
                int i = 0, k = 0;
                do {
                    dates[i] = cursor.getString(0);
                    theme[i] = cursor.getString(1);
                    dz[i] = cursor.getString(2);
                    types[i] = cursor.getString(3);
                    for (int j = 0; j < surnameEngArr.length; j++) {
                        markList[k] = cursor.getString(j + 4);
                        if (markList[k].equals("H") || markList[k].equals("Н")) missSum++;
                        else if (!markList[k].equals("0")) {
                            sumMarks++;
                            allMarks += Float.valueOf(markList[k]);
                        }
                        k++;
                    }
                    i++;
                } while (cursor.moveToNext());
            } else showDialog(1);
        } else showDialog(1);

        progressBar.setVisibility(ProgressBar.INVISIBLE);

        tvSumLessons.setText(String.valueOf(dates.length));
        sum = dates.length * surnameEngArr.length;
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
        String activity = String.valueOf(Math.round(act)) + "%";
        tvActivity.setText(activity);
        btnViewMarks.setEnabled(true);

        tvSumMarks.setText(String.valueOf(sumMarks));
        tvMissSum.setText(String.valueOf(missSum));

        if (avr > 4.2)
            tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (avr > 3.4)
            tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvAvrMark.setBackgroundColor(getResources().getColor(R.color.colorBad));

        if (Math.round(100 - vis) > 60)
            tvVisit.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (Math.round(100 - vis) > 30)
            tvVisit.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvVisit.setBackgroundColor(getResources().getColor(R.color.colorBad));

        if (Math.round(act) > 39)
            tvActivity.setBackgroundColor(getResources().getColor(R.color.colorGood));
        else if (Math.round(act) > 19)
            tvActivity.setBackgroundColor(getResources().getColor(R.color.colorMedium));
        else tvActivity.setBackgroundColor(getResources().getColor(R.color.colorBad));
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