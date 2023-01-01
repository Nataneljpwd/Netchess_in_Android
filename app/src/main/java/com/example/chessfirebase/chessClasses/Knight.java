package com.example.chessfirebase.chessClasses;

//imports------

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

public class Knight extends Piece{

    public Knight(int row, int col,boolean isWhite){
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(),isWhite ? R.drawable.white_knight : R.drawable.black_knight);
        this.img= Bitmap.createScaledBitmap(this.img,(int)this.size,(int)this.size,true);
    }

    public void calculateMoves(Board b){
        int[][] moves={{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};//the possible 8 moves.
        for(int[] mov:moves){
            if(this.row+mov[0]<8 && this.row+mov[0]>=0 && this.col+mov[1]<8 && this.col+mov[1]>=0 && (b.getCell(this.row+mov[0],this.col+mov[1]).getPiece()==null || b.getCell(this.row+mov[0],this.col+mov[1]).getPiece().getIsWhite()!=this.isWhite)){
                super.possibleMoves.add(new int[]{mov[0]+this.row,mov[1]+this.col});
            }
        }
        validateMoves(b);
    }

    @Override
    public char toChar() {
        return this.isWhite ? 'N' : 'n';
    }

}