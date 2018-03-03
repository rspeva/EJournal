package spevakov.ejournal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class UserActivity extends AppCompatActivity implements View.OnClickListener {
    StorageReference storageReference, mainRef;
    Button btnStudent, btnTeacher, btnOk;
    EditText etPassword;
    String password, prefPassword, prefVersion;
    int currentVersion, yourVersion;
    boolean visible, ready;
    File database;
    SharedPreferences preferences;
    ProgressDialog progressDialog;

    public static String DB_NAME = "database";
    public static String DB_PATH = Environment.getExternalStorageDirectory() + File.separator + "Ejournal" + File.separator;
    public static boolean login;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        btnStudent = (Button) findViewById(R.id.btnStudent);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnTeacher = (Button) findViewById(R.id.btnTeacher);
        btnOk = (Button) findViewById(R.id.btnOk);
        btnStudent.setOnClickListener(this);
        btnTeacher.setOnClickListener(this);
        btnOk.setOnClickListener(this);

        preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        prefPassword = preferences.getString("DBPW", "");
        if (prefPassword.equals("")) password = "112211";
        else password = prefPassword.substring(6, prefPassword.length() - 10);

        prefVersion = preferences.getString("version", "");
        if (prefVersion.equals("")) yourVersion = 0;
        else yourVersion = Integer.valueOf(prefVersion);

        File folder = new File(DB_PATH);
        if (!folder.exists()) folder.mkdir();
        database = new File(DB_PATH + DB_NAME);

        storageReference = FirebaseStorage.getInstance().getReference();
        mainRef = storageReference.child(DB_NAME);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Обновлене БД");
        progressDialog.setMessage("Проверка актуальности БД");
        if (!isOnline(this))
            Toast.makeText(this, "Для работы приложение необходим Интернет! Перезапустите приложение!", Toast.LENGTH_LONG).show();
        else progressDialog.show();

        checkUpdate();

//        StorageMetadata metadata = new StorageMetadata.Builder()
//                .setContentType("database")
//                .setCustomMetadata("version", "1")
//                .build();
//        mainRef.updateMetadata(metadata)
//                .addOnSuccessListener(new OnSuccessListener<StorageMetadata>() {
//                    @Override
//                    public void onSuccess(StorageMetadata storageMetadata) {
//                        // Updated metadata is in storageMetadata
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception exception) {
//                        // Uh-oh, an error occurred!
//                    }
//                });


    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        switch (v.getId()) {
            case R.id.btnStudent:
                login = false;
                if (ready)
                    startActivity(intent);
                else Toast.makeText(this, "БД не готова!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btnTeacher:
                if (!visible) {
                    etPassword.setVisibility(View.VISIBLE);
                    etPassword.setHint("Введите пароль");
                    etPassword.requestFocus();
                    btnOk.setVisibility(View.VISIBLE);
                    visible = true;
                } else {
                    etPassword.setVisibility(View.INVISIBLE);
                    etPassword.setHint("");
                    btnOk.setVisibility(View.INVISIBLE);
                    visible = false;
                }
                imm.toggleSoftInput(0, 0);

                break;
            case R.id.btnOk:
                if (!etPassword.getText().toString().equals(password)) {
                    Toast.makeText(this, "Неправильный пароль!", Toast.LENGTH_LONG).show();
                    etPassword.setText("");

                } else {
                    login = true;
                    imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
                    if (ready)
                        startActivity(intent);
                    else Toast.makeText(this, "БД не готова!", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm != null ? cm.getActiveNetworkInfo() : null;
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    public void checkUpdate() {
        mainRef.getMetadata().addOnSuccessListener(new OnSuccessListener() {
            @Override
            public void onSuccess(Object storageMetadata) {
                StorageMetadata st = (StorageMetadata) storageMetadata;
                try {
                    currentVersion = Integer.valueOf(st.getCustomMetadata("version"));
                } catch (Exception e) {
                    Toast.makeText(UserActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
                if (yourVersion == currentVersion && database.exists()) {
                    ready = true;
                    progressDialog.dismiss();
                    Toast.makeText(UserActivity.this, "Обновление не требуется!", Toast.LENGTH_SHORT).show();
                } else updateDB();
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        progressDialog.dismiss();
                        ready = false;
                        Toast.makeText(UserActivity.this, "Ошибка! \n" + exception, Toast.LENGTH_LONG).show();
                    }
                });
    }

    public void updateDB() {
        progressDialog.setMessage("Обновление БД!");
        if (database.exists()) database.delete();
        mainRef.getFile(database)
                .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("version", String.valueOf(currentVersion));
                        editor.apply();
                        ready = true;
                        progressDialog.dismiss();
                        Toast.makeText(UserActivity.this, "Успешно!", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                progressDialog.dismiss();
                Toast.makeText(UserActivity.this, "Ошибка!\n" + exception.getMessage(), Toast.LENGTH_LONG).show();
                ready = false;
            }
        });
    }
}

