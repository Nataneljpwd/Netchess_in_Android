package com.example.chessfirebase.chessClasses;

//imports:
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

import java.util.*;




public class Pawn extends Piece{
    //variables
    public boolean isFirstMove;

    public Pawn(int row, int col, boolean isWhite) {
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(), isWhite ? R.drawable.white_pawn : R.drawable.black_pawn);
        this.img = Bitmap.createScaledBitmap(this.img,(int) this.size,(int) this.size,true);
        this.isFirstMove=true;
    }

    //methods:
    public void calculateMoves(Board b){
        this.possibleMoves.clear();
        if (this.row-1>=0 && b.getCell(this.row-1,this.col).getPiece()==null){
            if(this.isFirstMove && this.row-2>=0 && b.getCell(this.row-2,this.col).getPiece()==null){
                super.possibleMoves.add(new int[] {this.row-2,this.col});
            }
            super.possibleMoves.add(new int[] {this.row-1,this.col});
        }
        if(row-1>=0 && col+1<8 && b.getCell(row-1, col+1).getPiece()!=null && b.getCell(row-1,col+1).getPiece().isWhite!=this.isWhite)super.possibleMoves.add(new int[] {row-1,col+1});

        if(row-1>=0 && col-1>=0 && b.getCell(row-1, col-1).getPiece()!=null && b.getCell(row-1, col-1).getPiece().isWhite!=this.isWhite)super.possibleMoves.add(new int[] {row-1,col-1});

        //check for unPeasant move

//        if(this.row==3){
//            Piece p=b.getCell(this.row, this.col-1).getPiece() == null ? b.getCell(this.row, this.col+1).getPiece() : b.getCell(this.row, this.col-1).getPiece();
//            if(p!=null && p instanceof Pawn && p.isWhite!=this.isWhite){
//                int[] prevPosOfPawn=((Pawn) p).prevPos;
//                if(prevPosOfPawn[0]==1){//if it was the first move
//
//                }
//            }
//        }

        validateMoves(b);
    }

    public List<int[]> getMoves(Board b) {
        calculateMoves(b);
        return this.possibleMoves;
    }

    @Override
    public void move(int row, int col) {
        super.move(row, col);
    }

    @Override
    public char toChar() {
        return this.isWhite ? 'P':'p';
    }
}