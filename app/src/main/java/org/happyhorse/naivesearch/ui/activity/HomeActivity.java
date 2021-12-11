package org.happyhorse.naivesearch.ui.activity;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.ui.AppBarConfiguration;

import com.google.android.material.navigation.NavigationView;

import org.happyhorse.naivesearch.R;
import org.happyhorse.naivesearch.databinding.LayoutHomeContainerBinding;

import java.util.Locale;

/**
 * A {@link AppCompatActivity} subclass.
 * This activity is the start activity which contains the home page and slid menu
 */
public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private AppBarConfiguration mAppBarConfiguration;   //TODO:not used
    private LayoutHomeContainerBinding binding;

    //engine selection
    private int engine = 0; //used to decide what search engine will be used

    //preferences
    private SharedPreferences prefs;    //use to load preferences
    private int TOTAL_BLOCKED_AD = 0;   //a total of blocked advertisements
    private int TOTAL_SEARCH_TIME = 0;  //a total of search times
    private int LANGUAGE_SELECTION = 0; //the identifier of selected language
    private int THEME_SELECTION = 0;    //the identifier of selected theme//TODO:not used
    TextView text;  //TODO:not used
    //search parameters
    private String QUERY;   //the key word input//TODO:not used

    //view
    private ImageButton ENGINE_SELECTION_BUTTON;    //a image button used to select search engine
    private ImageButton SEARCH_BUTTON;  //a button start search function
    private ImageButton CLEAN_KEY_WORD_BUTTON;  //a button used to clean text in the input box//TODO:not used
    private ImageButton LANGUAGE_SELECTION_BUTTON;  //used to select what language will display//TODO:not used
    private ImageButton THEME_SELECTION_BUTTON; //used to select theme//TODO:not used
    private ImageButton REST_RECORDING_BUTTON;  //TODO:not used
    private ImageButton LOGO_IMAGE_BUTTON;  //show the application logo
    private ImageButton ENGINE_BAIDU_BUTTON;    //select the Baidu engine
    private ImageButton ENGINE_BING_BUTTON;     //select the Bing engine

    private TextView LANGUAGE_SELECTION_TEXTVIEW;   //TODO:not used
    private TextView THEME_SELECTION_TEXTVIEW;  //TODO:not used
    private TextView KEY_WORD_TEXTVIEW; //key word input box
    private TextView TOTAL_BLOCKED_AD_TEXTVIEW; //show blocked advertisements information
    private TextView TOTAL_SEARCHED_TEXTVIEW;   //show search times information

    private TextView ENGINE_SELECTION_FRAGMENT; //TODO:not used
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
        super.onCreate(savedInstanceState);

        createNotificationChannel();

        //load preferences and set language to display
        loadPreferences();
        if (LANGUAGE_SELECTION == 0) {
            setLANGUAGE_SELECTION(Locale.ENGLISH);
        } else if (LANGUAGE_SELECTION == 1) {
            setLANGUAGE_SELECTION(Locale.CHINESE);
        }

        //set the layout which will be shown
        binding = LayoutHomeContainerBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //load the them selection information from shared preferences, and set the theme for last use
        final SharedPreferences appSettingsPrefs = HomeActivity.this.getSharedPreferences(PREF, 0);
        final boolean isNightModeOn = appSettingsPrefs.getBoolean(NIGHT_MODE, true);
        boolean isFirstStart = appSettingsPrefs.getBoolean(FIRST_START, true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P && isFirstStart) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
        } else {
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
        }
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
            finish();
            SharedPreferences.Editor editor = prefs.edit();
            Intent intent = new Intent(HomeActivity.this, HomeActivity.class);
            switch (LANGUAGE_SELECTION) {
                case 0:
                    editor.putInt("language", 1);
                    editor.commit();
                    setLANGUAGE_SELECTION(Locale.CHINESE);
                    System.out.println("Changing to CHINESE");
                    startActivity(intent);
                    break;
                case 1:
                    editor.putInt("language", 0);
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
        } else if (id == R.id.nav_theme) { //change theme function
            final SharedPreferences appSettingsPrefs = HomeActivity.this.getSharedPreferences(PREF, 0);
            SharedPreferences.Editor editor = appSettingsPrefs.edit();
            final boolean isNightModeOn = appSettingsPrefs.getBoolean(NIGHT_MODE, true);
            if (isNightModeOn) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                editor.putBoolean(FIRST_START, false);
                editor.putBoolean(NIGHT_MODE, false);
                editor.apply();
                recreate();
                sendNotification("Day Time", "Theme enabled!", 0, 1);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                editor.putBoolean(FIRST_START, false);
                editor.putBoolean(NIGHT_MODE, true);
                editor.apply();
                recreate();
                sendNotification("Night Time", "Theme enabled!", 1, 2);
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
            ClipData clipData = ClipData.newPlainText("simple text", "https://github.com/NaiveSearch/NaiveSearch/tree/dev/app/src/main");
            System.out.println("Pasted to clipboard");
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

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
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
    }

    /**
     * Load and assign the statistic information to variables
     */
    private void loadPreferences() {
        prefs = getSharedPreferences("statistic", MODE_PRIVATE);
        TOTAL_BLOCKED_AD = prefs.getInt("blockedAD", 0);    //number of blocked advertisements
        TOTAL_SEARCH_TIME = prefs.getInt("searchTime", 0);  //number of searched times
        LANGUAGE_SELECTION = prefs.getInt("language", 1);   //identifier of last applied language
        System.out.println(LANGUAGE_SELECTION);
    }

    /**
     * Assign the default value to variables
     */
    private void setDefaultText() {
        updateSummaryInfo();    //set the statistic information
        KEY_WORD_TEXTVIEW.setText("");  //reset the key word input box
    }

    /**
     * Update the statistic information in text view
     */
    @SuppressLint("SetTextI18n")
    private void updateSummaryInfo() {
        //update total blocked advertisements information
        TOTAL_BLOCKED_AD_TEXTVIEW.setText(getString(R.string.TOTAL_BLOCKED_AD) + " " + String.valueOf(TOTAL_BLOCKED_AD));
        //update total searched times information
        TOTAL_SEARCHED_TEXTVIEW.setText(getString(R.string.TOTAL_SEARCH_TIME_STRING) + " " + String.valueOf(TOTAL_SEARCH_TIME));
    }

    /**
     * Add listener to components
     */
    private void listenerAdding() {

        //shown or hide all supported search engines
        ENGINE_SELECTION_BUTTON.setOnClickListener(new View.OnClickListener() {
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
        SEARCH_BUTTON.setOnClickListener(new View.OnClickListener() {
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
        ENGINE_BAIDU_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the current selected engine image to be Baidu image
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_baidu_engine_foreground);
                engine = 0; //identifier of Baidu engine
                View fragment = findViewById(R.id.fragment_engines);
                fragment.setVisibility(View.INVISIBLE);
                Log.d("TAG", "onClick: set invisible");
            }
        });

        //set search to be Bing when Bing button is clicked
        ENGINE_BING_BUTTON.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //set the current selected engine image to be Bing image
                ENGINE_SELECTION_BUTTON.setImageResource(R.mipmap.ic_bing_engine);
                engine = 1; //identifier of Bing engine
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

    /**
     * Set language according to given locale object
     *
     * @param locale a Locale object contains lang and country
     */
    @SuppressWarnings("deprecation")
    private void setLANGUAGE_SELECTION(Locale locale) {
        System.out.println("resetting" + locale);
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