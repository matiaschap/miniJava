package asyn.Semantic;

import gci.GenCode;

public class If extends Sentencia{
	protected Expresion exp;
	protected Sentencia sent;

	public If(Expresion exp, Sentencia s) {
		this.exp = exp;
		sent = s;
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
		//System.out.println(exp.check().getNombre());
		if(!exp.check().getNombre().equals("boolean")) {
			throw new Exception("El tipo de la expresion en la clausula if en la linea "+exp.getToken().getnLinea()+" no es boolean");
		}	

		String finIf = "finIf"+GenCode.gen().genLabel();
		GenCode.gen().write("BF "+ finIf + " # Salto si la sentencia es falsa");

		sent.check();

		GenCode.gen().write(finIf+": NOP # Etiqueta fin if");
		GenCode.gen().nl();
	}
}