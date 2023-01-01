package com.example.chessfirebase.chessClasses;

//imports-------

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

import java.util.*;



public class Bishop extends Piece{

    public Bishop(int row,int col,boolean isWhite) {
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(), isWhite ? R.drawable.white_bishop : R.drawable.black_bishop);
        this.img= Bitmap.createScaledBitmap(this.img,(int)this.size,(int)this.size,true);
    }

    //calculateing the moves using bfs

    public void calculateMoves(Board b){
        this.possibleMoves.clear();
        int[][] dir={{-1,-1},{-1,1},{1,-1},{-1,-1}};
        //top left, top right, bottom left, bottom right

        for(int i=0;i<dir.length;i++){
            for(int r=this.row,c=this.col;r>=0 && c>=0 && r<8 && c<8;r+=dir[i][0], c+=dir[i][1]){
                //if first time we dont check curr pos,(possible not to add this cond)
                //Delete if needed
                if(r==this.row && c==this.col){
                    continue;
                }
                if(b.getCell(r,c).getPiece()==null){
                    super.possibleMoves.add(new int[] {r,c});
                }else{
                    if(b.getCell(r, c).getPiece().isWhite==this.isWhite){
                        break;
                    }else{
                        super.possibleMoves.add(new int[] {r,c});
                        break;
                    }
                }
            }
        }
        validateMoves(b);
    }

    @Override
    public char toChar() {
        return this.isWhite ? 'B' : 'b';
    }

}