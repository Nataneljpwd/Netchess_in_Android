package com.example.chessfirebase.chessClasses;

//---------------imports------------------
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;
//handle gui later

//will probably be changed to buttons
public class BoardCell {
    //position of the board cell
    private int col;
    private int row;
    public static float size;
    private Piece piece;
    public boolean isWhite;

    //-------------------------methods+ctor--------------------------------
    public BoardCell(int row, int col) {
        this.col = col;
        this.row = row;

        this.piece =null;//so that later we can check if has piece
    }

    public void setPiece(Piece p) {
        this.piece = p;
    }
    public float[] getPosition() {
        return new float[]{this.col*size, this.row*size};//returns {x,y}
    }

    public Piece getPiece(){
        return this.piece;
    }

    public void draw(@NonNull Canvas canvas){
        Paint p=new Paint();
        p.setStyle(Paint.Style.FILL);
        p.setColor(isWhite ? customGameView.context.getResources().getColor(R.color.gray_400):customGameView.context.getResources().getColor(R.color.light_blue_600));
        canvas.drawRect((int)this.col*size,(int)this.row*size,(int)(1+this.col)*size,(int)(1+this.row)*size,p);

        if(this.piece !=null){
            this.piece.draw(canvas);
        }
    }
    public void print(){
//        if(this.p!=null){
//            System.out.print(p.toChar());
//        }
//        else{
            Log.d("COLORwS",isWhite ? "-":".");
//        }
    }

}