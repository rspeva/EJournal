package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.backendless.Backendless;
import com.backendless.async.callback.AsyncCallback;
import com.backendless.exceptions.BackendlessFault;

import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.R;
import spevakov.ejournal.activity.SubjectActivity;

public class    SubjectInfo extends Activity {
    Intent i = new Intent();
    SwitchCompat colorSwitch;
    EditText etThemeInfo, etDZInfo, etDateInfo, etTypeInfo;
    CheckBox checkBox;
    String date, type, theme, dz, objectid, groupEng, titleEng;
    int id;
    boolean checked, edit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_info);

        Intent intent = getIntent();
        date = intent.getExtras().getString("Date");
        type = intent.getExtras().getString("Type");
        theme = intent.getExtras().getString("Theme");
        dz = intent.getExtras().getString("DZ");
        checked = intent.getExtras().getBoolean("checked");
        edit = intent.getExtras().getBoolean("edit");
        id = intent.getExtras().getInt("id");

        setTitle(date + " | " + type);

        etDateInfo = (EditText) findViewById(R.id.etDateInfo);
        etThemeInfo = (EditText) findViewById(R.id.etThemeInfo);
        etDZInfo = (EditText) findViewById(R.id.etDZInfo);
        etTypeInfo = (EditText) findViewById(R.id.etTypeInfo);
        checkBox = (CheckBox) findViewById(R.id.checkBoxDel);

        etDateInfo.setText(date);
        etThemeInfo.setText(theme);
        etDZInfo.setText(dz);
        etTypeInfo.setText(type);

        if (edit){
            objectid = intent.getExtras().getString("objectId");
            groupEng = intent.getExtras().getString("GroupEng");
            titleEng = intent.getExtras().getString("TitleEng");
            etDateInfo.setFocusable(true);
            etDateInfo.setFocusableInTouchMode(true);
            etDateInfo.setClickable(true);

            etThemeInfo.setFocusable(true);
            etThemeInfo.setFocusableInTouchMode(true);
            etThemeInfo.setClickable(true);

            etDZInfo.setFocusable(true);
            etDZInfo.setFocusableInTouchMode(true);
            etDZInfo.setClickable(true);

            etTypeInfo.setFocusable(true);
            etTypeInfo.setFocusableInTouchMode(true);
            etTypeInfo.setClickable(true);

            checkBox.setClickable(true);
            checkBox.setVisibility(View.VISIBLE);
        }


        Button btnCloseInfo = (Button) findViewById(R.id.btnCloseInfo);
        btnCloseInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!etDateInfo.getText().toString().equals(date) || !etThemeInfo.getText().toString().equals(theme) ||
                        !etDZInfo.getText().toString().equals(dz) || !etTypeInfo.getText().toString().equals(type) || checkBox.isChecked()) {
                    if (etDateInfo.getText().toString().length() != 5)
                        Toast.makeText(getApplicationContext(), "Укажите дату в формате 'дд.мм'", Toast.LENGTH_SHORT).show();
                    else if (!etTypeInfo.getText().toString().equals("Лекция") && !etTypeInfo.getText().toString().equals("Практическая") &&
                            !etTypeInfo.getText().toString().equals("Лабороторная") && !etTypeInfo.getText().toString().equals("Контрольная") &&
                            !etTypeInfo.getText().toString().equals("Атестация") && !etTypeInfo.getText().toString().equals("Семестровая"))
                        Toast.makeText(getApplicationContext(),
       "Неправильный тип занятия. Доступные типы: \n Лекция \n Практическая \n Лабороторная \n Контрольная \n Атестация \n Семестровая", Toast.LENGTH_LONG).show();
                    else {
                        if (checkBox.isChecked())
                            i.putExtra("Dates", "0000");
                        else i.putExtra("Dates", etDateInfo.getText().toString());

                        i.putExtra("DZ", etDZInfo.getText().toString());
                        i.putExtra("Theme", etThemeInfo.getText().toString());
                        i.putExtra("Type", etTypeInfo.getText().toString());
                        i.putExtra("checked", checked);
                        i.putExtra("changed", true);
                        i.putExtra("id", id);
                        setResult(RESULT_OK,i);
                        finish();
                    }

                } else {
                    i.putExtra("checked", checked);
                    i.putExtra("changed", false);
                    setResult(RESULT_OK,i);
                    finish();
                }
            }
        });

        colorSwitch = (SwitchCompat) findViewById(R.id.colorSwitch);
        colorSwitch.setChecked(checked);
        colorSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    checked = true;
                } else {
                    checked = false;
                }
            }
        });

    }


}
