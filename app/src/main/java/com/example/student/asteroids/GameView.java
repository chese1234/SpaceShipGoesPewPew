package com.example.student.asteroids;


import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.*;

public class GameView extends SurfaceView implements SurfaceHolder.Callback {
    private GameThread thread;
    private Spaceship spaceship;
    private boolean down = false;
    Paint color = new Paint();
    HashSet<Bullet> bullets = new HashSet<>();
    //color.setColor(Color.RED);
    HashMap<Integer,Finger> fingers = new HashMap<>();



    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);

        thread = new GameThread(getHolder(), this);
        setFocusable(true);
        spaceship = new Spaceship(BitmapFactory.decodeResource(getResources(),
                R.drawable.spaceship),getHeight() / 2, getWidth() / 2,this);
    }
    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height){

    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        thread.setRunning(true);
        thread.start();
    }



    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        boolean retry = false;
        while(retry){
            try{
                thread.join();
                retry = false;
            }catch (InterruptedException e){
            }
        }
    }

    public void removeFinger(int id){
        fingers.remove(id);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int id = event.getPointerId(event.getActionIndex());
        int action = MotionEventCompat.getActionMasked(event);
        //if(action != MotionEvent.ACTION_MOVE)
        //System.out.println(actionToString(action) + "  " + event.getActionIndex() + "  " + event.getPointerId(event.getActionIndex()) + "  " + event.getX(index) + "  " + event.getY(index));
        //System.out.println(event.getAction());
        switch(action){
            case MotionEvent.ACTION_DOWN:
                MoveFinger p = new MoveFinger(event.getX(index),event.getY(index),id,this);
                fingers.put(id,p);
                p.setSpaceship(spaceship);
                break;
            case MotionEvent.ACTION_MOVE:
                int count = event.getPointerCount();
                for(int i = 0;i < count;i++){
                    if(fingers.get(event.getPointerId(i)) != null)
                        fingers.get(event.getPointerId(i)).move(event.getX(i), event.getY(i));
                }

                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                //System.out.println(" pointer down" + id);
                Finger k= new Finger(event.getX(index),event.getY(index),id,this);
                fingers.put(id, k);
                break;
            case MotionEvent.ACTION_UP:
                fingers.remove(id);
                spaceship.stopShip();
                break;
            case MotionEvent.ACTION_POINTER_UP:
                fingers.get(id).remove();
                break;
            case MotionEvent.ACTION_OUTSIDE:
                fingers.get(id).remove();
                break;
            case MotionEvent.ACTION_CANCEL:
                ArrayList<Integer> w = new ArrayList<>();
                for(Integer i: fingers.keySet()){
                    w.add(i);
                }
                while(!w.isEmpty()){
                    fingers.get(w.get(0)).remove();
                    w.remove(0);
                }
                break;
        }





        return true;
    }

    public static String actionToString(int action) {
        switch (action) {

            case MotionEvent.ACTION_DOWN: return "Down";
            case MotionEvent.ACTION_MOVE: return "Move";
            case MotionEvent.ACTION_POINTER_DOWN: return "Pointer Down";
            case MotionEvent.ACTION_UP: return "Up";
            case MotionEvent.ACTION_POINTER_UP: return "Pointer Up";
            case MotionEvent.ACTION_OUTSIDE: return "Outside";
            case MotionEvent.ACTION_CANCEL: return "Cancel";
        }
        return "nothing";
    }


    @Override
    protected void onDraw(Canvas canvas) {
        try {
            canvas.drawColor(Color.GREEN);
            if (fingers.size() > 0)
                try {
                    for (Integer i : fingers.keySet()) {
                        if (fingers.get(i).getSize() > 0)
                            fingers.get(i).draw(canvas);
                    }
                } catch (Exception e) {
                    System.out.println("  asdfasdfa  " + e);
                }
            //System.out.println(" ");
            spaceship.draw(canvas);
        }catch(Exception p){
                //this.pause();
            }

    }


   /*public  Spaceship getSpaceship() {
        return spaceship;
    }*/
}
