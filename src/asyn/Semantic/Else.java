package asyn.Semantic;

import gci.GenCode;

public class Else extends If{

	Sentencia sentElse;
	public Else(Expresion enc, Sentencia s, Sentencia sentElse) {
		super(enc,s);
		this.sentElse = sentElse;
	}
	public Sentencia getSentElse() {
		return sentElse;
	}
	public void setSentElse(Sentencia sentElse) {
		this.sentElse = sentElse;
	}

	public void check() throws Exception {
		if(!exp.check().getNombre().equals("boolean")) {
			throw new Exception("El tipo de la expresion en la clausula if en la linea "+exp.getToken().getnLinea()+" no es boolean");
		}

		String finIf = "finIf"+GenCode.gen().genLabel();
		String lElse = "else"+GenCode.gen().genLabel();

		GenCode.gen().write("BF "+ lElse + " # Salto si la sentencia es falsa");

		sent.check();

		GenCode.gen().write("JUMP "+finIf+" # Salto al fin del if para que no ejecute el else");
		GenCode.gen().write(lElse+": NOP # Codigo del else");

		sentElse.check();

		GenCode.gen().write(finIf+": NOP # Etiqueta fin if");
		GenCode.gen().nl();
	}
}