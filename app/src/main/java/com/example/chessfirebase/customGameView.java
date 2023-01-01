package com.example.chessfirebase;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.example.chessfirebase.chessClasses.Board;
import com.example.chessfirebase.chessClasses.BoardCell;
import com.example.chessfirebase.chessClasses.Piece;

public class customGameView extends SurfaceView implements Runnable {
    public static Context context;
    private float width,height;
    private SurfaceHolder holder;
    private Board b;
    public customGameView(Context context, float width, float height) {
        super(context);
        this.context = context;
        this.width = width;
        this.height = height;
        holder = getHolder();
    }
    public void start(Board b){
        this.b=b;
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                boardMove(event.getX(), event.getY());
                return false;
            }
        });
        Thread t=new Thread(this);
        t.start();
    }
    public void boardMove(float x, float y){
        int row=(int)Math.floor(y/BoardCell.size);
        int col=(int)Math.floor(x/BoardCell.size);
        Log.d("Click at :",x+", "+y+", row:"+row+", col:"+col);
        Log.i("PIECE:","the piece in the current cell:" + (this.b.getCell(row,col).getPiece()==null));
        this.b.psuedoClickListener(row,col);
    }
    public void drawBoard(){
        if(holder.getSurface().isValid()){
            Canvas canvas = holder.lockCanvas();
            canvas.drawColor(context.getResources().getColor(androidx.cardview.R.color.cardview_dark_background));
            b.draw(canvas);
            holder.unlockCanvasAndPost(canvas);
        }
    }

    @Override
    public void run() {
        while (true){
            drawBoard();
            try {
            Thread.sleep(1000/30);
            } catch (InterruptedException e) { e.printStackTrace();}
          }
    }
}
