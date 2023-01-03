package com.example.chessfirebase.chessClasses;

//---------------imports-------------------

import android.graphics.Canvas;

import com.example.chessfirebase.socketPlayer;

import java.util.List;

public class Board{
    //variables
    private BoardCell[][] board;
    private King wk;
    private King bk;
    public Piece remove;
    public boolean isFirstClick=true;
    int[] from;
    private socketPlayer player;
    private Piece currSelectedPiece;

    public Board(boolean isWhite,socketPlayer p){
        this.player=p;
        from = new int[2];
        board=new BoardCell[8][8];
        for(int i=0;i<board.length;i++){
            for(int j=0;j<board.length;j++){
                board[i][j]=new BoardCell(i,j);
                //color handling
                board[i][j].isWhite=i%2==j%2;
                if(i==0){//add backrank pieces
                    if(j==0 || j==7){
                        Rook r=new Rook(i,j,!isWhite);
                        board[i][j].setPiece(r);
                    }

                    if(j==1 || j==6){
                        //add other colored horse
                        board[i][j].setPiece(new Knight(i, j,!isWhite));
                    }

                    if(j==2 || j==5){
                        //add other colored bishop
                        board[i][j].setPiece(new Bishop(i, j, !isWhite));
                    }
                    if(isWhite){
                        if(j==3){
                            //add other colored queen
                            board[i][j].setPiece(new Queen(i, j, !isWhite));
                        }
                        if(j==4){
                            if(!isWhite){
                                wk=new King(i, j, !isWhite);
                                board[i][j].setPiece(wk);
                            }
                            else{
                                bk=new King(i, j, !isWhite);
                                board[i][j].setPiece(bk);
                            }
                        }
                    }else{
                        if(j==3){
                            if(!isWhite){
                                wk=new King(i, j, !isWhite);
                                board[i][j].setPiece(wk);
                            }
                            else{
                                bk=new King(i, j, !isWhite);
                                board[i][j].setPiece(bk);
                            }
                        }
                        if(j==4){
                            //add other colored queen
                            board[i][j].setPiece(new Queen(i, j, !isWhite));
                        }
                    }

                }
                if(i==1){
                    //add not our color pawn
                    Pawn pawn=new Pawn(i,j, !isWhite);
                    board[i][j].setPiece(pawn);
                }
                if(i==6){
                    //add our colored pawn
                    Pawn pawn=new Pawn(i,j, isWhite);
                    board[i][j].setPiece(pawn);
                    player.pieces.add(pawn);
                }
                if(i==7){
                    if(j==0 || j==7){
                        //ad our colored rook
                        board[i][j].setPiece(new Rook(i, j, isWhite));
                        player.pieces.add(board[i][j].getPiece());
                    }

                    if(j==1 || j==6){
                        //add our colored horse
                        board[i][j].setPiece(new Knight(i, j, isWhite));
                        player.pieces.add(board[i][j].getPiece());
                    }

                    if(j==2 || j==5){
                        //add our colored bishop
                        board[i][j].setPiece(new Bishop(i, j, isWhite));
                        player.pieces.add(board[i][j].getPiece());
                    }
                    if(isWhite){
                        if(j==3){
                            //add our colored queen
                            board[i][j].setPiece(new Queen(i, j, isWhite));
                            player.pieces.add(board[i][j].getPiece());
                        }
                        if(j==4){
                            if(!isWhite){
                                bk=new King(i, j, isWhite);
                                board[i][j].setPiece(bk);
                            }
                            else{
                                wk=new King(i, j, isWhite);
                                board[i][j].setPiece(wk);
                            }
                            player.pieces.add(board[i][j].getPiece());
                        }
                    }else{
                        if(j==3){
                            if(!isWhite){
                                bk=new King(i, j, isWhite);
                                board[i][j].setPiece(bk);
                            }
                            else{
                                wk=new King(i, j, isWhite);
                                board[i][j].setPiece(wk);
                            }
                            player.pieces.add(board[i][j].getPiece());
                        }
                        if(j==4){
                            board[i][j].setPiece(new Queen(i, j, isWhite));
                            player.pieces.add(board[i][j].getPiece());
                        }
                    }
                }
            }
        }
    }

    public void psuedoClickListener(int row,int col){
//        if(!player.ourTurn)return;
        if(isFirstClick){
            currSelectedPiece=board[row][col].getPiece();
            if(currSelectedPiece!=null && currSelectedPiece.isWhite==player.isWhite){
                from=currSelectedPiece.getRawPos();
                isFirstClick=!isFirstClick;
                currSelectedPiece.isSelected=true;
            }
        }else if(currSelectedPiece!=null){
            List<int[]> lst=currSelectedPiece.getMoves();
            isFirstClick=!isFirstClick;
            for(int[] mov: lst){
                if(mov[0]==row && mov[1]==col){
                    player.toggleOurTurn();
                    this.playerMove(from , mov);
                    player.ch.move=from[0]+","+from[1]+" "+mov[0]+","+mov[1];
                    if(currSelectedPiece instanceof Pawn)((Pawn) currSelectedPiece).isFirstMove=false;
                    player.calculateMoves();
                    break;
                }
            }
            currSelectedPiece.isSelected=false;
            currSelectedPiece=null;
        }
    }

    public void move(int[] from,int[] to) {
        this.board[to[0]][to[1]].setPiece(this.board[from[0]][from[1]].getPiece());
        this.board[from[0]][from[1]].getPiece().move(to[0], to[1]);
        this.board[from[0]][from[1]].setPiece(null);
    }

    public void playerMove(int[] from,int[] to){
        Piece p=this.getCell(to[0], to[1]).getPiece();
        if(p!=null && p.isWhite!=player.isWhite) player.remove(p);

        this.board[to[0]][to[1]].setPiece(this.board[from[0]][from[1]].getPiece());
        this.board[from[0]][from[1]].setPiece(null);
        this.board[to[0]][to[1]].getPiece().move(to[0],to[1]);
    }

    public void draw(Canvas canvas){
        for(int i=0;i<8;i++){
            for(int j=0;j<8;j++){
                board[i][j].draw(canvas);
            }
        }
    }

    public void printBoard(){
        for(int i=0;i<this.board.length;i++){
            for(int j=0;j<this.board[i].length;j++){
                board[i][j].print();
            }
        }
    }

    public BoardCell getCell(int row,int col){
        return this.board[row][col];
    }

    public BoardCell getCell(Piece p){
        for(int i=0;i<this.board.length;i++){
            for(int j=0;j<this.board.length;j++){
                if(this.board[i][j].getPiece()==p){
                    return this.board[i][j];
                }
            }
        }
        return null;
    }

    public boolean isCheck(boolean isWhite){//check if it is check, here we input the color of the player
        if(isWhite){
            return wk.isCheck(this);
        }else{
            return bk.isCheck(this);
        }
    }
}