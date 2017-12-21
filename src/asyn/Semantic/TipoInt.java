package asyn.Semantic;

import gci.GenCode;

public class TipoInt extends TipoPrim{

	public TipoInt(int l) {
		nombre = "int";
		linea = l;
	}

	public boolean esCompatible(TipoMetodo t) {

		return (t.getNombre().equals(this.nombre));
	}

	public boolean check() {
		return true;
	}
	
	public void gen(String s) {
		GenCode.gen().write("PUSH "+s+" # Apilo el valor "+s);
	}

}