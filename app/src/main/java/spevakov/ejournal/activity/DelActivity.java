package spevakov.ejournal.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

import static spevakov.ejournal.MainActivity.progressSave;
import static spevakov.ejournal.UserActivity.DB_NAME;
import static spevakov.ejournal.UserActivity.DB_PATH;

public class DelActivity extends Activity implements View.OnClickListener {
    Context context = this;
    StorageReference riversRef, storageReference;
    SharedPreferences preferences;
    Spinner spinnerCourse, spinnerGroup, spinnerDel;
    ImageButton btnDel;
    Button btnDelGroup, btnDelStudent, btnDelSubject, btnClose;
    ArrayAdapter<String> adapter, adapterGroup, adapterDel;
    ArrayList<String> group1 = new ArrayList<>();
    ArrayList<String> group2 = new ArrayList<>();
    ArrayList<String> group3 = new ArrayList<>();
    ArrayList<String> group4 = new ArrayList<>();
    ArrayList<String> groups = new ArrayList<>();
    String groupEng, title, surname, group, delete, titleEng;
    DBHelper dbHelper;
    int c1 = 0, c2 = 0, c3 = 0, course;
    String[] courses = {"1", "2", "3", "4"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_del);

        btnClose = (Button) findViewById(R.id.btnClose);
        btnDel = (ImageButton) findViewById(R.id.btnDel);
        btnDelSubject = (Button) findViewById(R.id.btnDelSubject);
        btnDelStudent = (Button) findViewById(R.id.btnDelStudent);
        btnDelGroup = (Button) findViewById(R.id.btnDelGroup);

        btnClose.setOnClickListener(this);
        btnDel.setOnClickListener(this);
        btnDelSubject.setOnClickListener(this);
        btnDelStudent.setOnClickListener(this);
        btnDelGroup.setOnClickListener(this);

        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);
        spinnerDel = (Spinner) findViewById(R.id.spinnerDel);

        dbHelper = new DBHelper(this);
        dbHelper.openDataBase();

        Cursor cursor = dbHelper.query("GROUPS", null, null, null, "Course");
        if (cursor.moveToFirst()) {
            do {
                switch (cursor.getString(0)) {
                    case "1":
                        group1.add(cursor.getString(1));
                        c1++;
                        break;
                    case "2":
                        group2.add(cursor.getString(1));
                        c2++;
                        break;
                    case "3":
                        group3.add(cursor.getString(1));
                        c3++;
                        break;
                    case "4":
                        group4.add(cursor.getString(1));
                        break;
                }
                groups.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group1);
                        course = 1;
                        break;
                    case 1:
                        adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group2);
                        course = 2;
                        break;
                    case 2:
                        adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group3);
                        course = 3;
                        break;
                    case 3:
                        adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group4);
                        course = 4;
                        break;
                }
                spinnerGroup.setAdapter(adapterGroup);
                adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                spinnerDel.setVisibility(View.INVISIBLE);
                btnDel.setVisibility(View.INVISIBLE);
                switch (course) {
                    case 1:
                        groupEng = groups.get(position);
                        group = group1.get(position);
                        break;
                    case 2:
                        groupEng = groups.get(position + c1);
                        group = group2.get(position);
                        break;
                    case 3:
                        groupEng = groups.get(position + c1 + c2);
                        group = group3.get(position);
                        break;
                    case 4:
                        groupEng = groups.get(position + c1 + c2 + c3);
                        group = group4.get(position);
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnDelGroup:
                if (spinnerGroup.getAdapter().getCount() > 0)
                    showDialog(3);
                else
                    Toast.makeText(context, "Для удаления выберите группу", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDelStudent:
                if (spinnerGroup.getAdapter().getCount() > 0) {
                    delete = "student";
                    Cursor cursor = dbHelper.query(groupEng, null, null, null, "Surname");
                    final String[] students = new String[cursor.getCount()];
                    int i = 0;
                    if (cursor.moveToFirst()) {
                        do {
                            students[i] = cursor.getString(0);
                            i++;
                        } while (cursor.moveToNext());
                    }

                    adapterDel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, students);
                    adapterDel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDel.setAdapter(adapterDel);
                    spinnerDel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            surname = students[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    spinnerDel.setVisibility(View.VISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(context, "Для удаления выберите группу", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDelSubject:
                if (spinnerGroup.getAdapter().getCount() > 0) {
                    delete = "subject";
                    String selection = "GroupEng = ?";
                    String[] selectionArgs = {groupEng};
                    Cursor cursor1 = dbHelper.query("SUBJECT", null, selection, selectionArgs, "Title");
                    final String[] subjects = new String[cursor1.getCount()];
                    final String[] subjectsEng = new String[cursor1.getCount()];
                    int j = 0;
                    if (cursor1.moveToFirst()) {
                        do {
                            subjects[j] = cursor1.getString(0);
                            subjectsEng[j] = cursor1.getString(4);
                            j++;
                        } while (cursor1.moveToNext());
                    }

                    adapterDel = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, subjects);
                    adapterDel.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerDel.setAdapter(adapterDel);
                    spinnerDel.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                            title = subjects[position];
                            titleEng = subjectsEng[position];
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {

                        }
                    });
                    spinnerDel.setVisibility(View.VISIBLE);
                    btnDel.setVisibility(View.VISIBLE);
                } else
                    Toast.makeText(context, "Для удаления выберите группу", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnDel:
                if (spinnerDel.getAdapter().getCount() > 0)
                    if (delete.equals("subject")) {
                        showDialog(1);
                    } else if (delete.equals("student"))
                        showDialog(2);
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }

    protected Dialog onCreateDialog(int id) {
        if (id == 1) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Удаление предмета");
            adb.setMessage("Удалить предмет '" + title + "' у группы " + group + "?");
            adb.setIcon(R.drawable.ic_delete);
            adb.setPositiveButton("Да", myClickListener);
            adb.setNegativeButton("Нет", myClickListener);
            return adb.create();
        }
        if (id == 2) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Удаление студента");
            adb.setMessage("Удалить студента '" + surname + "' из группы " + group + "?");
            adb.setIcon(R.drawable.ic_delete);
            adb.setPositiveButton("Да", myClickListener2);
            adb.setNegativeButton("Нет", myClickListener2);
            return adb.create();
        }
        if (id == 3) {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setTitle("Удаление группы");
            adb.setMessage("Удалить группу " + group + "?");
            adb.setIcon(R.drawable.ic_delete);
            adb.setPositiveButton("Да", myClickListener3);
            adb.setNegativeButton("Нет", myClickListener3);
            return adb.create();
        }
        return super.onCreateDialog(id);
    }

    DialogInterface.OnClickListener myClickListener = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    progressSave.setVisibility(View.VISIBLE);
                    dbHelper.deleteSubject(groupEng, titleEng);
                    updateDB();
                    finish();
                    btnDel.setVisibility(View.INVISIBLE);
                    spinnerDel.setVisibility(View.INVISIBLE);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;

            }
        }
    };

    DialogInterface.OnClickListener myClickListener2 = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    progressSave.setVisibility(View.VISIBLE);
                    dbHelper.deleteStudent(groupEng, surname);
                    updateDB();
                    finish();
                    btnDel.setVisibility(View.INVISIBLE);
                    spinnerDel.setVisibility(View.INVISIBLE);
                    break;
                case Dialog.BUTTON_NEGATIVE:
                    break;

            }
        }
    };

    DialogInterface.OnClickListener myClickListener3 = new DialogInterface.OnClickListener() {
        public void onClick(DialogInterface dialog, int which) {
            switch (which) {
                case Dialog.BUTTON_POSITIVE:
                    progressSave.setVisibility(View.VISIBLE);
                    dbHelper.deleteGroup(groupEng);
                    updateDB();
                    finish();
                    group1.clear();
                    group2.clear();
                    group3.clear();
                    group4.clear();
                    groups.clear();
                    Cursor cursor = dbHelper.query("GROUPS", null, null, null, "Course");
                    if (cursor.moveToFirst()) {
                        do {
                            switch (cursor.getString(0)) {
                                case "1":
                                    group1.add(cursor.getString(1));
                                    c1++;
                                    break;
                                case "2":
                                    group2.add(cursor.getString(1));
                                    c2++;
                                    break;
                                case "3":
                                    group3.add(cursor.getString(1));
                                    c3++;
                                    break;
                                case "4":
                                    group4.add(cursor.getString(1));
                                    break;
                            }
                            groups.add(cursor.getString(2));
                        } while (cursor.moveToNext());
                    }

                    adapter = new ArrayAdapter<>(DelActivity.this, android.R.layout.simple_spinner_item, courses);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCourse.setAdapter(adapter);

                    spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(AdapterView<?> parent, View view,
                                                   int position, long id) {
                            switch (position) {
                                case 0:
                                    adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group1);
                                    course = 1;
                                    break;
                                case 1:
                                    adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group2);
                                    course = 2;
                                    break;
                                case 2:
                                    adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group3);
                                    course = 3;
                                    break;
                                case 3:
                                    adapterGroup = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, group4);
                                    course = 4;
                                    break;
                            }
                            spinnerGroup.setAdapter(adapterGroup);
                            adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        }

                        @Override
                        public void onNothingSelected(AdapterView<?> parent) {
                        }
                    });
                    btnDel.setVisibility(View.INVISIBLE);
                    spinnerDel.setVisibility(View.INVISIBLE);
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
                                progressSave.setVisibility(View.INVISIBLE);
                                Toast.makeText(getApplicationContext(), "Удаление завершено!", Toast.LENGTH_SHORT).show();
                            }
                        })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {

                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        Toast.makeText(getApplicationContext(), "Ошибка! \n" + exception.getMessage(), Toast.LENGTH_SHORT).show();

                    }
                });
    }
}
