package spevakov.ejournal.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.R;
import spevakov.ejournal.adapters.LvEditAdapter;

public class ListMarks extends Activity {


    String [] students, marks;

    Button btnSave;

    LvEditAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_marks);

        Intent intent = getIntent();
        students = intent.getExtras().getStringArray("Students");
        marks = intent.getExtras().getStringArray("Marks");

        ArrayList<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        Map<String, Object> m;
        String mark;
        for (int i = 0; i < students.length; i++){
                m = new HashMap<String, Object>();
                m.put("Surname", students[i]);
                if (marks[i].equals("0"))
                    mark = "";
                else mark = marks[i];
                m.put("Marks", mark);
                data.add(m);
            }

         adapter = new LvEditAdapter(data, this);
         ListView lvListMarks = (ListView)findViewById(R.id.lvListMarks);
        lvListMarks.setAdapter(adapter);


        Button btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<Map<String, Object>> data = adapter.getMarks();
                String[] marksResult = new String[data.size()];
                for (int i = 0; i < data.size(); i++){
                    if (data.get(i).get("Marks").toString().equals(""))
                        marksResult[i] = "0";
                    else marksResult[i]= data.get(i).get("Marks").toString();
                }

                Intent intent = new Intent();
                intent.putExtra("marksResult", marksResult);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        Button btnCancel = (Button) findViewById(R.id.btnCancel);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }



}
