package com.thames.shootemdown;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Handler;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class GameView extends View {
    Bitmap background;

    Rect rect;
    static int dWidth, dHeight;
    Handler handler;
    Runnable runnable;
    final long UPDATE_MILLIS = 30;
    ArrayList<PinkBird> pinkBirds;
    ArrayList<EggBird> eggBirds;

    Bitmap[] fireball = new Bitmap[8];
    int fireballFrame;

    Bitmap pointer, boom_shooting;
    float fireballX, fireballY;
    float sX, sY;
    float fX, fY;
    float dX, dY;
    float tempX, tempY;
    Paint aimingAreaPaint;

    //life and score for the game more fun
    int score = 0;
    final int MAX_LIFE = 12;
    int life = MAX_LIFE;

    //context for Gameover
    Context context;

    //create sound effects for the game
    MediaPlayer pinkBird_hit, eggBird_hit, bird_miss, fireball_shooting;

    //when life becomes less than 1, change the gameState boolean to false
    boolean gameState = true;

    Paint textPaint;

    public GameView(Context context) {
        super(context);
        background = BitmapFactory.decodeResource(getResources(), R.drawable.background_game);
        Display display = ((Activity) getContext()).getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        dWidth = size.x;
        dHeight = size.y;

        //the rectangular matching the phone's display to draw canvas
        rect = new Rect(0, 0, dWidth, dHeight);

        //use Handler and Runnable to delay the drawing 30 milliseconds
        //to save resources and battery
        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                invalidate();
            }
        };
        //creating Arraylists of pinkBird and eggBird
        pinkBirds = new ArrayList<>();
        eggBirds = new ArrayList<>();

        //adding 2 pinkBirds and 2 eggBirds into the Arraylists
        for (int i = 0; i < 2; i++) {
            PinkBird pinkBird = new PinkBird(context);
            pinkBirds.add(pinkBird);
            EggBird eggBird = new EggBird(context);
            eggBirds.add(eggBird);
        }

        fireball [0] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame1);
        fireball [1] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame2);
        fireball [2] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame3);
        fireball [3] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame4);
        fireball [4] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame5);
        fireball [5] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame6);
        fireball [6] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame7);
        fireball [7] = BitmapFactory.decodeResource(context.getResources(),R.drawable.hedgehog_frame8);

        fireballFrame = 0;

        pointer = BitmapFactory.decodeResource(context.getResources(),R.drawable.pointer);
        boom_shooting = BitmapFactory.decodeResource(context.getResources(),R.drawable.boom_shooting);

        fireballX = fireballY = 0;
        //sX and sY are the coordinates for the first touch by user
        //fX and fY are the coordinates for the moving touch and lifting from phone's screen by user
        sX = sY = fX = fY = 0;

        //dX and dY are the distances between coordinates of the first touch and lifting touch by user
        dX = dY = 0;

        //tempX and tempY are also the velocities for the fireball shooting diagonally
        tempX = tempY = 0;

        //draw a border for limiting drawing pointer area
        aimingAreaPaint = new Paint();
        aimingAreaPaint.setColor(Color.GREEN);
        aimingAreaPaint.setStrokeWidth(6);

        textPaint = new Paint();
        textPaint.setColor(Color.RED);
        textPaint.setStrokeWidth(6);
        textPaint.setTextSize(60);

        //Context of Gameover passed in the constructor
        this.context = context;

        //Instantiate the sound effects in the Constructor
        pinkBird_hit = MediaPlayer.create(context, R.raw.pinkbirdhit);
        eggBird_hit = MediaPlayer.create(context, R.raw.eggbirdhit);
        bird_miss = MediaPlayer.create(context, R.raw.miss2);
        fireball_shooting = MediaPlayer.create(context,R.raw.shooting);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //when life becomes zero, the game will create an intent object to launch GameOver activity
        //and finish the current activity
        if(life < 1){
            gameState = false;
            Intent intent = new Intent(context,GameOver.class);
            intent.putExtra("score", score);
            context.startActivity(intent);
            ((Activity)context).finish();
        }

        //draw the Bitmap for the game's background
        canvas.drawBitmap(background, null, rect, null);

        //drawing the pinkBird on screen
        for (int i = 0; i < pinkBirds.size(); i++) {
            //Drawing the pinkBird
            canvas.drawBitmap(pinkBirds.get(i).getBirdBitmap(),
                    pinkBirds.get(i).birdX,pinkBirds.get(i).birdY, null);
            pinkBirds.get(i).birdFrame++;
            if (pinkBirds.get(i).birdFrame > 3) {
                pinkBirds.get(i).birdFrame = 0;
            }

            //if eggBird crosses the screen left, it will be redrawn
            pinkBirds.get(i).birdX -= pinkBirds.get(i).velocity;
            if(pinkBirds.get(i).birdX < -pinkBirds.get(i).getBirdWidth()){
                pinkBirds.get(i).resetPosition();
                life--;
                if(bird_miss != null){
                    bird_miss.start();
                }
            }
            //fireball collision with pinkBird mechanism
            if(fireballX <= (pinkBirds.get(i).birdX + pinkBirds.get(i).getBirdWidth())
                    && fireballX + fireball[0].getWidth() >= pinkBirds.get(i).birdX
                    && fireballY <= (pinkBirds.get(i).birdY + pinkBirds.get(i).getBirdHeight())
                    && fireballY >= pinkBirds.get(i).birdY)
            {for(int j = 0; j < 10; j++) {
                canvas.drawBitmap(boom_shooting,pinkBirds.get(i).birdX,
                    pinkBirds.get(i).birdY,null);}
                pinkBirds.get(i).resetPosition();
            score++;
            if(pinkBird_hit != null){
                pinkBird_hit.start();}
            }

            //drawing the eggBird
            canvas.drawBitmap(eggBirds.get(i).getBirdBitmap(),
                    eggBirds.get(i).birdX,eggBirds.get(i).birdY, null);
            eggBirds.get(i).birdFrame++;
            if (eggBirds.get(i).birdFrame > 3) {
                eggBirds.get(i).birdFrame = 0;}

            //if eggBird crosses the screen left, it will be redrawn
            //or the fireball missed the eggBird
            eggBirds.get(i).birdX -= eggBirds.get(i).velocity;
            if(eggBirds.get(i).birdX < -eggBirds.get(i).getBirdWidth()){
                eggBirds.get(i).resetPosition();
                life--;
                if(bird_miss != null){
                    bird_miss.start();
                }
            }
            //fireball collision with eggBird mechanism
            if(fireballX <= (eggBirds.get(i).birdX + eggBirds.get(i).getBirdWidth())
                    && fireballX + fireball[0].getWidth() >= eggBirds.get(i).birdX
                    && fireballY <= (eggBirds.get(i).birdY + eggBirds.get(i).getBirdHeight())
                    && fireballY >= eggBirds.get(i).birdY)
            {for(int j = 0; j < 10; j++) {
                canvas.drawBitmap(boom_shooting,eggBirds.get(i).birdX,
                        eggBirds.get(i).birdY,null);}
                eggBirds.get(i).resetPosition();
            score++;
            if(eggBird_hit != null){
                eggBird_hit.start();}
            }

        }
        //Draw the pointer to aim the birds
        if(sX > 0 && sY > dHeight*0.7f){
            canvas.drawBitmap(pointer,sX - pointer.getWidth()/2,
                    sY - pointer.getHeight()/2,null );}
        if((Math.abs(fX - sY) > 0 || Math.abs(fY - sY)>0) && fY>0 && fY > dHeight * 0.7f) {
            canvas.drawBitmap(fireball[fireballFrame],fX - fireball[0].getWidth()/2,
                        fY - fireball[0].getHeight()/2,null );
            fireballFrame++;
            if(fireballFrame>7){
                fireballFrame = 0;
            }
        }
        //draw the shooting fireball
        if((Math.abs(dX) > 10 || Math.abs(dY) > 10) && sY > dHeight * 0.7f
        && fY > dHeight * 0.7f){
                fireballX = fX - fireball[0].getWidth()/2 - tempX;
                fireballY = fY - fireball[0].getHeight()/2 - tempY;
                canvas.drawBitmap(fireball[fireballFrame],fireballX,fireballY,null);
                fireballFrame++;
                if(fireballFrame>7){
                    fireballFrame = 0;
                }
                tempX += dX;
                tempY += dY;
                if(fireball_shooting != null){
                    fireball_shooting.start();
                }
        }
        //draw a border for limiting drawing pointer area
        canvas.drawLine(0,dHeight * 0.7f,
                dWidth, dHeight * 0.7f,aimingAreaPaint);

        //draw life and score
        canvas.drawText("Life: " + life,dWidth*0.1f,dHeight*0.6f,textPaint);
        canvas.drawText("Score: " + score,dWidth*0.1f,dHeight*0.65f,textPaint);

        //use Handler and Runnable to delay the drawing 30 milliseconds
        // to save resources and battery
        //the postDelayed is only called when the gameState is true
        if(gameState){
        handler.postDelayed(runnable, UPDATE_MILLIS);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                dX = dY = fX = fY = tempX = tempY = 0;
                sX = event.getX();
                sY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                fX = event.getX();
                fY = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                fX = event.getX();
                fY = event.getY();
                fireballX = event.getX();
                fireballY = event.getY();
                dX = fX - sX;
                dY = fY - sY;
                break;
        }
        return true;
    }
}
