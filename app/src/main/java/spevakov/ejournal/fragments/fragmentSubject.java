package spevakov.ejournal.fragments;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.app.Fragment;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;
import spevakov.ejournal.activity.SubjectActivity;
import spevakov.ejournal.adapters.LvAdapter;


public class fragmentSubject extends Fragment {

    Spinner spinnerGroupSubject, spinnerCourseSubject;
    ListView lvSubjects;
    ArrayAdapter<String> adapterGroup;
    LvAdapter adapterSubject;

    String group;
    String[] course = {"1", "2", "3", "4"};
    ArrayList<String> group1 = new ArrayList<>();
    ArrayList<String> group2 = new ArrayList<>();
    ArrayList<String> group3 = new ArrayList<>();
    ArrayList<String> group4 = new ArrayList<>();


    DBHelper myDbHelper;

    int courseN;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Предметы");

    }

    @Override
    public void onResume() {
        super.onResume();

        group1.clear();
        group2.clear();
        group3.clear();
        group4.clear();
        myDbHelper = new DBHelper(getActivity());

        myDbHelper.openDataBase();


        Cursor cursor = myDbHelper.query("GROUPS", null, null, null, "Course");
        if (cursor.moveToFirst()) {
            do {
                switch (cursor.getString(0)) {
                    case "1":
                        group1.add(cursor.getString(1));
                        break;
                    case "2":
                        group2.add(cursor.getString(1));
                        break;
                    case "3":
                        group3.add(cursor.getString(1));
                        break;
                    case "4":
                        group4.add(cursor.getString(1));
                        break;
                }
            } while (cursor.moveToNext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_subject, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCourseSubject = (Spinner) view.findViewById(R.id.spinnerCourseSubject);
        spinnerGroupSubject = (Spinner) view.findViewById(R.id.spinnerGroupSubject);
        lvSubjects = (ListView) view.findViewById(R.id.lvSubjects);



        ArrayAdapter<String> adapterCourse = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, course);
        adapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourseSubject.setAdapter(adapterCourse);
        spinnerCourseSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        adapterGroup = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, group1);
                        courseN = 1;
                       break;
                    case 1:
                        adapterGroup = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, group2);
                        courseN = 2;
                        break;
                    case 2:
                        adapterGroup = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, group3);
                        courseN = 3;
                         break;
                    case 3:
                        adapterGroup = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, group4);
                        courseN = 4;
                        break;
                }

                spinnerGroupSubject.setAdapter(adapterGroup);
                adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGroupSubject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (courseN){
                            case 1:
                                group = group1.get(position);
                                break;
                            case 2:
                                group = group2.get(position);
                                break;
                            case 3:
                                group = group3.get(position);
                                break;
                            case 4:
                                group = group4.get(position);
                                break;
                        }

                        String selection = "Groups = ?";
                        String [] selectionArgs = {group};
                        final Cursor cursor = myDbHelper.query("SUBJECT", null, selection, selectionArgs, "Title");
                        ArrayList<Map<String, Object>> subjects = new ArrayList<Map<String, Object>>(cursor.getCount());
                        Map<String, Object> m;
                        if (cursor.moveToFirst()) {
                            do {
                                m = new HashMap<String, Object>();
                                m.put("Title", cursor.getString(0));
                                m.put("Teacher", cursor.getString(1));
                                subjects.add(m);

                            } while (cursor.moveToNext());
                        }

                        String[] from = {"Title", "Teacher"};
                        int[] to = {R.id.tvTitle, R.id.tvTeacher};

                        adapterSubject = new LvAdapter(getActivity(), subjects, R.layout.item_lv_subjects, from, to);
                        lvSubjects.setAdapter(adapterSubject);
                        lvSubjects.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                cursor.moveToPosition(position);
                                Intent i = new Intent(getActivity(),SubjectActivity.class);
                                i.putExtra("Title", cursor.getString(0));
                                i.putExtra("Groups", cursor.getString(2));
                                i.putExtra("GroupEng", cursor.getString(3));
                                i.putExtra("TitleEng", cursor.getString(4));
                                startActivity(i);
                            }
                        });

                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }



}
