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

public class SPMarks extends Activity {

    String [] theme, dates, marks, types;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spmarks);
        Intent intent = getIntent();
        theme = intent.getExtras().getStringArray("Theme");
        dates = intent.getExtras().getStringArray("Date");
        marks = intent.getExtras().getStringArray("Marks");
        types = intent.getExtras().getStringArray("Type");

        ArrayList<Map<String, Object>> lessons = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        String mark;
        for (int i = 0; i < marks.length; i++){
            m = new HashMap<String, Object>();
            m.put("Theme", theme[i]);
            m.put("Date", dates[i]);
            m.put("Type", types[i]);
            if (marks[i].equals("0"))
                mark = "";
            else mark = marks[i];
            m.put("Marks", mark);
            lessons.add(m);
        }

        String[] from = {"Date", "Theme", "Marks", "Type"};
        int[] to = {R.id.tvDate, R.id.tvTheme, R.id.btnMarksSP, R.id.tvSubjectType};
        LvAdapter adapter = new LvAdapter(getApplicationContext(), lessons, R.layout.item_lv_student_progress, from, to);
        ListView listView = (ListView) findViewById(R.id.lvSP);
        listView.setAdapter(adapter);

        Button btnCancelSP = (Button) findViewById(R.id.btnCancelSP);
        btnCancelSP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
