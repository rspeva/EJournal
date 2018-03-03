package spevakov.ejournal.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import spevakov.ejournal.R;

public class LvEditAdapter extends BaseAdapter implements ListAdapter {
    private ArrayList<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
    private Context context;

    public LvEditAdapter(ArrayList<Map<String, Object>> list, Context context) {
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
            view = inflater.inflate(R.layout.item_lv_listmarks, null);
        }

        TextView listItemText = (TextView)view.findViewById(R.id.tvSurnameListmakrs);
        Button btnMarks = (Button)view.findViewById(R.id.btnMarks);

        listItemText.setText(list.get(position).get("Surname").toString());
        btnMarks.setText(list.get(position).get("Marks").toString());

        btnMarks.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                showPopupMenu(v, position);
                //notifyDataSetChanged();
            }
        });

        return view;
    }

    private void showPopupMenu(final View v, final int position) {
        PopupMenu popupMenu = new PopupMenu(context, v);
        popupMenu.inflate(R.menu.popupmenu);
        if (((Button) v).getText()!="") popupMenu.getMenu().findItem(R.id.menuClear).setEnabled(true);

            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        Map<String, Object> m;
                        switch (item.getItemId()) {
                            case R.id.menuN:
                                ((Button) v).setText("H");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "Н");
                                list.set(position,m);
                                return true;
                            case R.id.menu2:
                                ((Button) v).setText("2");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "2");
                                list.set(position,m);
                                return true;
                            case R.id.menu3:
                                ((Button) v).setText("3");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "3");
                                list.set(position,m);
                                return true;
                            case R.id.menu4:
                                ((Button) v).setText("4");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "4");
                                list.set(position,m);
                                return true;
                            case R.id.menu5:
                                ((Button) v).setText("5");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "5");
                                list.set(position,m);
                                return true;
                            case R.id.menuClear:
                                ((Button) v).setText("");
                                m = new HashMap<String, Object>();
                                m.put("Surname", list.get(position).get("Surname"));
                                m.put("Marks", "");
                                list.set(position,m);
                                item.setEnabled(false);
                                return true;
                            default:
                                return false;
                        }
                    }
                });

        popupMenu.show();
    }

    public ArrayList<Map<String, Object>> getMarks(){
        return list;
    }
}