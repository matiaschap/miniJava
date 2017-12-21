package asyn.Semantic;

public abstract class Primario extends Expresion{

	protected Encadenado enc;
	protected boolean esLadoIzq;

	public boolean esLadoIzq() {
		return esLadoIzq;
	}

	public void setEsLadoIzq(boolean esLadoIzq) {
		this.esLadoIzq = esLadoIzq;
	}

	public Encadenado getEnc() {
		return enc;
	}

	public void setEnc(Encadenado enc) {
		this.enc = enc;
	}

	public abstract boolean terminaEnVar();

	public abstract boolean terminaEnLlamada();
	
	public abstract TipoMetodo check() throws Exception;

}