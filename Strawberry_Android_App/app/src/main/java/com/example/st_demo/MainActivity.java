//package com.example.st_demo;
//
//import android.os.Bundle;
//import android.view.MenuItem;
//
//import androidx.annotation.NonNull;
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.fragment.app.Fragment;
//import androidx.fragment.app.FragmentManager;
//import androidx.fragment.app.FragmentTransaction;
//
//import com.google.android.material.bottomnavigation.BottomNavigationView;
//
//public class MainActivity extends AppCompatActivity {
//
//    private BottomNavigationView bottomNavigationView;
//    private Fragment firstFragment;
//    private Fragment secondFragment;
//    private Fragment thirdFragment;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        bottomNavigationView = findViewById(R.id.bottomNavigationView);
//
//        // Initialize fragments
//        firstFragment = new FirstFragment();
//        secondFragment = new SecondFragment();
//        thirdFragment = new ThirdFragment();
//
//        // Set default fragment
//        loadFragment(firstFragment);
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//       if (item.getItemId()== R.id.home) {
//           loadFragment(firstFragment);
//           return true;
//       }
//        else if (item.getItemId()== R.id.person) {
//           loadFragment(secondFragment);
//           return true;
//       }
//        else if (item.getItemId()== R.id.settings) {
//           loadFragment(thirdFragment);
//           return true;
//       }
//            else{
//                return super.onOptionsItemSelected(item);
//        }
//    }
//
//    private void loadFragment(Fragment fragment) {
//        // Load fragment
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        transaction.replace(R.id.flFragment, fragment);
//        transaction.addToBackStack(null);
//        transaction.commit();
//    }
//}


package com.example.st_demo;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
//import android.widget.Toolbar;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;
//    private Fragment firstFragment;
    private Fragment secondFragment;
    private Fragment thirdFragment;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    private ImageView toolbarIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        toolbar = findViewById(R.id.toolbar);


        // Set toolbar as action bar
        setSupportActionBar(toolbar);

        // Initialize fragments
//        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();

        thirdFragment = new ThirdFragment();

        // Set default fragment
//        loadFragment(firstFragment);
        loadFragment(secondFragment);


        // Set listener for bottom navigation view
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                if (item.getItemId() == R.id.person) {
//                    loadFragment(firstFragment);
//                    return true;
//                } else
                    if (item.getItemId() == R.id.home) {
                    loadFragment(secondFragment);
                    return true;
                } else if (item.getItemId() == R.id.settings) {
                    loadFragment(thirdFragment);
                    return true;
                }
                onBackPressed();
                return false;

            }
        });

    }

    @Override
    public void onBackPressed() {
        // Check if the current fragment is the secondFragment
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.flFragment);
        if (currentFragment instanceof SecondFragment) {
            // If it is the secondFragment, close the app
            bottomNavigationView.setSelectedItemId(R.id.home);
            finish();
        } else {
            // If not, let the system handle the back button as usual
            super.onBackPressed();
            bottomNavigationView.setSelectedItemId(R.id.home);
        }
    }


    private void loadFragment(Fragment fragment) {
        // Load fragment
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(R.id.flFragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }

}

