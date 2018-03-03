package spevakov.ejournal;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;

import spevakov.ejournal.activity.AddGroup;
import spevakov.ejournal.activity.AddStudent;
import spevakov.ejournal.activity.AddSubject;
import spevakov.ejournal.activity.ChangePassword;
import spevakov.ejournal.activity.DelActivity;
import spevakov.ejournal.fragments.fragmentBells;
import spevakov.ejournal.fragments.fragmentStudents;
import spevakov.ejournal.fragments.fragmentSubject;

import static spevakov.ejournal.UserActivity.login;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Fragment fragmentStudents = new fragmentStudents();
    Fragment fragmentBells = new fragmentBells();
    Fragment fragmentSubject = new fragmentSubject();

    public static ProgressBar progressSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        progressSave = (ProgressBar) findViewById(R.id.progressSave);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getFragmentManager().beginTransaction().add(R.id.container, fragmentSubject).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (login) {
            getMenuInflater().inflate(R.menu.main, menu);
            return true;
        } else return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.add_group:
                intent = new Intent(getApplicationContext(), AddGroup.class);
                startActivity(intent);
                break;
            case R.id.add_student:
                intent = new Intent(getApplicationContext(), AddStudent.class);
                startActivity(intent);
                break;
            case R.id.add_subject:
                intent = new Intent(getApplicationContext(), AddSubject.class);
                startActivity(intent);
                break;
            case R.id.delete:
                intent = new Intent(getApplicationContext(), DelActivity.class);
                startActivity(intent);
                break;
            case R.id.change_pass:
                intent = new Intent(getApplicationContext(), ChangePassword.class);
                startActivity(intent);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();

        switch (id) {
            case R.id.nav_subject:
                fragmentTransaction.replace(R.id.container, fragmentSubject);
                break;
            case R.id.nav_students:
                fragmentTransaction.replace(R.id.container, fragmentStudents);
                break;
            case R.id.nav_bells:
                fragmentTransaction.replace(R.id.container, fragmentBells);
                break;
            case R.id.nav_exit:
                Intent intent = new Intent(getApplicationContext(), UserActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
        }
        fragmentTransaction.commit();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
