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
import android.text.Editable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.databinding.LayoutHomeContainerBinding;

import java.util.Locale;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{
    private AppBarConfiguration mAppBarConfiguration;
    private LayoutHomeContainerBinding binding;

    //engine selection
    private int engine = 0;

    //preferences
    private SharedPreferences prefs;
    private int TOTAL_BLOCKED_AD = 0;
    private int TOTAL_SEARCH_TIME = 0;
    private int LANGUAGE_SELECTION = 0;
    TextView text;
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
        loadPreferences();
        if (LANGUAGE_SELECTION==0){
            setLANGUAGE_SELECTION(Locale.ENGLISH);
        }else if(LANGUAGE_SELECTION==1){
            setLANGUAGE_SELECTION(Locale.CHINESE);
        }
        binding = LayoutHomeContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initialization();
        listenerAdding();
        //loadPreferences();
        setDefaultText();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);
        //获取抽屉布局并设置监听
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle  是 DrawerLayout.DrawerListener实现，和 NavigationDrawer搭配使用，推荐用这个方法，符合Android design规范。
        ActionBarDrawerToggle toggle =new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        //获取导航视图并设置菜单监听，对应上面实现的接口
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//当导时航栏菜单被单击时，根据ID判断并给出响应
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id == R.id.nav_language) {
            finish();
            SharedPreferences.Editor editor=prefs.edit();
            Intent intent=new Intent(HomeActivity.this,HomeActivity.class);
            switch (LANGUAGE_SELECTION) {
                case 0:
                    editor.putInt("language",1);
                    editor.commit();
                    setLANGUAGE_SELECTION(Locale.CHINESE);
                    System.out.println("Changing to CHINESE");
                    startActivity(intent);
                    break;
                case 1:
                    editor.putInt("language",0);
                    editor.commit();
                    setLANGUAGE_SELECTION(Locale.ENGLISH);
                    System.out.println("changing to English");
                    startActivity(intent);
                    break;
                default:
                    startActivity(intent);
                    break;
            }
//             Handle the camera action
        }else if(id == R.id.nav_theme){

        } else if (id == R.id.nav_reset){

        }  else if (id == R.id.nav_share){

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    private void loadPreferences() {
        prefs = getPreferences(MODE_PRIVATE);
        TOTAL_BLOCKED_AD = prefs.getInt("blockedAD", 0);
        TOTAL_SEARCH_TIME = prefs.getInt("searchTime", 0);
        LANGUAGE_SELECTION = prefs.getInt("language", 1);
        System.out.println(LANGUAGE_SELECTION);
    }


    private void setDefaultText() {
        updateSummaryInfo();
        KEY_WORD_TEXTVIEW.setText("");
    }

    @SuppressLint("SetTextI18n")
    private void updateSummaryInfo() {
        TOTAL_BLOCKED_AD_TEXTVIEW.setText(getString(R.string.TOTAL_BLOCKED_AD) + " " + String.valueOf(TOTAL_BLOCKED_AD));
        TOTAL_SEARCHED_TEXTVIEW.setText(getString(R.string.TOTAL_SEARCH_TIME_STRING) + " " + String.valueOf(TOTAL_SEARCH_TIME));
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
                EditText editText = (EditText) findViewById(R.id.key_word_TextView);
                Intent intent = new Intent(new Intent(HomeActivity.this, SearchActivity.class));
                //String input = editText.getText().toString();
                Editable editableinput =editText.getText();
                String input="";
                if(editableinput!=null) input=editableinput.toString();
                if (!input.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("keyword", input);
                    bundle.putInt("engine", engine);
                    bundle.putInt("page", 1);
                    intent.putExtra("Message", bundle);
                    startActivity(intent);
                }
            }
        });

        ENGINE_BAIDU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_baidu_engine_foreground);
                engine = 0;
                //TODO: do engine parameter here

            }
        });
        ENGINE_BING_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_bing_engine_foreground);
                engine = 1;
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
    private void setLANGUAGE_SELECTION(Locale locale) {
        System.out.println("resetting"+locale);
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();
        configuration.setLocale(locale);
        getApplicationContext().createConfigurationContext(configuration);
        resources.updateConfiguration(configuration,displayMetrics);
    }

}