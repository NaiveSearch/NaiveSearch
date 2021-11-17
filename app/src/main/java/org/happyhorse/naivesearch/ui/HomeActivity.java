package org.happyhorse.naivesearch.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import org.happyhorse.naivesearch.R;

public class HomeActivity extends AppCompatActivity {

    protected void jumpToWebpage() {
        Intent jump = new Intent(this, WebViewActivity.class);
        startActivity(jump);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setContentView(R.layout.activity_main);
        super.onCreate(savedInstanceState);
        Button b = findViewById(R.id.jump);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jumpToWebpage();
            }
        });
    }
}