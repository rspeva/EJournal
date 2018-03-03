package spevakov.ejournal.activity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

import static spevakov.ejournal.UserActivity.login;

public class SubjectActivity extends AppCompatActivity implements View.OnClickListener {
    Spinner spinnerStudentProgress;
    String groupEng, titleEng, group, title;
    String surname, surnameEng;
    ArrayAdapter<String> adapter;
    String [] surnameArr, surnameEngArr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (login)
            setContentView(R.layout.activity_subject);
        else
            setContentView(R.layout.activity_subject_student);

        Button btnClose = (Button) findViewById(R.id.btnClose);
        Button btnAdd = (Button) findViewById(R.id.btnAdd);
        Button btnStudentProgress = (Button) findViewById(R.id.btnStudentProgress);
        Button btnGroupProgress = (Button) findViewById(R.id.btnGroupProgress);
        Button btnEditJournal = (Button) findViewById(R.id.btnEditJournal);
        spinnerStudentProgress = (Spinner) findViewById(R.id.spinnerStudentProgress);
        btnAdd.setOnClickListener(this);
        btnStudentProgress.setOnClickListener(this);
        btnGroupProgress.setOnClickListener(this);
        btnEditJournal.setOnClickListener(this);
        btnClose.setOnClickListener(this);
        Intent intent = getIntent();
        title = intent.getExtras().getString("Title");
        group = intent.getExtras().getString("Groups");
        setTitle(group + " || " + title);

        groupEng = intent.getExtras().getString("GroupEng");
        titleEng = intent.getExtras().getString("TitleEng");

        DBHelper myDbHelper = new DBHelper(this);
        myDbHelper.openDataBase();

        Cursor cursor = myDbHelper.query(groupEng, null, null, null, "Surname");
        surnameArr = new String[cursor.getCount()];
        surnameEngArr = new String[cursor.getCount()];
        int i = 0;
        if (cursor.moveToFirst()) {
            do {
                surnameArr[i] = cursor.getString(0);
                surnameEngArr[i] = cursor.getString(3);
                i++;
            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, surnameArr);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStudentProgress.setAdapter(adapter);
        spinnerStudentProgress.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                surname = surnameArr[position];
                surnameEng = surnameEngArr[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        }

    @Override
    public void onClick(View v) {
        Intent i;
        switch (v.getId()) {
            case R.id.btnAdd:
                i = new Intent(this, AddLesson.class);
                i.putExtra("GroupEng", groupEng);
                i.putExtra("TitleEng", titleEng);
                i.putExtra("Title", title);
                i.putExtra("Groups", group);
                startActivity(i);
                break;
            case R.id.btnStudentProgress:
                i = new Intent(this, StudentProgress.class);
                i.putExtra("GroupEng", groupEng);
                i.putExtra("TitleEng", titleEng);
                i.putExtra("Title", title);
                i.putExtra("SurnameEng", surnameEng);
                i.putExtra("Surname", surname);
                startActivity(i);
                break;
            case R.id.btnGroupProgress:
                i = new Intent(this, GroupProgress.class);
                i.putExtra("Groups", group);
                i.putExtra("GroupEng", groupEng);
                i.putExtra("TitleEng", titleEng);
                i.putExtra("Title", title);
                i.putExtra("SurnameEngArr", surnameEngArr);
                i.putExtra("SurnameArr", surnameArr);
                startActivity(i);
                break;
            case R.id.btnEditJournal:
                i = new Intent(this, EditJournal.class);
                i.putExtra("Groups", group);
                i.putExtra("GroupEng", groupEng);
                i.putExtra("TitleEng", titleEng);
                i.putExtra("Title", title);
                i.putExtra("SurnameEngArr", surnameEngArr);
                i.putExtra("SurnameArr", surnameArr);
                startActivity(i);
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }
}
