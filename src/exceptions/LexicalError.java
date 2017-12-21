package exceptions;

/**
 * @author Matias Marzullo
 *
 */

@SuppressWarnings("serial")

public class LexicalError extends Exception {

	public LexicalError(String msg) {
		super(msg);
	}

}
