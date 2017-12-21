package asyn.Semantic;

public class ExpConEncadenado extends Primario{
	protected Expresion exp;

	public ExpConEncadenado(Expresion exp, Encadenado enc) {
		this.enc = enc;
		this.exp = exp;
	}

	public Expresion getExp() {
		return exp;
	}

	public void setExp(Expresion exp) {
		this.exp = exp;
	}

	public TipoMetodo check() throws Exception {
		
		TipoMetodo tipoExp = exp.check();

			
		if(enc!=null) {
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(tipoExp);
		}

		return tipoExp;
	}

	public boolean terminaEnVar() {
		if (this.getEnc()==null)
			return true;
		else return this.getEnc().terminaEnVar();
	}

	public boolean terminaEnLlamada() {
		if (this.getEnc()==null)
			return false;
		else return this.getEnc().terminaEnLlamada();
	}

}