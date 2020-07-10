package com.example.firebase;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.firebase.fragment.AccountFragment;
import com.example.firebase.fragment.FavoriteFragment;
import com.example.firebase.fragment.SearchFragment;
import com.example.firebase.fragment.addFragment;
import com.example.firebase.ui.home.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    BottomNavigationView bottomNavigationView;
    Fragment selectedFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.Bottom_navigation);

        bottomNavigationView.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment ()).commit();

    }

    private BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

            switch (menuItem.getItemId()){

                case R.id.nav_home:
                    selectedFragment= new HomeFragment();
                    break;

                case R.id.nav_search:
                    selectedFragment= new SearchFragment();
                    break;

                case R.id.nav_add:
                    selectedFragment= null;
                    startActivity(new Intent(MainActivity.this, PostActivity.class));
                    break;

                case R.id.nav_favorite:
                    selectedFragment= new FavoriteFragment();
                    break;


                case R.id.nav_account:
                    SharedPreferences.Editor editor = getSharedPreferences("PREFS", MODE_PRIVATE).edit();
                    editor.putString("Profiled", FirebaseAuth.getInstance().getCurrentUser().getUid());
                    editor.apply();

                    selectedFragment= new AccountFragment();
                    break;


            }
            if(selectedFragment != null){
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            }
            return false;

        }
    };
}