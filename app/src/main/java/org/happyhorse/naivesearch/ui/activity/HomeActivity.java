package org.happyhorse.naivesearch.ui.activity;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.databinding.LayoutHomeContainerBinding;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity {
    private AppBarConfiguration mAppBarConfiguration;
    private LayoutHomeContainerBinding binding;

    //engine selection
    private int engine=0;

    //preferences
    private SharedPreferences prefs;
    private int TOTAL_BLOCKED_AD = 0;
    private int TOTAL_SEARCH_TIME = 0;
    private int LANGUAGE_SELECTION=0;

    //search parameters
    private String QUERY;


    //view
    private ImageButton ENGINE_SELECTION_BUTTON;
    private ImageButton SEARCH_BUTTON;
    private ImageButton CLEAN_KEY_WORD_BUTTON;
    private ImageButton LANGUAGE_SELECTION_BUTTON;
    private ImageButton THEME_SELECTION_BUTTON;
    private ImageButton REST_RECORDING_BUTTON;
    private ImageButton LOGO_IMAGE_BUTTON;
    private ImageButton ENGINE_BAIDU_BUTTON;
    private ImageButton ENGINE_BING_BUTTON;

    private TextView LANGUAGE_SELECTION_TEXTVIEW;
    private TextView THEME_SELECTION_TEXTVIEW;
    private TextView KEY_WORD_TEXTVIEW;
    private TextView TOTAL_BLOCKED_AD_TEXTVIEW;
    private TextView TOTAL_SEARCHED_TEXTVIEW;


    private TextView ENGINE_SELECTION_FRAGMENT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setLANGUAGE_SELECTION(Locale.CHINESE);
        binding = LayoutHomeContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initialization();
        listenerAdding();
        loadPreferences();
        setDefaultText();

        setSupportActionBar(binding.appBarMain.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_language, R.id.nav_reset, R.id.nav_theme)
                .setOpenableLayout(drawer)
                .build();






//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
    }

    private void loadPreferences() {
        prefs= getPreferences(MODE_PRIVATE);
        TOTAL_BLOCKED_AD = prefs.getInt("blockedAD", 0);
        TOTAL_SEARCH_TIME = prefs.getInt("searchTime", 0);
        LANGUAGE_SELECTION=prefs.getInt("language",0);

    }


    private void setDefaultText() {
        updateSummaryInfo();
        KEY_WORD_TEXTVIEW.setText("");
    }

    @SuppressLint("SetTextI18n")
    private void updateSummaryInfo() {
        TOTAL_BLOCKED_AD_TEXTVIEW.setText(getString(R.string.TOTAL_BLOCKED_AD)+" "+String.valueOf(TOTAL_BLOCKED_AD));
        TOTAL_SEARCHED_TEXTVIEW.setText(getString(R.string.TOTAL_SEARCH_TIME_STRING)+" "+String.valueOf(TOTAL_SEARCH_TIME));
    }

    private void listenerAdding() {
        //TODO: add listener to views
        ENGINE_SELECTION_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: do selection engines function here
//                getSupportFragmentManager()    //
//                        .beginTransaction()
//                        .add(R.id.fragment_container_engines,new EngineSelectFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
//                        .commit();
                View fragment = findViewById(R.id.fragment_engines);
                if (fragment.getVisibility() == View.VISIBLE) {
                    fragment.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "onClick: set invisible");
                } else {
                    fragment.setVisibility(View.VISIBLE);
                    Log.d("TAG", "onClick: set visible");
                }
            }
        });


        SEARCH_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: do selection engines function here
                EditText editText=(EditText) findViewById(R.id.key_word_TextView);
                Intent intent=new Intent(new Intent(HomeActivity.this, SearchActivity.class));
                String input=editText.getText().toString();
                if(!input.equals("")){
                    Bundle bundle=new Bundle();
                    bundle.putString("keyword",input);
                    bundle.putInt("engine",engine);
                    bundle.putInt("page",1);
                    intent.putExtra("Message",bundle);
                }

                startActivity(intent);
            }
        });

        ENGINE_BAIDU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_baidu_engine_foreground);
                engine=0;
                //TODO: do engine parameter here

            }
        });
        ENGINE_BING_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_bing_engine_foreground);
                engine=1;
                //TODO: do engine parameter here
            }
        });
    }


    @SuppressLint("ResourceType")
    private void initialization() {
        //TODO: initialize variables here
        ENGINE_SELECTION_BUTTON = findViewById(R.id.engine_selection_imageButton);
        SEARCH_BUTTON = findViewById(R.id.search_imageButton);
        CLEAN_KEY_WORD_BUTTON = null;
        LANGUAGE_SELECTION_BUTTON = null;
        THEME_SELECTION_BUTTON = null;
        REST_RECORDING_BUTTON = null;
        LOGO_IMAGE_BUTTON = findViewById(R.id.logo_imageButton);
        ENGINE_BAIDU_BUTTON = findViewById(R.id.imageButton_baidu);
        ENGINE_BING_BUTTON = findViewById(R.id.imageButton_bing);

        LANGUAGE_SELECTION_TEXTVIEW = null;
        THEME_SELECTION_TEXTVIEW = null;
        KEY_WORD_TEXTVIEW = findViewById(R.id.key_word_TextView);
        TOTAL_BLOCKED_AD_TEXTVIEW = findViewById(R.id.total_blocked_ad_textview);
        TOTAL_SEARCHED_TEXTVIEW = findViewById(R.id.total_searched_textview);

        LOGO_IMAGE_BUTTON.setImageResource(R.raw.logo);


        //set the size of buttons of fragment same as select button
        int height = ENGINE_SELECTION_BUTTON.getLayoutParams().height;
        ENGINE_BAIDU_BUTTON.setLayoutParams(new LinearLayout.LayoutParams(height, height));
        ENGINE_BING_BUTTON.setLayoutParams(new LinearLayout.LayoutParams(height, height));
    }

    @SuppressWarnings("deprecation")
    private void setLANGUAGE_SELECTION(Locale locale){
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1){
            configuration.setLocale(locale);
        }else{
            configuration.locale=locale;
        }
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N){
            getApplicationContext().createConfigurationContext(configuration);
        }else {
            resources.updateConfiguration(configuration,displayMetrics);
        }
    }
}