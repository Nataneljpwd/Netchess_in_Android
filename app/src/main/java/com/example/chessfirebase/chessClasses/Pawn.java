package com.example.chessfirebase.chessClasses;

//imports:
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

import java.util.*;




public class Pawn extends Piece{
    //variables
    public boolean isFirstMove=true;



    public Pawn(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(), isWhite ? R.drawable.white_pawn : R.drawable.black_pawn);
        this.img= Bitmap.createScaledBitmap(this.img,(int) this.size,(int) this.size,true);
    }

    //methods:
    public void calculateMoves(Board b){
        this.possibleMoves.clear();
        //change to check the moves better - we dont have a check checker
        if (this.row-1>=0 && b.getCell(this.row-1,this.col).getPiece()==null){
            if(isFirstMove && this.row-2>=0 && b.getCell(this.row-2,this.col).getPiece()==null){
                super.possibleMoves.add(new int[] {this.row-2,this.col});
            }
            isFirstMove = false;
            super.possibleMoves.add(new int[] {this.row-1,this.col});
        }
        if(row-1>=0 && col+1<8 && b.getCell(row-1, col+1).getPiece()!=null)super.possibleMoves.add(new int[] {row-1,col+1});

        if(row-1>=0 && col-1>=0 && b.getCell(row-1, col-1).getPiece()!=null)super.possibleMoves.add(new int[] {row-1,col-1});

        validateMoves(b);
    }

    public List<int[]> getMoves(Board b) {
        calculateMoves(b);
        return this.possibleMoves;
    }

    @Override
    public char toChar() {
        return this.isWhite ? 'P':'p';
    }
}