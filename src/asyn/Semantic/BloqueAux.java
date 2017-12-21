package asyn.Semantic;

import java.util.ArrayList;
import java.util.HashMap;

import gci.GenCode;

public class BloqueAux extends Bloque {

	String print;

	public BloqueAux(String print)
	{	
		this.print = print;	
		sentencias = new ArrayList<Sentencia>();
		varLocales = new HashMap<String, Variable>();
	}

	public void check() {
		String[] inst = print.split("\n");

		for (String s : inst) {
			GenCode.gen().write(s);
		}

	}
}