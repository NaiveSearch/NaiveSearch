package org.happyhorse.naivesearch.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ClipData;
import android.content.ClipboardManager;
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
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.config.Config;
import org.happyhorse.naivesearch.databinding.LayoutHomeContainerBinding;

import java.util.Calendar;
import java.util.Locale;

/**
 * A {@link AppCompatActivity} subclass.
 * This activity is the start activity which contains the home page and slid menu
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;   //TODO:not used

    //engine selection
    private int engine = Config.BAIDU_SEARCH; //used to decide what search engine will be used

    //preferences
    private SharedPreferences prefs;    //use to load preferences
    private SharedPreferences appSettingsPrefs;    //use to load preferences
    private int total_block_ad = 0;   //a total of blocked advertisements
    private int total_search_time = 0;  //a total of search times
    private int current_language = Config.ENGLISH; //the identifier of selected language
    private int theme_selection = 0;    //the identifier of selected theme//TODO:not used
    TextView text;  //TODO:not used
    //search parameters
    private String QUERY;   //the key word input//TODO:not used

    //view
    private ImageButton engine_selection_button;    //a image button used to select search engine
    private ImageButton search_button;  //a button start search function
    private ImageButton clean_keyword_button;  //a button used to clean text in the input box//TODO:not used
    private ImageButton language_selection_button;  //used to select what language will display//TODO:not used
    private ImageButton theme_selection_button; //used to select theme//TODO:not used
    private ImageButton reset_recording_button;  //TODO:not used
    private ImageButton logo_image_button;  //show the application logo
    private ImageButton engine_baidu_button;    //select the Baidu engine
    private ImageButton engine_bing_button;     //select the Bing engine

    private TextView language_selection_textview;   //TODO:not used
    private TextView theme_selection_textview;  //TODO:not used
    private TextView keyword_textview; //key word input box
    private TextView total_blocked_ad_textview; //show blocked advertisements information
    private TextView total_searched_textview;   //show search times information

    private TextView engine_selection_fragment; //TODO:not used
    public static final String FIRST_START = "FirstStart";  //TODO:need comments
    public static final String NIGHT_MODE = "NightMode";    //theme name of night mode
    public static final String PREF = "AppSettingsPrefs";   //identifier of settings preferences
    private static final String TAG = "MainActivity";   //TODO:not used
    final String[] CHANNEL_IDS = {"CH1", "CH2", "CH3"};

    /**
     * Override onCreate() method
     * Do variables initialization, listener adding, preferences loading and fault value assignment
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Calendar ca = Calendar.getInstance();
        int hour=ca.get(Calendar.HOUR);
        appSettingsPrefs = HomeActivity.this.getSharedPreferences(PREF, 0);
        final boolean isNightModeOn = appSettingsPrefs.getBoolean(NIGHT_MODE, true);
        boolean isFirstStart = appSettingsPrefs.getBoolean(FIRST_START, true);

        super.onCreate(savedInstanceState);
        createNotificationChannel();

        //load preferences and set language to display
        loadPreferences();
        setCurrentLanguage(Config.LOCALES[current_language]);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isFirstStart) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }

        //set the layout which will be shown
        org.happyhorse.naivesearch.databinding.LayoutHomeContainerBinding binding = LayoutHomeContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        initialization();   //initialize variables
        listenerAdding();   //add event listener for components
        //loadPreferences();
        setDefaultText();   //assign default value for variables

        //add sidebar into home page
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.

//        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
//        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
//        NavigationUI.setupWithNavController(navigationView, navController);

        //获取抽屉布局并设置监听//TODO:Chinese
        //get drawer layout and set listening
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        //ActionBarDrawerToggle  是 DrawerLayout.DrawerListener实现，和 NavigationDrawer搭配使用，推荐用这个方法，符合Android design规范。//TODO:Chinese
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        //获取导航视图并设置菜单监听，对应上面实现的接口//TODO:Chinese
        //get the Navigation view and set menu listening, corresponding to the interface implemented above
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    /**
     * When the navigation bar menu is clicked, judge and respond according to the ID
     *
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {//当导时航栏菜单被单击时，根据ID判断并给出响应//TODO:Chinese
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id == R.id.nav_language) {   //change language function

            SharedPreferences.Editor editor = prefs.edit();
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            //when supporting more languages, the logic needs to be modified here
            switch (current_language) {
                case Config.ENGLISH:
                    editor.putInt("language", Config.CHINESE);
                    editor.commit();
                    setCurrentLanguage(Locale.CHINESE);
                    System.out.println("Changing to CHINESE");
                    startActivity(intent);
                    break;
                case Config.CHINESE:
                    editor.putInt("language", Config.ENGLISH);
                    editor.commit();
                    setCurrentLanguage(Locale.ENGLISH);
                    System.out.println("Changing to English");
                    startActivity(intent);
                    break;
                default:
                    startActivity(intent);
                    break;
            }
//             Handle the camera action
        } else if (id == R.id.nav_theme) { //change theme function
            //final SharedPreferences appSettingsPrefs = HomeActivity.this.getSharedPreferences(PREF, 0);
            SharedPreferences.Editor editor = appSettingsPrefs.edit();
            //Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            final boolean isNightModeOn = appSettingsPrefs.getBoolean(NIGHT_MODE, true);
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean(FIRST_START, false);
                editor.putBoolean(NIGHT_MODE, false);
                editor.apply();
                recreate();
                sendNotification(getString(R.string.Day_Time), getString(R.string.Theme_enabled), 0, 1);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean(FIRST_START, false);
                editor.putBoolean(NIGHT_MODE, true);
                editor.apply();
                recreate();
                sendNotification(getString(R.string.Night_Time), getString(R.string.Theme_enabled), 1, 2);
            }

        } else if (id == R.id.nav_reset) {   //reset the statistic information to be 0
            prefs = getSharedPreferences("statistic", MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putInt("blockedAD", 0);
            editor.putInt("searchTime", 0);
            editor.apply();
            Intent intent = new Intent(new Intent(HomeActivity.this, HomeActivity.class));
            startActivity(intent);

        } else if (id == R.id.nav_share) {  //share the application with friends
            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clipData = ClipData.newPlainText("simple text", Config.DOWNLOAD_LINK);
            sendNotification("Sharing to friends", "App link has been pasted to clipboard", 2, 3);
            clipboardManager.setPrimaryClip(clipData);
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void sendNotification(String title, String text, int channel, int id) {
        Notification.Builder builder =
                new Notification.Builder(HomeActivity.this, CHANNEL_IDS[channel]);
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle(title);
        builder.setContentText(text);
        Notification notification = builder.build();
        NotificationManager manager =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(id, notification);
    }

    private void createNotificationChannel() {
        NotificationChannel channel1 = new NotificationChannel(CHANNEL_IDS[0],
                "Channel1(channel name)",
                NotificationManager.IMPORTANCE_HIGH);
        channel1.setDescription("description 1");

        NotificationChannel channel2 = new NotificationChannel(CHANNEL_IDS[1],
                "Channel2(channel name)",
                NotificationManager.IMPORTANCE_HIGH);
        channel2.setDescription("description 2");

        NotificationChannel channel3 = new NotificationChannel(CHANNEL_IDS[2],
                "Channel3(channel name)",
                NotificationManager.IMPORTANCE_HIGH);
        channel3.setDescription("description 3");

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel1);
        notificationManager.createNotificationChannel(channel2);
        notificationManager.createNotificationChannel(channel3);
    }

    /**
     * Load and assign the statistic information to variables
     */
    private void loadPreferences() {
        prefs = getSharedPreferences("statistic", MODE_PRIVATE);
        total_block_ad = prefs.getInt("blockedAD", 0);    //number of blocked advertisements
        total_search_time = prefs.getInt("searchTime", 0);  //number of searched times
        current_language = prefs.getInt("language", Config.ENGLISH);   //identifier of last applied language
        System.out.println(current_language);
    }

    /**
     * Assign the default value to variables
     */
    private void setDefaultText() {
        updateSummaryInfo();    //set the statistic information
        keyword_textview.setText("");  //reset the key word input box
    }

    /**
     * Update the statistic information in text view
     */
    @SuppressLint("SetTextI18n")
    private void updateSummaryInfo() {
        //update total blocked advertisements information
        total_blocked_ad_textview.setText(getString(R.string.TOTAL_BLOCKED_AD) + " " + String.valueOf(total_block_ad));
        //update total searched times information
        total_searched_textview.setText(getString(R.string.TOTAL_SEARCH_TIME_STRING) + " " + String.valueOf(total_search_time));
    }

    /**
     * Add listener to components
     */
    private void listenerAdding() {

        //shown or hide all supported search engines
        engine_selection_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO:not used
//                getSupportFragmentManager()    //
//                        .beginTransaction()
//                        .add(R.id.fragment_container_engines,new EngineSelectFragment())   // 此处的R.id.fragment_container是要盛放fragment的父容器
//                        .commit();
                View fragment = findViewById(R.id.fragment_engines);
                if (fragment.getVisibility() == View.VISIBLE) { //show all search engines
                    fragment.setVisibility(View.INVISIBLE);
                    Log.d("TAG", "onClick: set invisible");
                } else {    //hide all search engines
                    fragment.setVisibility(View.VISIBLE);
                    Log.d("TAG", "onClick: set visible");
                }
            }
        });


        //start searching action when key word input text has"search" operation
        EditText editText = (EditText) findViewById(R.id.key_word_TextView);
        Intent intent = new Intent(new Intent(HomeActivity.this, SearchActivity.class));
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //when the action key is "search" operation
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    System.out.println("Search key pressed");
                    Editable editableinput = editText.getText();    //get input key word
                    String input = "";
                    if (editableinput != null) input = editableinput.toString();

                    //start search the input key word
                    if (!input.equals("")) {
                        Bundle bundle = new Bundle();
                        bundle.putString("keyword", input); //key word will be searched
                        bundle.putInt("engine", engine);    //search engine will be used
                        bundle.putInt("page", 1);   //the page of search results
                        //pass search parameters to search activity
                        intent.putExtra("Message", bundle);
                        startActivity(intent);
                        return true;
                    }
                }
                return false;
            }
        });

        //start searching action when search button is clicked
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable editableinput = editText.getText();    //get input key word
                String input = "";
                if (editableinput != null) input = editableinput.toString();

                //start search the input key word
                if (!input.equals("")) {
                    Bundle bundle = new Bundle();
                    bundle.putString("keyword", input); //key word will be searched
                    bundle.putInt("engine", engine);    //search engine will be used
                    bundle.putInt("page", 1);   //the page of search results
                    //pass search parameters to search activity
                    intent.putExtra("Message", bundle);
                    startActivity(intent);
                }
            }
        });

        //set search to be Baidu when Baidu button is clicked
        engine_baidu_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the current selected engine image to be Baidu image
                engine_selection_button.setImageResource(R.mipmap.ic_baidu_engine_foreground);
                engine = Config.BAIDU_SEARCH;
                View fragment = findViewById(R.id.fragment_engines);
                fragment.setVisibility(View.INVISIBLE);
                Log.d("TAG", "onClick: set invisible");
            }
        });

        //set search to be Bing when Bing button is clicked
        engine_bing_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the current selected engine image to be Bing image
                engine_selection_button.setImageResource(R.mipmap.ic_bing_engine);
                engine = Config.BING_SEARCH;
                View fragment = findViewById(R.id.fragment_engines);
                fragment.setVisibility(View.INVISIBLE);
                Log.d("TAG", "onClick: set invisible");
            }
        });


    }

    /**
     * Used to first assign values to variables, do most of views by {@link androidx.appcompat.app.AppCompatActivity#findViewById}
     */
    @SuppressLint("ResourceType")
    private void initialization() {
        //TODO: initialize variables here
        engine_selection_button = findViewById(R.id.engine_selection_imageButton);
        search_button = findViewById(R.id.search_imageButton);
        clean_keyword_button = null;
        language_selection_button = null;
        theme_selection_button = null;
        reset_recording_button = null;
        logo_image_button = findViewById(R.id.logo_imageButton);
        engine_baidu_button = findViewById(R.id.imageButton_baidu);
        engine_bing_button = findViewById(R.id.imageButton_bing);

        language_selection_textview = null;
        theme_selection_textview = null;
        keyword_textview = findViewById(R.id.key_word_TextView);
        total_blocked_ad_textview = findViewById(R.id.total_blocked_ad_textview);
        total_searched_textview = findViewById(R.id.total_searched_textview);


        //set the size of buttons of fragment same as select button
        int height = engine_selection_button.getLayoutParams().height;
        engine_baidu_button.setLayoutParams(new LinearLayout.LayoutParams(height, height));
        engine_bing_button.setLayoutParams(new LinearLayout.LayoutParams(height, height));
    }

    /**
     * Set language according to given locale object
     *
     * @param locale a Locale object contains lang and country
     */
    @SuppressWarnings("deprecation")
    private void setCurrentLanguage(Locale locale) {
        //load the configuration object from resources
        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        DisplayMetrics displayMetrics = resources.getDisplayMetrics();

        //change the locale in configuration to given locale value
        configuration.setLocale(locale);
        getApplicationContext().createConfigurationContext(configuration);

        //update configuration to refresh selected language
        resources.updateConfiguration(configuration, displayMetrics);
    }

}