package asyn.Semantic;

import gci.GenCode;

public class TipoBool extends TipoPrim{

	public TipoBool(int l) {
		nombre = "boolean";
		linea = l;
	}

	public boolean esCompatible(TipoMetodo t) {

		return (t.getNombre().equals(this.nombre));
	}

	public boolean check() {
		return true;
	}

	public void gen(String s) {
		//System.out.println(s);
		if (s.equals("true"))
			GenCode.gen().write("PUSH 1 # Apilo el booleano true");
		else
			GenCode.gen().write("PUSH 0 # Apilo el booleano false");
	}
}