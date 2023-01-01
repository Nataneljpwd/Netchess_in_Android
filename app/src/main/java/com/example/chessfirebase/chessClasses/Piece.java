package com.example.chessfirebase.chessClasses;

//---------------------------imports--------------------------------

import android.graphics.Bitmap;
import android.graphics.Canvas;
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
    //if 1 piece is selected change all other are to not selected

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
        List<int[]> moves=this.possibleMoves;
        List<int[]> remove=new ArrayList<>();
        int[] currPos;
        //check what moves to remove:
        for(int i=0;i<moves.size();i++){
            //move the piece without drawing
            int[] mov=moves.get(i);
            currPos=this.getRawPos();
            Piece p=b.getCell(mov[0], mov[1]).getPiece();
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
    }

    public abstract void calculateMoves(Board b);

    public void draw(Canvas canvas){
        float[] pos=getPos();
//        Log.i("POSITION OF "+this.row+","+this.col,""+pos[0]+", "+pos[1]);
        canvas.drawBitmap(this.img,pos[0],pos[1],null);
    }
    public abstract char toChar();

    public void drawValidMoves(){
        //foreach loop of moves, draw circle with low opacity
    }
    //in the board class add a listener to all cells in the board that sends move events
    public void move(int row,int col){
        this.row=row;
        this.col=col;
    }




}
