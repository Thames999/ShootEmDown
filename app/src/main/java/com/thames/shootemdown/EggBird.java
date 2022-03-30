package com.thames.shootemdown;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class EggBird extends PinkBird {

    Bitmap[] eggBird = new Bitmap[4];

    public EggBird(Context context) {
        super(context);

        eggBird[0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.eggbird0);
        eggBird[1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.eggbird1);
        eggBird[2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.eggbird2);
        eggBird[3] = BitmapFactory.decodeResource(context.getResources(),R.drawable.eggbird3);

        resetPosition();
    }

    @Override
    public Bitmap getBirdBitmap() {
        return eggBird[birdFrame];
    }

    @Override
    public int getBirdWidth() {
        return eggBird[0].getWidth();
    }

    @Override
    public int getBirdHeight() {
        return eggBird[0].getHeight();
    }

    @Override
    public void resetPosition() {
        birdX = GameView.dWidth + random.nextInt(1500);
        birdY = random.nextInt(400);
        velocity = 16 + random.nextInt(12);
    }
}
