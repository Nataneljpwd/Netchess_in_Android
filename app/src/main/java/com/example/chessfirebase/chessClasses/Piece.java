package com.example.chessfirebase.chessClasses;

//---------------------------imports--------------------------------

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.util.*;


//------------------------class + methods--------------------------------
public abstract class Piece{

    //---------------------------position handling--------------------------------
    protected int col;
    protected int row;
    public final boolean isWhite;
    public List<int []> possibleMoves;
    public boolean isSelected;//if piece is tapped we set it to true and calc moves and display validated moves
    protected boolean isMoveCheck=false;

    //------------------------GUI handling--------------------------------
    public static float size;
    public Bitmap img;
    //------------------------methods--------------------------------

    public Piece(int row, int col, boolean isWhite){
        this.row = row;
        this.col = col;
        this.isWhite = isWhite;
        this.isSelected=false;
        this.possibleMoves=new ArrayList<int[]>();
    }
    //returns position of the piece assuming 0,0 is the start of the board
    public float[] getPos(){
        return new float[] {(col)*size,(row)*size};//returns {x,y}
    }

    public int[] getRawPos(){
        return new int[]{row,col};
    }

    public int getCol() {
        return this.col;
    }

    public int getRow() {
        return this.row;
    }

    public boolean getIsWhite() {
        return this.isWhite;
    }

    public List<int[]> getMoves() {
        //removed the board and the calc moves because thy happen before every move.
        return this.possibleMoves;
    }

    public void setMoves(List<int[]> possibleMoves){
        this.possibleMoves= possibleMoves;
    }

    public void validateMoves(Board b){
        //checking for pinned pieces
        isMoveCheck=true;
        List<int[]> moves=this.possibleMoves;
//        this.isSelected=false;
        List<int[]> remove=new ArrayList<>();
        int[] currPos=new int[]{this.row,this.col};
        //check what moves to remove:
        for(int i=0;i<moves.size();i++){
            //move the piece
            int[] mov=moves.get(i);
            Piece p=b.getCell(mov[0], mov[1]).getPiece();
            //maybe add an edge case of same colored pieces but probably handled by cacl moves
            b.move(currPos, mov);
            if(b.isCheck(this.isWhite)){
                remove.add(mov);
            }
            //move the piece back
            b.move(mov,currPos);
            if(p!=null){
                b.getCell(mov[0], mov[1]).setPiece(p);
            }
        }
        //remove the invalid moves
        for(int i=0;i<remove.size();i++){
            if(remove.get(i)!=null){
                moves.remove(remove.get(i));
            }
        }

        this.setMoves(moves);
        isMoveCheck=false;
    }

    public abstract void calculateMoves(Board b);

    public void draw(Canvas canvas){
        float[] pos=getPos();
        canvas.drawBitmap(this.img,pos[0],pos[1],null);
        if(isSelected)drawValidMoves(canvas);
    }
    public abstract char toChar();

    public void drawValidMoves(Canvas canvas){
        //foreach loop of moves, draw circle with low opacity
        Paint paint=new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        //set paint alpha to be mostly transparent
        paint.setAlpha(85);
        float circleSize = 0.75f*size;
        for(int[] mov:possibleMoves){
            //draw the gray circle
            canvas.drawCircle(mov[1]*size+0.5f*size,mov[0]*size+0.5f*size,circleSize/2,paint);
        }
    }
    //in the board class add a listener to all cells in the board that sends move events
    public void move(int row,int col){
        this.row=row;
        this.col=col;
    }
}