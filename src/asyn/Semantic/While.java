package asyn.Semantic;

import gci.GenCode;

public class While extends Sentencia{

	private Expresion exp;
	private Sentencia sent;

	public While(Expresion exp, Sentencia sent) {
		this.exp = exp;
		this.sent = sent;
	}

	public Expresion getExp() {
		return exp;
	}
	public void setExp(Expresion exp) {
		this.exp = exp;
	}
	public Sentencia getSent() {
		return sent;
	}
	public void setSent(Sentencia sent) {
		this.sent = sent;
	}

	public void check() throws Exception {

		String finWhile = "finWhile"+GenCode.gen().genLabel();
		String lWhile = "while"+GenCode.gen().genLabel();
		GenCode.gen().write(lWhile+": NOP # Etiqueta while");

		TipoMetodo tipoExp = exp.check();
		if(!tipoExp.getNombre().equals("boolean")) {
			throw new Exception("La expresion en la sentencia while de la linea "+exp.getToken().getnLinea()+" no es booleana");
		}

		GenCode.gen().write("BF "+ finWhile + " # Si es falso salgo del bucle");
		sent.check();
		GenCode.gen().write("JUMP "+lWhile+" # Salto al label del while");
		GenCode.gen().write(finWhile+": NOP # Etiqueta finWhile");	


	}
}