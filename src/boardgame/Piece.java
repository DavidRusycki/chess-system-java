package boardgame;

/**
 * Classe base de peça.
 * @author David
 */
public class Piece {

	protected Position position;
	private Board board;
	
	public Piece(Board board) {
		this.board = board;
		this.position = null;
	}

	protected Board getBoard() {
		return board;
	}
	
}
