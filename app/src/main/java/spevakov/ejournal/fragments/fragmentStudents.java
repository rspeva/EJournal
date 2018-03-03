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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.DBHelper;
import spevakov.ejournal.R;
import spevakov.ejournal.activity.StudentActivity;
import spevakov.ejournal.adapters.LvAdapter;


public class fragmentStudents extends Fragment {

    int courseN;

    Spinner spinnerGroup, spinnerCourse;

    ListView lvStudents;
    String group, groupEng;
    DBHelper myDbHelper;

    String[] course = {"1","2", "3", "4"};
    ArrayList<String> group1 = new ArrayList<>();
    ArrayList<String> group2 = new ArrayList<>();
    ArrayList<String> group3 = new ArrayList<>();
    ArrayList<String> group4 = new ArrayList<>();
    ArrayList<String> groups = new ArrayList<>();
    int c1=0, c2=0, c3=0;
    ArrayAdapter<String> adapterCourse;
    ArrayAdapter<String> adapterGroup;
    LvAdapter adapterStudent;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getActivity().setTitle("Студенты");


    }

    @Override
    public void onResume() {
        super.onResume();
        group1.clear();
        group2.clear();
        group3.clear();
        group4.clear();
        groups.clear();
        myDbHelper = new DBHelper(getActivity());
        myDbHelper.openDataBase();


        Cursor cursor = myDbHelper.query("GROUPS", null, null, null, "Course");
        if (cursor.moveToFirst()) {
            do {
                switch (cursor.getString(0)) {
                    case "1":
                        group1.add(cursor.getString(1));
                        c1++;
                        break;
                    case "2":
                        group2.add(cursor.getString(1));
                        c2++;
                        break;
                    case "3":
                        group3.add(cursor.getString(1));
                        c3++;
                        break;
                    case "4":
                        group4.add(cursor.getString(1));
                        break;
                }
                groups.add(cursor.getString(2));
            } while (cursor.moveToNext());
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_students, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        spinnerCourse = (Spinner) view.findViewById(R.id.spinnerCourse);
        spinnerGroup = (Spinner) view.findViewById(R.id.spinnerGroup);
        lvStudents = (ListView) view.findViewById(R.id.lvStudents);



        adapterCourse = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, course);
        adapterCourse.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCourse.setAdapter(adapterCourse);
        spinnerCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                switch (position) {
                    case 0:
                        adapterGroup = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, group1);
                        courseN = 1;
                        break;
                    case 1:
                        adapterGroup = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, group2);
                        courseN = 2;
                         break;
                    case 2:
                        adapterGroup = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, group3);
                        courseN = 3;
                         break;
                    case 3:
                        adapterGroup = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, group4);
                        courseN = 4;
                        break;
                }

                spinnerGroup.setAdapter(adapterGroup);
                adapterGroup.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerGroup.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        switch (courseN) {
                            case 1:
                                groupEng = groups.get(position);
                                group = group1.get(position);
                                break;
                            case 2:
                                groupEng = groups.get(position + c1);
                                group = group2.get(position);
                                break;
                            case 3:
                                groupEng = groups.get(position + c1 + c2);
                                group = group3.get(position);
                                break;
                            case 4:
                                groupEng = groups.get(position + c1 + c2 + c3);
                                group = group4.get(position);
                                break;
                        }


                        final Cursor cursor = myDbHelper.query(groupEng, null, null, null, "Surname");

                        ArrayList<Map<String, Object>> students = new ArrayList<>(cursor.getCount());
                        Map<String, Object> m;
                        if (cursor.moveToFirst()) {
                            do {
                                m = new HashMap<>();
                                m.put("Surname", cursor.getString(0));
                                m.put("Name", cursor.getString(1));
                                m.put("Lastname", cursor.getString(2));
                                students.add(m);

                            } while (cursor.moveToNext());
                        }

                        String[] from = {"Surname", "Name", "Lastname"};
                        int[] to = {R.id.tvSurname, R.id.tvName, R.id.tvLastname};
                        adapterStudent = new LvAdapter(getActivity(), students, R.layout.item_lv_students, from, to);
                        lvStudents.setAdapter(adapterStudent);
                        lvStudents.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                cursor.moveToPosition(position);
                                Intent i = new Intent(getActivity(),StudentActivity.class);
                                i.putExtra("Surname", cursor.getString(0));
                                i.putExtra("Name", cursor.getString(1));
                                i.putExtra("Lastname", cursor.getString(2));
                                i.putExtra("SurnameEng", cursor.getString(3));
                                i.putExtra("Status", cursor.getString(4));
                                i.putExtra("GroupEng", groupEng);
                                i.putExtra("Group", group);
                                startActivity(i);
                            }
                        });

                    }


                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {}
                });
            }
            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });

    }
}
