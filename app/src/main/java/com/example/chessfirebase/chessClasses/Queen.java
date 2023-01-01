package com.example.chessfirebase.chessClasses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

public class Queen extends Piece{

    public Queen(int row,int col,boolean isWhite){
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(),isWhite ? R.drawable.white_queen : R.drawable.black_queen);
        this.img=Bitmap.createScaledBitmap(this.img,(int)size,(int) size,true);
    }
    //TODO: implement movement.
    public void calculateMoves(Board b){
        int[][] dir={{-1,-1},{-1,1},{1,-1},{-1,-1},{1,0},{0,1},{-1,0},{0,-1}};
        for(int[] d:dir){
            for(int r=this.row,c=this.col;r>=0 && c>=0 && r<8 && c<8;r+=d[0],c+=d[1]){
                if(b.getCell(r,c).getPiece()==null){
                    super.possibleMoves.add(new int[] {r,c});
                }else{
                    if(b.getCell(r,c).getPiece().isWhite==this.isWhite){
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
        return this.isWhite ? 'Q' : 'q';
    }
}
