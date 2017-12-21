package exceptions;

/**
 * @author Matias Marzullo
 *
 */
public class SyntaxError extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Constructor de la clase SyntaxError.
	 *
	 * @param exc
	 */

	private int line;
	private String exp;
	private String found;
	public SyntaxError(int line, String msg, String exp, String found) {
		super("Syntax Error at line "+ line + ". Expected " + exp +" and " +found+ " has found: "+msg);
		this.line=line;
		this.exp=exp;
		this.found=found;
	}
	
	public SyntaxError(String msg, String exp, String found) {
		super("Syntax Error . Expected  " + exp +" and " +found+ " has found: "+msg);
		this.exp=exp;
		this.found=found;
	}
	public SyntaxError(String exc) {
		super(exc);
	}
	public int getLine() {
		return line;
	}

	public String getExp() {
		return exp;
	}

	public String getFound() {
		return found;
	}
}