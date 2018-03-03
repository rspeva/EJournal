package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.SharedPreferences;
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

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;

import static spevakov.ejournal.MainActivity.progressSave;
import static spevakov.ejournal.UserActivity.DB_NAME;
import static spevakov.ejournal.UserActivity.DB_PATH;


public class AddGroup extends Activity implements View.OnClickListener {

    Button btnAddGroup, btnCloseAG;
    Spinner spinnerCourse;
    EditText etGroup, etGroupEng;
    String[] courses = {"1", "2", "3", "4"};
    ArrayAdapter<String> adapter;
    String course, group, groupEng;
    DBHelper myDbHelper;
    StorageReference riversRef, storageReference;
    SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);

        btnAddGroup = (Button) findViewById(R.id.btnAddGroup);
        btnCloseAG = (Button) findViewById(R.id.btnCloseAG);
        spinnerCourse = (Spinner) findViewById(R.id.spinnerCourse);
        etGroup = (EditText) findViewById(R.id.etGroup);
        etGroupEng = (EditText) findViewById(R.id.etGroupEng);
        etGroupEng.setHintTextColor(Color.GRAY);
        etGroupEng.setHint("ПС-13  ->  PS13");


        btnAddGroup.setOnClickListener(this);
        btnCloseAG.setOnClickListener(this);

        myDbHelper = new DBHelper(this);
        myDbHelper.openDataBase();

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, courses);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapter);
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                course = String.valueOf(position + 1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnAddGroup:
                group = etGroup.getText().toString();
                groupEng = etGroupEng.getText().toString();
                if (groupEng.equals("") || group.equals(""))
                    Toast.makeText(getApplicationContext(), "Заполните все поля!", Toast.LENGTH_SHORT).show();
                else {
                    try {
                        progressSave.setVisibility(View.VISIBLE);
                        myDbHelper.createGroup(course, group, groupEng);
                        finish();
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
                                                progressSave.setVisibility(View.INVISIBLE);
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
                                        progressSave.setVisibility(View.INVISIBLE);
                                    }
                                });
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.btnCloseAG:
                progressSave.setVisibility(View.INVISIBLE);
                finish();
                break;
        }
    }


}
