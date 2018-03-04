package spevakov.ejournal.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

import static spevakov.ejournal.UserActivity.DB_NAME;
import static spevakov.ejournal.UserActivity.DB_PATH;

public class AddLesson extends AppCompatActivity implements View.OnClickListener {


    Spinner spinnerSubjectType;
    ProgressBar progressBar;
    EditText etDate, etTheme, etDZ;
    Button btnCreate, btnRollcall, btnList;
    String date, theme, dz, groupEng, titleEng, group, title;
    DBHelper dbHelper;
    Cursor cursor;
    String[] students, studentsEng, marks;
    String[] subjectTypes = {"Лекция", "Практическая", "Лабороторная", "Контрольная", "Атестация", "Семестровая"};
    String subjectType;
    boolean created;
    StorageReference riversRef, storageReference;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_lesson);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddLesson.super.finish();
            }
        });


        Intent intent = getIntent();
        title = intent.getExtras().getString("Title");
        group = intent.getExtras().getString("Groups");
        setTitle(group + " || " + title);

        groupEng = intent.getExtras().getString("GroupEng");
        titleEng = intent.getExtras().getString("TitleEng");

        spinnerSubjectType = (Spinner) findViewById(R.id.spinnerSubjectType);
        progressBar = (ProgressBar) findViewById(R.id.progressBarSubjects);
        etDate = (EditText) findViewById(R.id.etDate);
        etTheme = (EditText) findViewById(R.id.etTheme);
        etDZ = (EditText) findViewById(R.id.etDZ);
        btnCreate = (Button) findViewById(R.id.btnCreate);
        btnRollcall = (Button) findViewById(R.id.btnRollcall);
        btnList = (Button) findViewById(R.id.btnList);
        btnCreate.setOnClickListener(this);
        btnRollcall.setOnClickListener(this);
        btnList.setOnClickListener(this);

        @SuppressLint("SimpleDateFormat") DateFormat df = new SimpleDateFormat("dd.MM");
        String date = df.format(Calendar.getInstance().getTime());
        etDate.setText(date);

        dbHelper = new DBHelper(this);
        dbHelper.openDataBase();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, subjectTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSubjectType.setAdapter(adapter);
        spinnerSubjectType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                subjectType = subjectTypes[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnCreate:
                date = etDate.getText().toString();
                theme = etTheme.getText().toString();
                dz = etDZ.getText().toString();
                if (!reformatDate(date) || date.length() != 5)
                    Toast.makeText(this, "Укажите дату в формате 'дд.мм'", Toast.LENGTH_SHORT).show();
                else {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    if (theme.length() == 0) theme = " ";
                    if (dz.length() == 0) dz = " ";
                    btnCreate.setEnabled(false);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    String[] columns = {"SurnameEng", "Surname"};
                    dbHelper.openDataBase();
                    cursor = dbHelper.query(groupEng, columns, null, null, "Surname");
                    students = new String[cursor.getCount()];
                    studentsEng = new String[cursor.getCount()];
                    int i = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            studentsEng[i] = cursor.getString(0);
                            students[i] = cursor.getString(1);
                            i++;
                        } while (cursor.moveToNext());
                    }
                    dbHelper.createLesson(groupEng, titleEng, studentsEng, date, theme, dz, subjectType);
                    updateDB();
                    created = true;
                    btnRollcall.setEnabled(true);
                    btnList.setEnabled(true);
                    etDate.setFocusable(false);
                    etDate.setFocusableInTouchMode(false);
                    etDate.setClickable(false);
                    etDZ.setFocusable(false);
                    etDZ.setFocusableInTouchMode(false);
                    etDZ.setClickable(false);
                    etTheme.setFocusable(false);
                    etTheme.setFocusableInTouchMode(false);
                    etTheme.setClickable(false);
                    spinnerSubjectType.setEnabled(false);
                }
                break;
            case R.id.btnRollcall:
                final Intent intent = new Intent(this, Rollcall.class);
                intent.putExtra("Students", students);
                intent.putExtra("Group", group);
                startActivityForResult(intent, 1);
                break;
            case R.id.btnList:
                String selection = "Date = ? AND Type = ?";
                String[] selectionArgs = {date, subjectType};
                dbHelper.openDataBase();
                Cursor cursor = dbHelper.query(groupEng + "_" + titleEng, null, selection, selectionArgs, null);
                marks = new String[studentsEng.length];
                if (cursor.moveToFirst())
                    for (int i = 0; i < marks.length; i++)
                        marks[i] = cursor.getString(cursor.getColumnIndexOrThrow(studentsEng[i]));

                Intent intent1 = new Intent(getApplicationContext(), ListMarks.class);
                intent1.putExtra("Students", students);
                intent1.putExtra("Marks", marks);
                startActivityForResult(intent1, 2);
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && data != null) {
                    btnRollcall.setEnabled(false);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    String[] rollCallResult = data.getExtras().getStringArray("rollcallResult");
                    dbHelper.setMarks(studentsEng, rollCallResult, groupEng, titleEng, date, subjectType);
                    updateDB();
                }
                break;
            case 2:
                if (resultCode == RESULT_OK && data != null) {
                    btnRollcall.setEnabled(false);
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    String[] marksResult = data.getExtras().getStringArray("marksResult");
                    dbHelper.setMarks(studentsEng, marksResult, groupEng, titleEng, date, subjectType);
                    updateDB();
                }
                break;
        }
    }

    public static boolean reformatDate(String dateString) {
        try {
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(new SimpleDateFormat("dd.MM").parse(dateString));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_subject, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (created) {
            menu.findItem(R.id.addLesson).setEnabled(true);
            menu.findItem(R.id.delLesson).setEnabled(true);
        } else {
            menu.findItem(R.id.addLesson).setEnabled(false);
            menu.findItem(R.id.delLesson).setEnabled(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.addLesson:
                etTheme.setFocusable(true);
                etTheme.setFocusableInTouchMode(true);
                etTheme.setClickable(true);
                etTheme.setText("");

                etDZ.setFocusable(true);
                etDZ.setFocusableInTouchMode(true);
                etDZ.setClickable(true);
                etDZ.setText("");

                etDate.setFocusable(true);
                etDate.setFocusableInTouchMode(true);
                etDate.setClickable(true);

                spinnerSubjectType.setEnabled(true);

                btnCreate.setEnabled(true);
                btnRollcall.setEnabled(false);
                btnList.setEnabled(false);
                created = false;
                break;
            case R.id.delLesson:
                showDialog(1);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Удалить текушую пару?");
            adb.setIcon(R.drawable.ic_delete);
            adb.setPositiveButton("Да", myClickListener);
            adb.setNegativeButton("Отмена", myClickListener);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    progressBar.setVisibility(ProgressBar.VISIBLE);
                    dbHelper.deleteLesson(groupEng, titleEng, date, subjectType);
                    updateDB();
                    etTheme.setFocusable(true);
                    etTheme.setFocusableInTouchMode(true);
                    etTheme.setClickable(true);
                    etTheme.setText("");
                    etDZ.setFocusable(true);
                    etDZ.setFocusableInTouchMode(true);
                    etDZ.setClickable(true);
                    etDZ.setText("");
                    etDate.setFocusable(true);
                    etDate.setFocusableInTouchMode(true);
                    etDate.setClickable(true);
                    spinnerSubjectType.setEnabled(true);
                    btnCreate.setEnabled(true);
                    btnRollcall.setEnabled(false);
                    btnList.setEnabled(false);
                    created = false;
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;

            }
        }
    };

    public void updateDB() {
        storageReference = FirebaseStorage.getInstance().getReference();
        Uri file = Uri.fromFile(new File(DB_PATH + DB_NAME));
        riversRef = storageReference.child(DB_NAME);
        riversRef.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        riversRef.getMetadata().addOnSuccessListener(new OnSuccessListener() {
                            @Override
                            public void onSuccess(Object storageMetadata) {
                                preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                                int yourVersion = Integer.valueOf(preferences.getString("version", ""));
                                yourVersion++;
                                StorageMetadata metadata = new StorageMetadata.Builder().setCustomMetadata("version", String.valueOf(yourVersion)).build();
                                riversRef.updateMetadata(metadata);
                                SharedPreferences.Editor editor = preferences.edit();
                                editor.putString("version", String.valueOf(yourVersion));
                                editor.apply();
                                Toast.makeText(getApplicationContext(), "Сохранение завершено", Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.INVISIBLE);
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        Toast.makeText(getApplicationContext(), "Ошибка! \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                                    }

                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Ошибка! \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }
}
