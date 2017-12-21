package asyn.Semantic;

import gci.GenCode;

public class TipoChar extends TipoPrim{
	public TipoChar(int l) {
		nombre = "char";
		linea = l;
	}

	public boolean esCompatible(TipoMetodo t) {

		return (t.getNombre().equals(this.nombre));
	}

	public void gen(String s) {
		//System.out.println(s);
		GenCode.gen().write("PUSH "+ (int) s.charAt(0)+" # Apilo el caracter "+s.charAt(0));
	}

	public boolean check() {
		return true;
	}
}