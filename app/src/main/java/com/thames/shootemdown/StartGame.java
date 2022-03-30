package com.thames.shootemdown;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class StartGame extends AppCompatActivity {

    GameView gameView;
    MediaPlayer bgmusic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Sets Application to full screen by removing action bar
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        gameView = new GameView(this);
        setContentView(gameView);

        bgmusic = MediaPlayer.create(this, R.raw.bgmusic);
        if(bgmusic != null){
            bgmusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(bgmusic!=null){
            bgmusic.stop();
            bgmusic.release();
        }
    }
}
