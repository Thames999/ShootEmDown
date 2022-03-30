package com.thames.shootemdown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Random;

public class PinkBird {

    Bitmap[] pinkbird = new Bitmap[4];
    int birdX, birdY, velocity, birdFrame;
    Random random;

    public PinkBird(Context context) {
        pinkbird[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.pinkbird1);
        pinkbird[1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.pinkbird2);
        pinkbird[2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.pinkbird3);
        pinkbird[3] = BitmapFactory.decodeResource(context.getResources(),R.drawable.pinkbird4);

        random = new Random();

        birdFrame = 0;
        resetPosition();
    }

    public Bitmap getBirdBitmap() {
        return pinkbird[birdFrame];
    }

    public int getBirdWidth(){
        return pinkbird[0].getWidth();
    }

    public int getBirdHeight(){
        return pinkbird[0].getHeight();
    }

    public void resetPosition() {
        birdX = GameView.dWidth + random.nextInt(1200);
        birdY = random.nextInt(300);
        velocity = 14 + random.nextInt(10);
    }
}
