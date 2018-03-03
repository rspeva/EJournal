package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import spevakov.ejournal.R;

public class ChangePassword extends Activity implements View.OnClickListener {

    Button btnClose, btnChangePass;
    EditText etOldPass, etNewPass, etNewPass2;
    String np, np2, op;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_pass);

        btnChangePass = (Button) findViewById(R.id.btnChangePass);
        btnClose = (Button) findViewById(R.id.btnClose);

        btnChangePass.setOnClickListener(this);
        btnClose.setOnClickListener(this);

        etOldPass = (EditText) findViewById(R.id.etOldPass);
        etNewPass = (EditText) findViewById(R.id.etNewPass);
        etNewPass2 = (EditText) findViewById(R.id.etNewPass2);

        etOldPass.setHintTextColor(Color.GRAY);
        etNewPass.setHintTextColor(Color.GRAY);
        etNewPass2.setHintTextColor(Color.GRAY);

        etOldPass.setHint("Старый пароль");
        etNewPass.setHint("Новый пароль");
        etNewPass2.setHint("Повторите пароль");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnChangePass:
                String password, pref;
                SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                pref = preferences.getString("DBPW", "");
                if (pref.equals("")) password = "112211";
                else password = pref.substring(6, pref.length() - 10);
                np = etNewPass.getText().toString();
                np2 = etNewPass2.getText().toString();
                op = etOldPass.getText().toString();
                if (np.equals("") || np2.equals("") || op.equals(""))
                    Toast.makeText(this, "Заполните все поля!", Toast.LENGTH_SHORT).show();
                else if (!op.equals(password))
                    Toast.makeText(this, "Старый пароль указан неверно!", Toast.LENGTH_SHORT).show();
                else if (!np.equals(np2))
                    Toast.makeText(this, "Пароли не совпадают!", Toast.LENGTH_SHORT).show();
                else {
                    password = np;
                    preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("MD5", "C93D3BF7A7C4AFE94B64E30C2CE39F4F");
                    editor.putString("DBPW", "B124C3" + password + "44323DE4F5");
                    editor.putString("VCL", "A523DF234EDC43F4E23C123");
                    editor.apply();
                    Toast.makeText(getApplicationContext(), "Пароль успешно изменен!", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
            case R.id.btnClose:
                finish();
                break;
        }
    }
}
