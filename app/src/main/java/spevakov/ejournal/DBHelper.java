package spevakov.ejournal;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static spevakov.ejournal.UserActivity.DB_NAME;
import static spevakov.ejournal.UserActivity.DB_PATH;

public class DBHelper extends SQLiteOpenHelper {

    private SQLiteDatabase sqlDB;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, 10);
    }

    public void openDataBase() throws SQLException {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READONLY);
    }

    @Override
    public synchronized void close() {
        if (sqlDB != null)
            sqlDB.close();
        super.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
        return sqlDB.query(table, columns, selection, selectionArgs, null, null, orderBy);
    }


    public void createGroup(String course, String groupRus, String groupEng) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues initialValues = new ContentValues();
        initialValues.put("Course", course);
        initialValues.put("GroupRus", groupRus);
        initialValues.put("GroupEng", groupEng);
        sqlDB.execSQL("create table " + groupEng + " ("
                + "Surname text,"
                + "Name text,"
                + "Lastname text,"
                + "SurnameEng text,"
                + "Status text" + ");");
        sqlDB.insert("GROUPS", null, initialValues);
        sqlDB.close();
    }

    public void createSubject(String title, String teacher, String group, String groupEng, String titleEng) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues initialValues = new ContentValues();
        initialValues.put("Title", title);
        initialValues.put("Teacher", teacher);
        initialValues.put("Groups", group);
        initialValues.put("GroupEng", groupEng);
        initialValues.put("TitleEng", titleEng);
        sqlDB.insert("SUBJECT", null, initialValues);
        sqlDB.close();
    }

    public void createStudent(String groupEng, String surname, String name, String lastname, String surnameEng, String status) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        ContentValues initialValues = new ContentValues();
        initialValues.put("Surname", surname);
        initialValues.put("Name", name);
        initialValues.put("Lastname", lastname);
        initialValues.put("SurnameEng", surnameEng);
        initialValues.put("Status", status);
        sqlDB.insert(groupEng, null, initialValues);

        String selection = "GroupEng = ?";
        String[] selectionArgs = {groupEng};
        Cursor cursor = query("SUBJECT", null, selection, selectionArgs, null);
        String[] titleEng = new String[cursor.getCount()];
        int j = 0;
        if (cursor.moveToFirst())
            do {
                titleEng[j] = cursor.getString(4);
                j++;
            } while (cursor.moveToNext());
        cursor.close();
        if (titleEng.length != 0)
            for (String title : titleEng)
                try {
                    sqlDB.execSQL("alter table " + groupEng + "_" + title + " add column " + surnameEng + " text;");
                } catch (SQLException ignored) {
                }
        sqlDB.close();
    }

    public void deleteSubject(String groupEng, String titleEng) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        String[] wc = {groupEng, titleEng};
        sqlDB.delete("SUBJECT", "GroupEng = ? AND TitleEng = ?", wc);
        sqlDB.execSQL("drop table if exists " + groupEng + "_" + titleEng);
        sqlDB.close();
    }

    public void deleteGroup(String groupEng) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        sqlDB.execSQL("drop table if exists " + groupEng);

        String selection = "GroupEng = ?";
        String[] selectionArgs = {groupEng};
        Cursor cursor = query("SUBJECT", null, selection, selectionArgs, null);
        String[] titleEng = new String[cursor.getCount()];
        int j = 0;
        if (cursor.moveToFirst())
            do {
                titleEng[j] = cursor.getString(4);
                j++;
            } while (cursor.moveToNext());
        cursor.close();
        if (titleEng.length != 0)
            for (String title : titleEng)
                sqlDB.execSQL("drop table if exists " + groupEng + "_" + title);
        sqlDB.delete("SUBJECT", "GroupEng = '" + groupEng + "'", null);
        sqlDB.delete("GROUPS", "GroupEng = '" + groupEng + "'", null);
        sqlDB.close();
    }

    public void deleteStudent(String groupEng, String surname) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        String[] wc = {surname};
        sqlDB.delete(groupEng, "Surname = ?", wc);

        String selection = "GroupEng = ?";
        String[] selectionArgs = {groupEng};
        Cursor cursor = query("SUBJECT", null, selection, selectionArgs, null);
        String[] titleEng = new String[cursor.getCount()];
        int j = 0;
        if (cursor.moveToFirst())
            do {
                titleEng[j] = cursor.getString(4);
                j++;
            } while (cursor.moveToNext());
        cursor.close();
        cursor = query(groupEng, null, null, null, null);
        StringBuilder str = new StringBuilder("Date, Theme, DZ, Type");
        if (cursor.moveToFirst())
            do {
                if (!cursor.getString(0).equals(surname))
                    str.append(", ").append(cursor.getString(3));
            } while (cursor.moveToNext());
        cursor.close();
        if (titleEng.length != 0)
            for (String title : titleEng)
                try {
                    sqlDB.execSQL("create table backup as select " + str.toString() + " from " + groupEng + "_" + title);
                    sqlDB.execSQL("drop table " + groupEng + "_" + title);
                    sqlDB.execSQL("alter table backup rename to " + groupEng + "_" + title);
                } catch (SQLException ignored) {
                }
        sqlDB.close();
    }

    public void createLesson(String groupEng, String titleEng, String studentsEng[], String date, String theme, String dz, String type) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        StringBuilder stringBuilder;
        stringBuilder = new StringBuilder("create table if not exists " + groupEng + "_" + titleEng + " ( Date text, Theme text, DZ text, Type text");
        for (String surname : studentsEng)
            stringBuilder.append(", ").append(surname).append(" text");
        sqlDB.execSQL(stringBuilder.toString() + ");");

        ContentValues initialValues = new ContentValues();
        initialValues.put("Date", date);
        initialValues.put("Theme", theme);
        initialValues.put("DZ", dz);
        initialValues.put("Type", type);
        for (String surname : studentsEng)
            initialValues.put(surname, "0");
        sqlDB.insert(groupEng + "_" + titleEng, null, initialValues);
        sqlDB.close();
    }

    public void setMarks(String students[], String marks[], String groupEng, String titleEng, String date, String type) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        for (int i = 0; i < students.length; i++) {
            sqlDB.execSQL("update " + groupEng + "_" + titleEng + " set " + students[i] + " = '" + marks[i] + "' where Date = '" + date + "' and Type = '" + type + "'");
            Log.d("mLog", "update " + groupEng + "_" + titleEng + " set " + students[i] + " = '" + marks[i] + "' where Date = " + date + "and Type = " + type);
        }
        sqlDB.close();
    }

    public void deleteLesson(String groupEng, String titleEng, String date, String type) {
        sqlDB = SQLiteDatabase.openDatabase(DB_PATH + DB_NAME, null, SQLiteDatabase.OPEN_READWRITE);
        String[] wc = {date, type};
        sqlDB.delete(groupEng + "_" + titleEng, "Date = ? AND Type = ?", wc);
        sqlDB.close();
    }

}