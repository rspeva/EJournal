package spevakov.ejournal.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.R;
import spevakov.ejournal.activity.SPMarks;
import spevakov.ejournal.activity.StudentProgress;

public class LvStudentListMarkAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private Context context;


    public LvStudentListMarkAdapter(ArrayList<Map<String, Object>> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_lv_student_marks, null);
        }

        TextView listItemTitle = (TextView)view.findViewById(R.id.tvSubjectTitle);
        TextView listItemTeacher = (TextView)view.findViewById(R.id.tvTeacherName);
        Button btnMarks = (Button)view.findViewById(R.id.btnAvr);

        listItemTitle.setText(list.get(position).get("Title").toString());
        listItemTeacher.setText(list.get(position).get("Teacher").toString());
        btnMarks.setText(list.get(position).get("Avr").toString());



        btnMarks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StudentProgress.class);
                intent.putExtra("SurnameEng", list.get(position).get("SurnameEng").toString());
                intent.putExtra("TitleEng", list.get(position).get("TitleEng").toString());
                intent.putExtra("Title", list.get(position).get("Title").toString());
                intent.putExtra("GroupEng",  list.get(position).get("GroupEng").toString());
                intent.putExtra("Surname", list.get(position).get("Surname").toString());
                context.startActivity(intent);

            }
        });

        return view;
    }



    public ArrayList<Map<String, Object>> getMarks(){
        return list;
    }
}