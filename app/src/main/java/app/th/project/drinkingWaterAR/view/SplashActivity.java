package app.th.project.drinkingWaterAR.view;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import app.th.project.drinkingWaterAR.R;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class SplashActivity extends AppCompatActivity {
    private Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
//        Log.d("logged the splash activity","splash reached");
        ScheduledExecutorService worker = Executors.newSingleThreadScheduledExecutor();
        Runnable runnable = new Runnable() {
            public void run() {
                intent = new Intent(SplashActivity.this, MainActivity.class);
                SplashActivity.this.startActivity(intent);
                SplashActivity.this.finish();
    }
};
        worker.schedule(runnable, 1, TimeUnit.SECONDS);
    }
}