package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.R;
import spevakov.ejournal.adapters.LvAdapter;
import spevakov.ejournal.adapters.LvStudentListMarkAdapter;

public class StudentListMark extends Activity {

    String [] title, teacher, avr, titleEng;
    String surnameEng, groupEng, surname;


    LvStudentListMarkAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spmarks);

        Intent intent = getIntent();
        title = intent.getExtras().getStringArray("Title");
        teacher = intent.getExtras().getStringArray("Teacher");
        avr = intent.getExtras().getStringArray("Avr");
        surnameEng = intent.getExtras().getString("SurnameEng");
        titleEng = intent.getExtras().getStringArray("TitleEng");
        groupEng = intent.getExtras().getString("GroupEng");
        surname = intent.getExtras().getString("Surname");

        ArrayList<Map<String, Object>> lessons = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        for (int i = 0; i < title.length; i++){
            m = new HashMap<String, Object>();
            m.put("Title", title[i]);
            m.put("Teacher", teacher[i]);
            String s = String.format("%.2f", Float.valueOf(avr[i]));
            m.put("Avr",s);
            m.put("TitleEng",titleEng[i]);
            m.put("SurnameEng",surnameEng);
            m.put("GroupEng",groupEng);
            m.put("Surname", surname);
            lessons.add(m);
        }


        adapter = new LvStudentListMarkAdapter(lessons, this);
        ListView lvListMarks = (ListView)findViewById(R.id.lvSP);
        lvListMarks.setAdapter(adapter);


        Button btnCancelSP = (Button) findViewById(R.id.btnCancelSP);
        btnCancelSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
}
