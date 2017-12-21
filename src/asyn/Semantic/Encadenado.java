package asyn.Semantic;

import asyn.Token;

public abstract class Encadenado {
	Token tok;
	Encadenado enc;	
	protected boolean esLadoIzq;

	public boolean isEsLadoIzq() {
		return esLadoIzq;
	}

	public void setEsLadoIzq(boolean esLadoIzq) {
		this.esLadoIzq = esLadoIzq;
	}
	
	public Token getTok() {
		return tok;
	}
	public void setTok(Token tok) {
		this.tok = tok;
	}
	public Encadenado getEnc() {
		return enc;
	}
	public void setEnc(Encadenado enc) {
		this.enc = enc;
	}

	public abstract TipoMetodo check(TipoMetodo t) throws Exception;

	public abstract boolean terminaEnVar();

	public abstract boolean terminaEnLlamada();

}