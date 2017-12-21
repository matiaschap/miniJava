package asyn.Semantic;

import gci.GenCode;

public class TipoString extends TipoPrim{

	public TipoString(int l) {
		nombre = "String";
		linea = l;
	}

	public boolean esCompatible(TipoMetodo t) {

		return (t.getNombre().equals(this.nombre)) ;
	}

	public boolean check() {
		return true;
	}

	public void gen(String s) {

		GenCode.gen().write(".DATA");
		
		String et = GenCode.gen().genLabel();

		GenCode.gen().write("l_str"+et+": DW \""+s+"\",0");
		GenCode.gen().write(".CODE");
		GenCode.gen().write("PUSH l_str"+et+" # Apilo etiqueta del String");	
	}
}