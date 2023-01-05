package chess;

import boardgame.Board;
import boardgame.Piece;

/**
 * Classe responsável por definir dados de uma peça do jogo.
 * @author David
 */
public abstract class ChessPiece extends Piece {

	private Color color;

	public ChessPiece(Board board, Color color) {
		super(board);
		this.color = color;
	}
	
	public Color getColor() {
		return this.color;
	}
	
}
