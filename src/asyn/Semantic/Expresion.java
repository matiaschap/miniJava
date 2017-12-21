package asyn.Semantic;

import asyn.Token;

public abstract class Expresion {
	protected Token tok;

	public Token getToken() {
		return tok;
	}

	public void setToken(Token tok) {
		this.tok = tok;
	}

	public abstract TipoMetodo check() throws Exception;
}