package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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
import spevakov.ejournal.MainActivity;
import spevakov.ejournal.R;

import static spevakov.ejournal.MainActivity.progressSave;
import static spevakov.ejournal.UserActivity.DB_NAME;
import static spevakov.ejournal.UserActivity.DB_PATH;

public class AddStudent extends Activity implements View.OnClickListener {
    Context context = this;
    Button btnAddStudent, btnCloseAS;
    Spinner spinnerCourse, spinnerStatus, spinnerGroup;
    EditText etSurname, etName, etSurnameEng, etLastname;
    String surname, name, lastname, surnameEng;
    String[] courses = {"1", "2", "3", "4"};
    String[] statusArr = {"Бюджет", "Контракт"};
    DBHelper dbHelper;
    ArrayAdapter<String> adapter, adapterGroup, adapterStatus;
    String groupEng, status;
    int course, c1 = 0, c2 = 0, c3 = 0;
    ArrayList<String> group1 = new ArrayList<>();
    ArrayList<String> group2 = new ArrayList<>();
    ArrayList<String> group3 = new ArrayList<>();
    ArrayList<String> group4 = new ArrayList<>();
    ArrayList<String> groups = new ArrayList<>();
    StorageReference riversRef, storageReference;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_student);

        group1.clear();
        group2.clear();
        group3.clear();
        group4.clear();

        btnAddStudent = (Button) findViewById(R.id.btnAddStudent);
        btnCloseAS = (Button) findViewById(R.id.btnCloseAS);
        btnAddStudent.setOnClickListener(this);
        btnCloseAS.setOnClickListener(this);

        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        spinnerStatus = (Spinner) findViewById(R.id.spinnerStatus);
        spinnerGroup = (Spinner) findViewById(R.id.spinnerGroup);

        etLastname = (EditText) findViewById(R.id.etLastname);
        etSurname = (EditText) findViewById(R.id.etSurname);
        etName = (EditText) findViewById(R.id.etName);
        etSurnameEng = (EditText) findViewById(R.id.etSurnameEng);
        etSurnameEng.setHintTextColor(Color.GRAY);
        etSurnameEng.setHint("Смирнов  ->  Smirnov");

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

        adapterStatus = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, statusArr);
        adapterStatus.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerStatus.setAdapter(adapterStatus);
        spinnerStatus.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) status = "Бюджет";
                else status = "Контракт";
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);

        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
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
                switch (course) {
                    case 1:
                        groupEng = groups.get(position);
                        break;
                    case 2:
                        groupEng = groups.get(position + c1);
                        break;
                    case 3:
                        groupEng = groups.get(position + c1 + c2);
                        break;
                    case 4:
                        groupEng = groups.get(position + c1 + c2 + c3);
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
            case R.id.btnAddStudent:
                surname = etSurname.getText().toString();
                name = etName.getText().toString();
                lastname = etLastname.getText().toString();
                surnameEng = etSurnameEng.getText().toString();
                if (surname.equals("") || name.equals("") || lastname.equals("") || surnameEng.equals(""))
                    Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                else if (spinnerGroup.getAdapter().getCount() > 0)
                    try {
                        MainActivity.progressSave.setVisibility(View.VISIBLE);
                        dbHelper.createStudent(groupEng, surname, name, lastname, surnameEng, status);
                        updateDB();
                        finish();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                else Toast.makeText(context, "Выберите группу", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnCloseAS:
                progressSave.setVisibility(View.INVISIBLE);
                finish();
                break;
        }
    }

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
                                MainActivity.progressSave.setVisibility(View.INVISIBLE);
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
                        MainActivity.progressSave.setVisibility(View.INVISIBLE);
                    }
                });
    }

}
