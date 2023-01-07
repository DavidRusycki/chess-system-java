package chess.pieces;

import boardgame.Board;
import boardgame.Position;
import chess.ChessPiece;
import chess.Color;

public class Queen extends ChessPiece {

	public Queen(Board board, Color color) {
		super(board, color);
	}
	
	@Override
	public String toString() {
		return "Q";
	}
	
	@Override
	public boolean[][] possibleMoves() {
		boolean[][] matriz = new boolean[this.getBoard().getRows()][this.getBoard().getColumns()];
		
		Position p = new Position(0, 0);
		
		//Acima
		p.setValues(this.position.getRow() - 1, this.position.getColumn());
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setRow(p.getRow() - 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//Abaixo
		p.setValues(this.position.getRow() + 1, this.position.getColumn());
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setRow(p.getRow() + 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//Esquerda
		p.setValues(this.position.getRow(), this.position.getColumn() - 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setColumn(p.getColumn() - 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//Direita
		p.setValues(this.position.getRow(), this.position.getColumn() + 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setColumn(p.getColumn() + 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//nw
		p.setValues(this.position.getRow() - 1, this.position.getColumn() - 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() - 1, p.getColumn() - 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//ne
		p.setValues(this.position.getRow() - 1, this.position.getColumn() + 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() - 1, p.getColumn() + 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//se
		p.setValues(this.position.getRow() + 1, this.position.getColumn() + 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() + 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		//sw
		p.setValues(this.position.getRow() + 1, this.position.getColumn() - 1);
		while(this.getBoard().positionExists(p) && !this.getBoard().thereIsAPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
			p.setValues(p.getRow() + 1, p.getColumn() - 1);
		}
		if (this.getBoard().positionExists(p) && this.isThereOpponentPiece(p)) {
			matriz[p.getRow()][p.getColumn()] = true;
		}
		
		return matriz;
	}
	
}
