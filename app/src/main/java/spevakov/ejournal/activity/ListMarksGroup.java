package spevakov.ejournal.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import spevakov.ejournal.R;

public class ListMarksGroup extends AppCompatActivity implements View.OnClickListener {

    LinearLayout llSurname, llJournal;
    String[] theme, dates, surnameArr, types, markList, dz;
    static boolean checked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_marks_group);

        llSurname = (LinearLayout) findViewById(R.id.llSurname);
        Intent intent = getIntent();
        TextView tvTitle = (TextView) findViewById(R.id.tvTitleJournal);
        tvTitle.setText((intent.getExtras().getString("Group") + " | " + intent.getExtras().getString("Title")));

        theme = intent.getExtras().getStringArray("Theme");
        dates = intent.getExtras().getStringArray("Date");
        types = intent.getExtras().getStringArray("Type");
        surnameArr = intent.getExtras().getStringArray("Surname");
        markList = intent.getExtras().getStringArray("MarkList");
        dz = intent.getExtras().getStringArray("DZ");

        LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lButtonParams.setMargins(0, 3, 0, 3);

        for (int i = -1; i < surnameArr.length; i++) {
            Button btn = new Button(this);
            btn.setLayoutParams(lButtonParams);

            if (i == -1) {
                ImageView iv = new ImageView(this);
                iv.setImageResource(R.drawable.iv_back);
                iv.setPadding(0, 0, 0, 6);
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ListMarksGroup.super.finish();
                    }
                });
                llSurname.addView(iv);
            } else {
                btn.setText(surnameArr[i]);
                btn.setClickable(false);
                btn.setBackgroundColor(getResources().getColor(R.color.colorSurname));
                llSurname.addView(btn);
            }

        }
        createLayout(checked);
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),SubjectInfo.class);
        intent.putExtra("Date", dates[v.getId()]);
        intent.putExtra("Theme", theme[v.getId()]);
        intent.putExtra("Type", types[v.getId()]);
        intent.putExtra("DZ", dz[v.getId()]);
        intent.putExtra("checked", checked);
        intent.putExtra("edit", false);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK)
            if (checked != data.getExtras().getBoolean("checked")){
                checked = data.getExtras().getBoolean("checked");
                createLayout(checked);
            }

        }


        void createLayout(boolean check){
            llJournal = (LinearLayout) findViewById(R.id.llJournal);
            llJournal.removeAllViewsInLayout();
            LinearLayout.LayoutParams llVerParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

            LinearLayout.LayoutParams lButtonParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lButtonParams.setMargins(0, 3, 0, 3);
            int var = 0;

            for (int i = 0; i < dates.length; i++) {
                LinearLayout llVertical = new LinearLayout(this);
                llVertical.setLayoutParams(llVerParams);
                llVertical.setOrientation(LinearLayout.VERTICAL);
                llVertical.setPadding(0, 0, 5, 0);
                Button btn1 = new Button(this);
                btn1.setLayoutParams(lButtonParams);
                btn1.setText(dates[i]);
                btn1.setBackgroundResource(R.drawable.btn_date_journal);
                btn1.setClickable(true);
                btn1.setOnClickListener(this);
                btn1.setId(i);
                llVertical.addView(btn1);
                for (int j = 0; j < surnameArr.length; j++) {
                    Button btn = new Button(this);
                    btn.setLayoutParams(lButtonParams);
                        if (markList[var].equals("0"))
                            btn.setText(" ");
                        else
                            btn.setText(String.valueOf(markList[var]));
                        var++;
                        btn.setClickable(false);
                        if (check){
                            switch(types[i]){
                                case "Практическая":
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorPR));
                                    break;
                                case "Лабороторная":
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorLR));
                                    break;
                                case "Контрольная":
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorKR));
                                    break;
                                case "Атестация":
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorAT));
                                    break;
                                case "Семестровая":
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorSM));
                                    break;
                                default:
                                    btn.setBackgroundColor(getResources().getColor(R.color.colorNeutral));
                                    break;
                            }
                        } else btn.setBackgroundColor(getResources().getColor(R.color.colorNeutral));

                        btn.setGravity(Gravity.CENTER);
                    llVertical.addView(btn);

                }
                llJournal.addView(llVertical);

            }
        }

    }

