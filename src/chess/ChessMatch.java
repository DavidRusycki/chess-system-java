package chess;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.Bishop;
import chess.pieces.King;
import chess.pieces.Knight;
import chess.pieces.Pawn;
import chess.pieces.Queen;
import chess.pieces.Rook;

/**
 * Classe responsável por implementar e definir as regras do jogo.
 * @author David
 */
public class ChessMatch {

	private int turn;
	private Color currentPlayer;
	private Board board;
	private boolean check;
	private boolean checkMate;
	private ChessPiece enPassantVulnerable;
	private ChessPiece promoted;

	private List<Piece> piecesOnTheBoard = new ArrayList<>();
	private List<Piece> capturedPieces = new ArrayList<>();
	
	public ChessMatch() {
		this.board = new Board(8, 8);
		this.turn = 1;
		this.currentPlayer = Color.WHITE;
		this.initialSetup();
	}
		
	public int getTurn() {
		return turn;
	}

	public Color getCurrentPlayer() {
		return currentPlayer;
	}

	public boolean getCheck() {
		return this.check;
	}
	
	public boolean getCheckMate() {
		return this.checkMate;
	}
	
	public ChessPiece getEnPassantVulnerable() {
		return this.enPassantVulnerable;
	}
	
	public ChessPiece getPromoted() {
		return this.promoted;
	}
	
	public ChessPiece[][] getPieces() {
		ChessPiece[][] mat = new ChessPiece[this.board.getRows()][this.board.getColumns()];
		for (int i = 0; i < this.board.getRows(); i++) {
			for (int j = 0; j < this.board.getColumns(); j++) {
				mat[i][j] = (ChessPiece) this.board.piece(i, j);
			}
		}
		return mat;
	}
	
	public boolean[][] possibleMoves(ChessPosition sourcePosition) {
		Position p = sourcePosition.toPosition();
		this.validateSourcePosition(p);
		return this.board.piece(p).possibleMoves();
	}
	
	public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
		Position source = sourcePosition.toPosition();
		Position target = targetPosition.toPosition();
		this.validateSourcePosition(source);
		this.validateTargetPosition(source, target);
		Piece capturedPiece = makeMove(source, target);
		
		if (this.testCheck(currentPlayer)) {
			this.undoMove(source, target, capturedPiece);
			throw new ChessException("Você se colocou em check. Movimento desfeito.");
		}
		
		ChessPiece movedPiece = (ChessPiece) this.board.piece(target);
		
		this.promoted = null;
		
		if (movedPiece instanceof Pawn) {
			if ((movedPiece.getColor() == Color.WHITE && target.getRow() == 0) || (movedPiece.getColor() == Color.BLACK && target.getRow() == 7)) {
				this.promoted = (ChessPiece) this.board.piece(target);
				this.promoted = this.replacePromotedPiece("Q");
			}
		}
		
		this.check = (this.testCheck(this.opponent(currentPlayer))) ? true : false;
		
		if (this.testCheckMate(opponent(currentPlayer))) {
			this.checkMate = true;
		}
		else {
			this.nextTurn();			
		}
		
		// special mode enpassant 
		if (movedPiece instanceof Pawn && (target.getRow() == source.getRow() - 2 || target.getRow() == source.getRow() + 2)) {
			this.enPassantVulnerable = movedPiece;
		}
		else {
			this.enPassantVulnerable = null;
		}
		
		return (ChessPiece) capturedPiece;
	}
	
	public ChessPiece replacePromotedPiece(String type) {
		if(this.promoted == null) {
			throw new IllegalStateException("Não há peça para ser promovida");
		}
		
		if (!type.equals("B") && !type.equals("N") && !type.equals("R") && !type.equals("Q")) {
			return this.promoted;
		}
		
		Position pos = this.promoted.getChessPosition().toPosition();
		Piece p = this.board.removePiece(pos);
		piecesOnTheBoard.remove(p);
		
		ChessPiece newPiece = this.newPiece(type, this.promoted.getColor());
		board.placePiece(newPiece, pos);
		piecesOnTheBoard.add(newPiece);
		
		return newPiece;
	}
	
	private ChessPiece newPiece(String type, Color color) {
		if (type.equals("B")) return new Bishop(this.board, color);
		if (type.equals("N")) return new Knight(this.board, color);
		if (type.equals("Q")) return new Queen(this.board, color);
		return new Rook(this.board, color);
	}
	
	private Piece makeMove(Position source, Position target) {
		ChessPiece p = (ChessPiece) this.board.removePiece(source);
		p.increaseMoveCount();
		Piece capturedPiece = this.board.removePiece(target);
		this.board.placePiece(p, target);
		
		if (capturedPiece != null) {
			this.piecesOnTheBoard.remove(capturedPiece);
			this.capturedPieces.add(capturedPiece);
		}
		
		// Special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceT);
			this.board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		} 
		
		// Special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(sourceT);
			this.board.placePiece(rook, targetT);
			rook.increaseMoveCount();
		} 
		
		// Special move en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == null) {
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(target.getRow() + 1, target.getColumn());
				}
				else {
					pawnPosition = new Position(target.getRow() - 1, target.getColumn());
				}
				capturedPiece = this.board.removePiece(pawnPosition);
				capturedPieces.add(capturedPiece);
				piecesOnTheBoard.remove(capturedPiece);
			}
		}
		
		return capturedPiece;
	}
	
	private void undoMove(Position source, Position target, Piece capturedPiece) {
		ChessPiece p = (ChessPiece) board.removePiece(target);
		p.decreaseMoveCount();
		this.board.placePiece(p, source);
		
		if (capturedPiece != null) {
			board.placePiece(capturedPiece, target);
			piecesOnTheBoard.add(capturedPiece);
		}
		
		// Special move castling kingside rook
		if (p instanceof King && target.getColumn() == source.getColumn() + 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() + 3);
			Position targetT = new Position(source.getRow(), source.getColumn() + 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetT);
			this.board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		} 
		
		// Special move castling queenside rook
		if (p instanceof King && target.getColumn() == source.getColumn() - 2) {
			Position sourceT = new Position(source.getRow(), source.getColumn() - 4);
			Position targetT = new Position(source.getRow(), source.getColumn() - 1);
			ChessPiece rook = (ChessPiece) this.board.removePiece(targetT);
			this.board.placePiece(rook, sourceT);
			rook.decreaseMoveCount();
		}
		
		// Special move en passant
		if (p instanceof Pawn) {
			if (source.getColumn() != target.getColumn() && capturedPiece == this.enPassantVulnerable) {
				ChessPiece pawn = (ChessPiece) this.board.removePiece(target);
				Position pawnPosition;
				if (p.getColor() == Color.WHITE) {
					pawnPosition = new Position(3, target.getColumn());
				}
				else {
					pawnPosition = new Position(4, target.getColumn());
				}
				this.board.placePiece(pawn, pawnPosition);
			}
		}
	}
	
	private void validateSourcePosition(Position position) {
		if (!this.board.thereIsAPiece(position)) {
			throw new ChessException("Não existe peça na posição de origem.");
		}
		if (this.currentPlayer != ((ChessPiece)this.board.piece(position)).getColor()) {
			throw new ChessException("A peça escolhida não é sua.");
		}
		if (!this.board.piece(position).isTherePossibleMove()) {
			throw new ChessException("Não existe movimentos possíveis para a peça escolhida.");
		}
	}
	
	private void validateTargetPosition(Position source, Position target) {
		if (!this.board.piece(source).possibleMove(target)) {
			throw new ChessException("A peça escolhida não pode ser movida para a posição de destino.");
		}
	}
	
	private void nextTurn() {
		turn++;
		currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private Color opponent(Color color) {
		return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
	}
	
	private ChessPiece king(Color color) {
		List<Piece> list = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		for (Piece p : list) {
			if (p instanceof King) {
				return (ChessPiece)p;
			}
		}
		throw new IllegalStateException("There is no " + color + "King on the board.");
	}
	
	private boolean testCheck(Color color) {
		Position kingPosition = this.king(color).getChessPosition().toPosition();
		List<Piece> opponentPieces = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == this.opponent(color)).collect(Collectors.toList());
		for (Piece p : opponentPieces) {
			boolean[][] mat = p.possibleMoves();
			if (mat[kingPosition.getRow()][kingPosition.getColumn()]) {
				return true;
			}
		}
		return false;
	}
	
	private boolean testCheckMate(Color color) {
		if (!this.testCheck(color)) {
			return false;
		}
		
		List<Piece> list = this.piecesOnTheBoard.stream().filter(x -> ((ChessPiece)x).getColor() == color).collect(Collectors.toList());
		
		for (Piece p : list) {
			boolean[][] mat = p.possibleMoves();
			for (int i = 0; i < this.board.getRows(); i++) {
				for (int j = 0; j < this.board.getColumns(); j++) {
					if (mat[i][j]) {
						Position source = ((ChessPiece)p).getChessPosition().toPosition();
						Position target = new Position(i, j);
						
						Piece capturedPiece = this.makeMove(source, target);
						boolean testCheck = this.testCheck(color);
						this.undoMove(source, target, capturedPiece);
						if (!testCheck) {
							return false;
						}
					}
				}
				
			}
		}
		return true;
	}
	
	private void placeNewPiece(char column, int row, ChessPiece piece) {
		this.board.placePiece(piece, new ChessPosition(column, row).toPosition());
		this.piecesOnTheBoard.add(piece);
	}
	
	private void initialSetup() {
		placeNewPiece('a', 1, new Rook(board, Color.WHITE));
		placeNewPiece('b', 1, new Knight(board, Color.WHITE));
        placeNewPiece('c', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('d', 1, new Queen(board, Color.WHITE));
        placeNewPiece('e', 1, new King(board, Color.WHITE, this));
        placeNewPiece('f', 1, new Bishop(board, Color.WHITE));
        placeNewPiece('g', 1, new Knight(board, Color.WHITE));
        placeNewPiece('h', 1, new Rook(board, Color.WHITE));
        placeNewPiece('a', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('b', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('c', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('d', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('e', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('f', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('g', 2, new Pawn(board, Color.WHITE, this));
        placeNewPiece('h', 2, new Pawn(board, Color.WHITE, this));

        placeNewPiece('a', 8, new Rook(board, Color.BLACK));
        placeNewPiece('b', 8, new Knight(board, Color.BLACK));
        placeNewPiece('c', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('d', 8, new Queen(board, Color.BLACK));
        placeNewPiece('e', 8, new King(board, Color.BLACK, this));
        placeNewPiece('f', 8, new Bishop(board, Color.BLACK));
        placeNewPiece('g', 8, new Knight(board, Color.BLACK));
        placeNewPiece('h', 8, new Rook(board, Color.BLACK));
        placeNewPiece('a', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('b', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('c', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('d', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('e', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('f', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('g', 7, new Pawn(board, Color.BLACK, this));
        placeNewPiece('h', 7, new Pawn(board, Color.BLACK, this));
	}
	
}
