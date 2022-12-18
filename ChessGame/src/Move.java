import java.io.PipedInputStream;
import java.util.ArrayList;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

enum moveType{
    NONE, NORMAL,KILL
}

class Move {
    private Square sourceSquare;
     private Square destinationSquare;
    private moveType type;
    public Piece piece;
    private Piece enemyPiece;
    public ArrayList<Piece> killedPieces=new ArrayList<Piece>();



    public Move(Square sourceSquare, Square destinationSquare, Piece piece) {
        this.sourceSquare = sourceSquare;
        this.destinationSquare = destinationSquare;
        this.piece = piece;
    }
    // Getters and setters
    public Square getSourceSquare() {
        return sourceSquare;
    }

    public Square getDestinationSquare() {
        return destinationSquare;
    }
    public Piece getEnemyPiece()
    {
        return enemyPiece;
    }
    public void setEnemyPiece(Piece enemyPiece)
    {
        this.enemyPiece=enemyPiece;
    }
   

    public Piece getPiece() {
        return piece;
    }
  
    public void setSource(Square sourceSquare) {
        this.sourceSquare = sourceSquare;
    }

    public void setDestination(Square destinationSquare) {
        this.destinationSquare = destinationSquare;
    }

    public void setPiece(Piece piece) {
        this.piece = piece;
    }
 

    public boolean equals(Move anotherMove) {
		if(this.getSourceSquare() == anotherMove.getSourceSquare() && this.getDestinationSquare() == anotherMove.getDestinationSquare() ){
			return true;
		}
		return false;
	}


    public void doMove(ChessBoard chessBoard) {

        // Handling castling
        if (piece.getName().equals("King")) {
            int difference = piece.getLocation().getx() - destinationSquare.getx();

            if (difference == 2) {
                Square rookSource = chessBoard.getBoard()[piece.getLocation().getx() - 3][piece.getLocation().gety()];
                Square rookDestination = chessBoard.getBoard()[piece.getLocation().getx() - 1][piece.getLocation().gety()];
                Move rookMove = new Move(rookSource, rookDestination, rookSource.getPlaceholder());
                rookMove.doMove(chessBoard);
            }

            else if (difference == -2) {
                Square rookSource = chessBoard.getBoard()[piece.getLocation().getx() + 4][piece.getLocation().gety()];
                Square rookDestination = chessBoard.getBoard()[piece.getLocation().getx() + 1][piece.getLocation().gety()];
                Move rookMove = new Move(rookSource, rookDestination, rookSource.getPlaceholder());
                rookMove.doMove(chessBoard);
            }
        }

        //Handling pawn promotion
        //////////////////////////////////////////

        if (!destinationSquare.isEmpty()) {
            Piece killedPiece = destinationSquare.getPlaceholder();
            chessBoard.getChildren().remove(killedPiece.getImage());  // Removing the image of the killed piece
            this.enemyPiece=killedPiece;

            if (killedPiece.getIsWhite()) {
                chessBoard.getWhitePieces().remove(killedPiece);
            }
            else {
                chessBoard.getBlackPieces().remove(killedPiece);
            }

            destinationSquare.getPlaceholder().setLocation(null);
        }
        
        destinationSquare.setPlaceholder(piece);
        chessBoard.getChildren().remove(piece.getImage());
        this.piece.setLocation(destinationSquare);
        chessBoard.add(piece.getImage(), destinationSquare.getx(), destinationSquare.gety());
        sourceSquare.setPlaceholder(null);
        this.piece.setHasMoved(true);
        //handling pawn promotion
        if(piece.getName().equals("Pawn") && (destinationSquare.gety()==0 || destinationSquare.gety()==7)){
            chessBoard.getChildren().remove(piece.getImage());
            Piece queen = new Queen(piece.getIsWhite(), destinationSquare);
            chessBoard.add(queen.getImage(), destinationSquare.getx(), destinationSquare.gety());
            destinationSquare.setPlaceholder(queen);
            if(piece.getIsWhite()){
                chessBoard.getWhitePieces().add(queen);
            }
            else{
                chessBoard.getBlackPieces().add(queen);
            }
        }
        chessBoard.gameHistory.add(this);

    }

    public void doMove(CheckersBoard checkersboard)
    {
        if(Math.abs(this.getDestinationSquare().getx()-this.getSourceSquare().getx())==2)
        {
            Square middleSquare=checkersboard.getBoard()[(this.getDestinationSquare().getx()+this.getSourceSquare().getx())/2][(this.getDestinationSquare().gety()+this.getSourceSquare().gety())/2];
            Piece killedPiece=middleSquare.getPlaceholder();
            checkersboard.getChildren().remove(killedPiece.getImage());
            this.killedPieces.add(killedPiece);
            middleSquare.setPlaceholder(null);
        }
        if(Math.abs(this.getDestinationSquare().getx()-this.getSourceSquare().getx())==4) 
        {
            Square middleSquare1=checkersboard.getBoard()[(this.getDestinationSquare().getx()+this.getSourceSquare().getx())/2][(this.getDestinationSquare().gety()+this.getSourceSquare().gety())/2];
            Square middleSquare2=checkersboard.getBoard()[(this.getDestinationSquare().getx()+this.getSourceSquare().getx())/2+1][(this.getDestinationSquare().gety()+this.getSourceSquare().gety())/2];
            Piece killedPiece1=middleSquare1.getPlaceholder();
            Piece killedPiece2=middleSquare2.getPlaceholder();
            checkersboard.getChildren().remove(killedPiece1.getImage());
            checkersboard.getChildren().remove(killedPiece2.getImage());
            this.killedPieces.add(killedPiece1);
            this.killedPieces.add(killedPiece2);
            middleSquare1.setPlaceholder(null);
            middleSquare2.setPlaceholder(null);
        }
        
        destinationSquare.setPlaceholder(piece);
        checkersboard.getChildren().remove(piece.getImage());
        this.piece.setLocation(this.getDestinationSquare());
        System.out.println(destinationSquare.getx() + " " + destinationSquare.gety());
        System.out.println("Piece location: " + piece.getLocation().getx() + " " + piece.getLocation().gety());
        checkersboard.add(piece.getImage(), destinationSquare.getx(), destinationSquare.gety());
        sourceSquare.setPlaceholder(null);
        checkersboard.gameHistory.add(this);
    }
    
    public void reverseMove(ChessBoard chessBoard)
    {
        if (this.enemyPiece != null) {
            chessBoard.add(this.enemyPiece.getImage(), this.destinationSquare.getx(), this.destinationSquare.gety());
            sourceSquare.setPlaceholder(piece);
            chessBoard.getChildren().remove(this.piece.getImage());
            chessBoard.add(this.piece.getImage(), this.sourceSquare.getx(), this.sourceSquare.gety());
            chessBoard.board[this.destinationSquare.getx()][this.destinationSquare.gety()].setPlaceholder(this.enemyPiece);
            chessBoard.board[this.sourceSquare.getx()][this.sourceSquare.gety()].setPlaceholder(piece);
            this.enemyPiece.setLocation(this.destinationSquare);
        }

        else {
            sourceSquare.setPlaceholder(piece);
            chessBoard.getChildren().remove(this.piece.getImage());
            chessBoard.add(this.piece.getImage(), this.sourceSquare.getx(), this.sourceSquare.gety());
            chessBoard.board[this.sourceSquare.getx()][this.sourceSquare.gety()].setPlaceholder(piece);
            destinationSquare.setPlaceholder(null);
        }

        this.piece.setLocation(this.sourceSquare);
        if (this.piece.isHasMoved())
            this.piece.setHasMoved(false);

        chessBoard.switchTurn();
        chessBoard.updateStatusLabel();
    }
    public void reverseMove(CheckersBoard checkersBoard) {
        if(this.killedPieces.size()>0)
        {
            Piece killedPiece=this.killedPieces.get(this.killedPieces.size()-1);
            this.killedPieces.remove(this.killedPieces.size()-1);
            checkersBoard.getChildren().remove(killedPiece.getImage());
            checkersBoard.add(killedPiece.getImage(), (this.destinationSquare.getx()+this.sourceSquare.getx())/2, (this.destinationSquare.gety()+this.sourceSquare.gety())/2);
            checkersBoard.board[(this.destinationSquare.getx()+this.sourceSquare.getx())/2][(this.destinationSquare.gety()+this.sourceSquare.gety())/2].setPlaceholder(killedPiece);
            killedPiece.setLocation(checkersBoard.board[(this.destinationSquare.getx()+this.sourceSquare.getx())/2][(this.destinationSquare.gety()+this.sourceSquare.gety())/2]);
        }
        sourceSquare.setPlaceholder(piece);
        checkersBoard.getChildren().remove(this.piece.getImage());
        checkersBoard.add(this.piece.getImage(), this.sourceSquare.getx(), this.sourceSquare.gety());
        checkersBoard.board[this.sourceSquare.getx()][this.sourceSquare.gety()].setPlaceholder(piece);
        destinationSquare.setPlaceholder(null);
        this.piece.setLocation(this.sourceSquare);
        checkersBoard.switchTurn();
        checkersBoard.updateStatusLabel();
    }
   
}


class Promotion extends Move{
    private Piece promotedPiece;
    public Promotion(Square sourceSquare, Square destinationSquare, Piece piece, Piece promotedPiece) {
        super(sourceSquare, destinationSquare, piece);
        this.promotedPiece=promotedPiece;
    }
    public Piece getPromotedPiece()
    {
        return promotedPiece;
    }
    public void setPromotedPiece(Piece promotedPiece)
    {
        this.promotedPiece=promotedPiece;
    }
    public void doMove(GameBoard chessBoard) {
        getDestinationSquare().setPlaceholder(piece);
        chessBoard.getChildren().remove(piece.getImage());
        this.piece.setLocation(getDestinationSquare());
        getSourceSquare().setPlaceholder(null);
        this.piece.setHasMoved(true);
        chessBoard.add(promotedPiece.getImage(), getDestinationSquare().getx(), getDestinationSquare().gety());
        promotedPiece.setLocation(getDestinationSquare());
        chessBoard.gameHistory.add(this);
    }
    public void reverseMove(ChessBoard chessBoard) {
        getSourceSquare().setPlaceholder(piece);
        chessBoard.getChildren().remove(this.promotedPiece.getImage());
        chessBoard.add(this.piece.getImage(), getSourceSquare().getx(), getSourceSquare().gety());
        chessBoard.board[getDestinationSquare().getx()][getDestinationSquare().gety()].setPlaceholder(piece);
        getDestinationSquare().setPlaceholder(null);
        this.piece.setLocation(getSourceSquare());
        if (this.piece.isHasMoved())
            this.piece.setHasMoved(false);
        chessBoard.switchTurn();
        chessBoard.updateStatusLabel();
    }

}


