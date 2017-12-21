package asyn.Semantic;

public class Argumentos {
	protected Expresion exp;
	protected Argumentos args;

	/**
	 * @param exp
	 * @param args
	 */
	public Argumentos(Expresion exp, Argumentos args) {
		this.exp = exp;
		this.args = args;
	}

	public Expresion getExp() {
		return exp;
	}

	public void setExp(Expresion exp) {
		this.exp = exp;
	}

	public Argumentos getArgs() {
		return args;
	}

	public void setArgs(Argumentos args) {
		this.args = args;
	}

}