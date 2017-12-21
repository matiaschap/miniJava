package asyn.Semantic;

import java.util.ArrayList;

import asyn.*;
import gci.GenCode;

public class DecVars extends Sentencia{
	private Tipo t;
	private ArrayList<Variable> vars;

	public DecVars(Tipo t, ArrayList<Variable> vars) {
		this.t = t;
		this.vars = vars;
	}

	public void check() throws Exception {

		for (Variable v : vars) {
			//AnalizadorSyn.getTs().getMetodoActual().addVar(v);
			Metodo m = AnalizadorSyn.getTs().getMetodoActual();

			m.addVar(v);

			v.setOffset(m.getOffVar());
			m.setOffVar(m.getOffVar()-1);

			v.chequearDeclaraciones();
		}
		//RMEM DE LA CANT EN VARS
		GenCode.gen().write("RMEM "+vars.size()+" # Reservo espacio para variables locales");

	}

}