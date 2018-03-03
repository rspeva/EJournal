package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import spevakov.ejournal.R;

public class Rollcall extends Activity implements View.OnClickListener {

    String [] students;
    String [] rollcallResult;
    TextView tvSurnameRollcall;
    int i = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rollcall);

        Intent intent = getIntent();
        students = intent.getExtras().getStringArray("Students");
        rollcallResult = new String[students.length];

        setTitle("Перекличка | " + intent.getExtras().getString("Group"));

        Button btnNo = (Button) findViewById(R.id.btnNo);
        Button btnYes = (Button) findViewById(R.id.btnYes);
        tvSurnameRollcall = (TextView) findViewById(R.id.tvSurnameRollcall);
        tvSurnameRollcall.setText(students[i]);

        btnNo.setOnClickListener(this);
        btnYes.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnYes:
                rollcallResult[i] = "0";
                if (i == students.length-1){
                    Intent intent = new Intent();
                    intent.putExtra("rollcallResult", rollcallResult);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    i++;
                    tvSurnameRollcall.setText(students[i]);
                }
                break;
            case R.id.btnNo:
                rollcallResult[i] = "Н";
                if (i == students.length-1){
                    Intent intent = new Intent();
                    intent.putExtra("rollcallResult", rollcallResult);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    i++;
                    tvSurnameRollcall.setText(students[i]);
                } break;
        }

    }
}
