package com.example.chessfirebase;

//imports
import com.example.chessfirebase.chessClasses.Bishop;
import com.example.chessfirebase.chessClasses.Board;
import com.example.chessfirebase.chessClasses.King;
import com.example.chessfirebase.chessClasses.Knight;
import com.example.chessfirebase.chessClasses.Pawn;
import com.example.chessfirebase.chessClasses.Piece;

import java.io.*;
import java.net.Socket;
import java.util.*;


public class socketPlayer implements Runnable{
    private Board board;//think about how move mechanism will work (almost done)
    public boolean isWhite;
    public boolean ourTurn;
    public List<Piece> pieces;
    public ConnectionHandler ch;
    String host="10.0.2.2";

    int port = 8888;

    public socketPlayer(){
        this.pieces=new ArrayList<Piece>();
        this.ch=new ConnectionHandler(host,port);
    }

    public Board getBoard() {return board;}

    public ConnectionHandler getConnectionHandler(){
        return this.ch;
    }
    @Override
    public void run(){
        try {
            playerSetup(this.ch.getIsWhite());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        calculateMoves();
        Thread t=new Thread(this.ch);
        t.start();
    }

    public void playerSetup(boolean isWhite) throws InterruptedException{
        this.board=new Board(isWhite, this);
        this.isWhite=isWhite;
        this.ourTurn=this.isWhite;
    }

    //add a check for what pieces there are to know if its draw!.
    public boolean checkPossibleMoves(){
        boolean bishop=false,knight=false;
        if(pieces.size()==1)return true;//only a king left
        for(Piece p : pieces){
            if(p instanceof Bishop || p instanceof Knight || p instanceof King){
                bishop=p instanceof Bishop;
                knight=p instanceof Knight;
            }

            if(p.possibleMoves.size()>0 && pieces.size()>1){
                return false;
            }
        }
        if(bishop^knight){
            // only the bishop or the knight is left or both kings
            return true;
        }

        return true ;
    }

    public void calculateMoves(){
        for(int i=0;i<this.pieces.size();i++){
            Piece p=this.pieces.get(i);
            if(p != null){
                p.calculateMoves(board);
            }
        }
    }

    public void move(int[] curr,int[] to){//add a listener that will listen where clicked after a piece was selected to get the x and y
        Piece p=board.getCell(curr[0], curr[1]).getPiece();
        for(int[] mov:p.possibleMoves){
            if(mov[0]==to[0] && mov[1]==to[1]){
                ourTurn=!ourTurn;
                this.ch.move=curr[0]+","+curr[1]+" "+to[0]+","+to[1];
            }
        }
    }

    //after we recieve the move:
    public int situationCheck(){
        //return 0 if nothing, 1 if check 2 if mate 3 if draw.
        if(board.isCheck(isWhite)){
            calculateMoves();
            //here if we get true then its mate because its check and there are no possible moves.
            if(checkPossibleMoves()){
                return 2;
            }else{
                return 1;
            }
        }else{
            //check for draw
            if(checkPossibleMoves()){
                return 3;
            }
        }
        return 0;
    }

    public boolean checkForPromotion(){
        for(int i=0;i<8;i++){
            if(board.getCell(0, i).getPiece() instanceof Pawn && board.getCell(0, i).getPiece().isWhite==this.isWhite){
                return true;//handle promotion logic to send to opp.
            }
        }
        return false;
    }

    public void remove(Piece p){
        this.pieces.remove(p);
    }

    //will add the listener in the gameview in android studio which willl be a thread.
    //it will call the move function


    public class ConnectionHandler implements Runnable{

        private Socket s;
        private BufferedReader reader;
        private PrintWriter writer;
        private boolean ourTurn;
        private boolean isWhite;
        private boolean isGameOver=false;
        public String move;

        public ConnectionHandler(String host,int port){
            try {
                s=new Socket(host,port);
                writer=new PrintWriter(s.getOutputStream(),true);
                reader=new BufferedReader(new InputStreamReader(s.getInputStream()));
                String msg="";
                //first we send a message wheter the playee is white of=r black
                while(msg==null || msg.equals(""))
                    msg=this.reader.readLine();

                this.isWhite = Boolean.parseBoolean(msg);
                this.ourTurn=this.isWhite;
            }catch (IOException e) {
                e.printStackTrace();
            }
        }

        public boolean getIsWhite(){
            return isWhite;
        }


        public void sendDraw() {
            try {
                writer.write("DRAW");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        public void sendOppWon(){
            try {
                writer.println("MATE");
            } catch (Exception e) {
                // TODO: handle exception
            }
        }

        @Override
        public void run() {
            String msg="";
            while(true){
                try{
                    if(ourTurn){//TODO: add diff funcs to check for stalemate and only king, then a  only king packet will be sent
                        int a=situationCheck();//need to recheck the function
                        if(a==3){
                            isGameOver=true;
                            //handle draw
                        }else if(a==2){
                            //Mate, we lost
                            isGameOver=true;
                        }else if(a==1){
                            //check , make a vibreation
                        }
                        if(this.move!=null && !this.move.equals("")){
                            writer.println(this.move);
                            this.move="";
                            ourTurn=!ourTurn;
                        }
                    }

                    else{
                        msg=reader.readLine();
                        if(msg!=null && msg.equals("DRAW")){
                            if(situationCheck()==3){
                                //its a draw
                                //handle draw
                                this.isGameOver=true;
                            }
                        }else if(msg != null && msg.equals("MATE")){
                            //we won
                            isGameOver=true;
                        }
                        this.move=msg;
                        //make the move
                        if(this.move != null && !this.move.equals("")){
                            String[] conv=this.move.split(" ");
                            int[] from={Integer.parseInt(conv[0].split(",")[0]),Integer.parseInt(conv[0].split(",")[1])};
                            int[] to={Integer.parseInt(conv[1].split(",")[0]),Integer.parseInt(conv[1].split(",")[1])};
                            from[0]=7-from[0];
                            from[1]=7-from[1];
                            to[0]=7-to[0];
                            to[1]=7-to[1];
                            board.move(from, to);
                            this.ourTurn=!ourTurn;
                            int a =situationCheck();
                            if(a==1){
                                //Makwe a sound or vibration
                            }else if(a==2){
                                isGameOver=true;
                                sendOppWon();
                            }else if(a==3){
                                sendDraw();
                            }
                            ourTurn=!ourTurn;
                            calculateMoves();
                        }
                    }
                }catch(Exception e){
                    //this.isGameOver=true;
                    //TODO handle exception
                    e.printStackTrace();
                    break;
                }
            }
        }

    }
}
