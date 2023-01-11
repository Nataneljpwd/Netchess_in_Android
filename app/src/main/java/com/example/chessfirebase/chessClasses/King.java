package com.example.chessfirebase.chessClasses;


//imports-------
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.chessfirebase.R;
import com.example.chessfirebase.customGameView;

import java.util.*;

public class King extends Piece {

    int[][] dir={{-1,-1},{-1,1},{1,-1},{1,1},{1,0},{0,1},{-1,0},{0,-1}};
    public boolean isFirstMove=true;
    Board board;
    public King(int row,int col,boolean isWhite){
        super(row, col, isWhite);
        this.img = BitmapFactory.decodeResource(customGameView.context.getResources(),isWhite ? R.drawable.white_king : R.drawable.black_king);
        this.img= Bitmap.createScaledBitmap(this.img,(int)this.size,(int)this.size,false);
    }
    public void setBoard(Board board){
        this.board= board;
    }

    public void calculateMoves(Board b){
        this.possibleMoves.clear();
        for(int[] d:dir){
            if(this.row+d[0]<8 && this.row+d[0]>=0 && this.col+d[1]<8 && this.col+d[1]>=0 && (b.getCell(this.row+d[0],this.col+d[1]).getPiece()==null || b.getCell(this.row+d[0],this.col+d[1]).getPiece().getIsWhite()!=this.isWhite)){
                super.possibleMoves.add(new int[] {this.row+d[0], this.col+d[1]});
            }
        }


        //start of castles logic:
        if(this.isFirstMove && !isCheck(b)){
            int[] castleShort,castleLong;
            if(isWhite){
                castleLong=new int[]{this.row,this.col-2};
                castleShort=new int[]{this.row,this.col+2};
            }else{
                castleLong=new int[]{this.row,this.col+2};
                castleShort=new int[]{this.row,this.col-2};
            }
            super.possibleMoves.add(castleLong);
            super.possibleMoves.add(castleShort);

            //check that there are no pieces in between
            boolean[] bol=canCastle(b);

            if(isWhite){
                if(!bol[0]){
                    super.possibleMoves.remove(castleShort);
                }
                if(!bol[1]){
                    super.possibleMoves.remove(castleLong);
                }
            }else{
                if(!bol[1]){
                    super.possibleMoves.remove(castleShort);
                }if(!bol[0]){
                    super.possibleMoves.remove(castleLong);
                }
            }
        }
        validateMoves(b);
    }
    private boolean[] canCastle(Board b){//first index is short second is long fo white, for black its the opposite

        boolean[] res=new boolean[]{true,true};
        //first the castle short
        Piece p=b.getCell(this.row,7).getPiece();
        if(p!=null && p instanceof Rook && ((Rook)p).isFirstMove && this.isWhite==p.isWhite){
            for(int i=this.col+1;i<p.getCol();i++){
                if(b.getCell(this.row,i).getPiece()!=null){
                    res[0]=false;
                    break;
                }
            }
        }
        //castle long
        p=b.getCell(this.row,0).getPiece();
        if(p!=null && p instanceof Rook && ((Rook) p).isFirstMove && this.isWhite==p.isWhite){
            for(int i=this.col-1;i>p.getCol();i--){
                if(b.getCell(this.row,i).getPiece()!=null){
                    res[1]=false;
                    break;
                }
            }
        }
        return res;
    }

    public boolean isCheck(Board b){
        //run BFS to check if its check+ 8 moves for Knight and 2 for pawns.
        //first diagonal then vertical
        Piece p;
        for(int i=1;i<dir.length;i++) {
            for(int r=this.row+dir[0][0],c=this.col+dir[0][1];r<8 && r>=0 && c<8 && c>=0;r+=dir[i][0],c+=dir[i][1]){
                if((p=b.getCell(r,c).getPiece())!=null){
                    if(p.isWhite!=this.isWhite){//check if not our color
                        if(i<=3){
                            if(p instanceof Queen || p instanceof Bishop){
                                return true;
                            }
                        }else{
                            if(p instanceof Queen || p instanceof Rook){
                                return true;
                            }
                        }
                    }else{
                        //we can break cuz it blocks
                        break;
                    }
                }
            }
        }
        //check for knight and pawn checks:

        //knight:

        int[][] moves={{2,1},{2,-1},{-2,1},{-2,-1},{1,2},{-1,2},{1,-2},{-1,-2}};//taken from the knight class
        //exact same algorithm, just reusing differently
        for(int[] mov:moves){
            if(this.row+mov[0]>=0 && this.row+mov[0]<8 && this.col+mov[1]>=0 && this.col+mov[1]<8){//checking if inside the board
                if(b.getCell(this.row+mov[0],this.col+mov[1]).getPiece()!=null && //if there is a piece there
                        b.getCell(this.row+mov[0],this.col+mov[1]).getPiece() instanceof Knight &&//is this piece a Knight
                        b.getCell(this.row+mov[0], this.col+mov[1]).getPiece().getIsWhite() != this.isWhite){
                    return true;
                }
            }
        }

        //pawn:
        if(this.row-1>=0 && this.col+1<8 && b.getCell(this.row-1, this.col+1).getPiece()!=null){//checking if there is a pawn infront on the right of the king
            if(b.getCell(this.row-1, this.col+1).getPiece() instanceof Pawn && b.getCell(this.row-1, this.col+1).getPiece().getIsWhite() != this.isWhite){
                return true;
            }
        }
        if(this.row-1>=0 && this.col-1>=0 && b.getCell(this.row-1, this.col-1).getPiece()!=null){//checking if there is a pawn infront on the left of the king
            if(b.getCell(this.row-1, this.col-1).getPiece() instanceof Pawn && b.getCell(this.row-1, this.col-1).getPiece().getIsWhite() != this.isWhite){
                return true;
            }
        }

        //if no checks found we return false.
        return false;
    }

    @Override
    public void move(int row, int col) {
        if(!this.isMoveCheck)
        if(Math.abs(this.col-col)>1){//means we can castle
            //we castle
            if(this.col>col){
                //move the rook one to the left of the king
                int[] rookPos=new int[]{7,0};
                board.getCell(rookPos[0],rookPos[1]).getPiece().move(this.row,this.col-1);
                board.move(rookPos,new int[]{this.row, this.col-1});
            }else{
                int[] rookPos=new int[]{7,7};
                board.getCell(rookPos[0],rookPos[1]).getPiece().move(this.row,this.row+1);
                board.move(rookPos,new int[]{this.row,this.col+1});
            }
        }
        super.move(row, col);
    }

    @Override
    public char toChar() {
        return this.isWhite ? 'K' : 'k';
    }

}