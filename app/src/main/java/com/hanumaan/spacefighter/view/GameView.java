package com.hanumaan.spacefighter.view;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.hanumaan.spacefighter.R;
import com.hanumaan.spacefighter.activity.MainActivity;
import com.hanumaan.spacefighter.model.Boom;
import com.hanumaan.spacefighter.model.Enemy;
import com.hanumaan.spacefighter.model.Friend;
import com.hanumaan.spacefighter.model.Player;
import com.hanumaan.spacefighter.model.Star;

import java.util.ArrayList;

/**
 * Created by Hanumaan on 12/8/2017.
 */

public class GameView extends SurfaceView implements Runnable {

    private Context context;

    //boolean variable to track if the game is playing or not
    volatile boolean playing;

    //the game thread
    private Thread gameThread = null;

    //adding the player
    private Player player;

    //These objects will be used for drawing
    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    //Adding a star list
    private ArrayList<Star> stars = new ArrayList<Star>();

    //Adding enemies object array
    private Enemy[] enemies;

    //Adding enemies
    private int enemyCount = 2;

    //defining a boom object to display boom
    private Boom boom;

    //Adding a friend
    private Friend friend;

    //a screenX holder
    int screenX;

    //to count the number of misses
    int countMisses;

    //indicator that the enemy has just entered the game screen
    boolean flag;

    //an indicator if the game is over
    private boolean isGameOver;

    //score
    int score;

    //high score
    int highScore[] = new int[4];

    //SharedPreferences to store high scores
    SharedPreferences sharedPreferences;

    //the media player objects to configure the background music
    static MediaPlayer gameOnSound;
    final MediaPlayer killedEnemySound;
    final MediaPlayer gameOverSound;

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        this.context = context;

        //initializing player object
        player = new Player(context,screenX,screenY);

        //initializing drawing objects
        surfaceHolder = getHolder();
        paint = new Paint();

        this.screenX = screenX;
        countMisses = 0;
        isGameOver = false;

        //initializing score to zero
        score = 0;

        sharedPreferences = context.getSharedPreferences("SPACE_FIGHTER",Context.MODE_PRIVATE);

        //initializing the array high score with previous values
        highScore[0] = sharedPreferences.getInt("score0",0);
        highScore[1] = sharedPreferences.getInt("score1",0);
        highScore[2] = sharedPreferences.getInt("score2",0);
        highScore[3] = sharedPreferences.getInt("score3",0);

        //initializing the media player for sounds
        gameOnSound = MediaPlayer.create(context, R.raw.gameon);
        killedEnemySound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOverSound = MediaPlayer.create(context,R.raw.gameover);

        //starting the game music as the game starts
        gameOnSound.start();

        //adding 100 stars
        int starNums = 100;
        for(int i=0;i<starNums;i++){
            Star star = new Star(screenX,screenY);
            stars.add(star);
        }

        //initializing enemy object array
        enemies = new Enemy[enemyCount];
        for(int i=0;i<enemyCount;i++){
            enemies[i] = new Enemy(context,screenX,screenY);
        }

        //initializing boom blast
        boom = new Boom(context);

        //initializing Friend
        friend = new Friend(context,screenX,screenY);
    }

    @Override
    public void run() {
        while (playing){
            //to update the frame
            update();

            //to draw the frame
            draw();

            //to control
            control();
        }
    }

    private void update(){
        //incrementing score as time passes
        score++;

        //updating player position
        player.update();

        //setting boom outside the screen;
        boom.setX(-250);
        boom.setY(-250);

        //updating the star with players speed
        for(Star star:stars)
            star.update(player.getSpeed());

        //updating the enemy coordinate with respect to playerSpeed
        for(int i=0;i<enemyCount;i++){

            //setting flag true when the enemy just entered screen
            if(enemies[i].getX() == screenX)
                flag = true;

            enemies[i].update(player.getSpeed());

            //if collision occurs with player
            if(Rect.intersects(player.getDetectCollision(),enemies[i].getDetectCollision())){
                //displaying boom at the location
                boom.setX(enemies[i].getX());
                boom.setY(enemies[i].getY());

                //playing killed enemy sound
                killedEnemySound.start();

                //moving enemy outside the left edge
                enemies[i].setX(-200);
            }
            else{ //if player misses the enemy
                //if the enemy has just entered
                if(flag){
                    //if player's x coordinate is more than enemy's x coordinate i.e enemy passed the player
                    if(player.getDetectCollision().exactCenterX() >= enemies[i].getDetectCollision().exactCenterX()){
                        //increment countMisses
                        countMisses++;

                        //setting the flag false so that the else part is executed only when new enemy enters the screen
                        flag = false;

                        //if no of misses is equal to 3, then game over
                        if(countMisses == 3){
                            playing = false;
                            isGameOver = true;

                            //assigning the scores to the high score
                            for(int j=0;j<4;j++){
                                if(highScore[j] < score){
                                    highScore[j] = score;
                                    break;
                                }
                            }

                            //storing the scores
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            for(int j=0;j<4;j++){
                                editor.putInt("score" + j,highScore[j]);
                            }
                            editor.apply();
                        }
                    }
                }
            }
        }

        //updating the friend according to player speed
        friend.update(player.getSpeed());

        //checking for a collision with friend
        if(Rect.intersects(player.getDetectCollision(),friend.getDetectCollision())){
            //displaying boom
            boom.setX(friend.getX());
            boom.setY(friend.getY());

            //setting playing to false
            playing = false;
            //setting game over
            isGameOver = true;

            //assigning the scores to the high score
            for(int j=0;j<4;j++){
                if(highScore[j] < score){
                    highScore[j] = score;
                    break;
                }
            }

            //storing the scores
            SharedPreferences.Editor editor = sharedPreferences.edit();
            for(int j=0;j<4;j++){
                editor.putInt("score" + j,highScore[j]);
            }
            editor.apply();
        }
    }

    private void draw(){
        //checking if surface is valid
        if(surfaceHolder.getSurface().isValid()){
            //locking the canvas
            canvas = surfaceHolder.lockCanvas();
            //drawing a background color for canvas
            canvas.drawColor(Color.BLACK);

            //setting the paint color to draw stars
            paint.setColor(Color.WHITE);

            for(Star star:stars){
                paint.setStrokeWidth(star.getStarWidth());
                canvas.drawPoint(star.getX(),star.getY(),paint);
            }

            //drawing score
            paint.setTextSize(30);
            canvas.drawText("Score: " + score,100,50,paint);

            //Drawing the player
            canvas.drawBitmap(player.getBitmap(),player.getX(),player.getY(),paint);

            //drawing the enemies
            for(int i=0;i<enemyCount;i++){
                canvas.drawBitmap(enemies[i].getBitmap(),enemies[i].getX(),enemies[i].getY(),paint);
            }

            //drawing the blast
            canvas.drawBitmap(boom.getBitmap(),boom.getX(),boom.getY(),paint);

            //drawing friend ship
            canvas.drawBitmap(friend.getBitmap(),friend.getX(),friend.getY(),paint);

            //drawing game over
            if(isGameOver){
                //stopping game on sound
                stopMusic();

                //play the game over sound
                gameOverSound.start();

                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos = (int)((canvas.getHeight()/2) - (paint.descent() + paint.ascent())/2);
                canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
            }

            //unlocking the canvas
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control(){
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause(){
        //when the game is paused
        //setting the variable to false
        playing = false;
        try{
            //stopping the thread
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void resume(){
        //when the game is resumed
        //starting the thread again
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                //When the user presses the screen
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                //When the user releases the screen
                player.setBoosting();
                break;
        }

        if(isGameOver){
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                context.startActivity(new Intent(context, MainActivity.class));
            }
        }

        return true;
    }

    public static void stopMusic(){
        if(gameOnSound != null){
            gameOnSound.stop();
            gameOnSound.release();
            gameOnSound = null;
        }
    }
}
