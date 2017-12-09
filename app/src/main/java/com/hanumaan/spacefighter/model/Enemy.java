package com.hanumaan.spacefighter.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

import com.hanumaan.spacefighter.R;

import java.util.Random;

/**
 * Created by Hanumaan on 12/8/2017.
 */

public class Enemy {
    //bitmap for the enemy
    private Bitmap bitmap;

    //x and y coordinate
    private int x;
    private int y;

    //enemy speed
    private int speed = 1;

    //min and max coordinate to keep them inside the screen
    private int maxX;
    private int minX;
    private int maxY;
    private int minY;

    //creating a rect object
    private Rect detectCollision;

    public Enemy(Context context,int screenX,int screenY){
        //getting bitmap from drawable resource
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.enemy);

        //initializing min and max coordinates
        maxX = screenX;
        maxY = screenY;
        minX = 0;
        minY = 0;

        //generating a random coordinate for enemy
        Random generator = new Random();
        speed = generator.nextInt(6) + 10;
        x = screenX;
        y = generator.nextInt(maxY)-bitmap.getHeight();

        //initializing rect object
        detectCollision = new Rect(x,y,bitmap.getWidth(),bitmap.getHeight());
    }

    public void update(int playerSpeed){
        //decreasing x coordinate so that enemy will move from right to left
        x -= playerSpeed;
        x -= speed;

        //if the enemy reaches the left edge
        if(x < minX-bitmap.getWidth()){
            //adding the enemy again to the right side
            Random generator = new Random();
            speed = generator.nextInt(10) + 10;
            x = maxX;
            y = generator.nextInt(maxY) - bitmap.getHeight();
        }

        //Adding the top,left,bottom,right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public Rect getDetectCollision() {
        return detectCollision;
    }

    public void setDetectCollision(Rect detectCollision) {
        this.detectCollision = detectCollision;
    }
}
