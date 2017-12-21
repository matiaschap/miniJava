package asyn.Semantic;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;


public class Return extends Sentencia {

	private Expresion exp;

	public Return(Expresion exp, Token t) {
		this.exp = exp;
		linea = t.getnLinea();
	}

	public Expresion getExp() {
		return exp;
	}

	public void setExp(Expresion exp) {
		this.exp = exp;
	}
	public void check() throws Exception {

		Metodo m = AnalizadorSyn.getTs().getMetodoActual();
		Clase c = AnalizadorSyn.getTs().getClaseActual();

		if(m.getNombre().equals(c.getNombre())) {
			throw new Exception("No puede haber una sentencia return dentro de el constructor en linea "+exp.getToken().getnLinea());
		}
		if(exp != null) {
			TipoMetodo tipoExp = exp.check();
			if(!tipoExp.esCompatible(m.getTipo())) {
				throw new Exception("Se intenta retornar algo de tipo "+tipoExp.getNombre()+" pero el metodo "+m.getNombre()+" debe devolver algo de tipo "+m.getTipo().getNombre()+" en linea "+linea);
			}
			if(m.getForma().equals("static")) {
				GenCode.gen().write("STORE "+(3+m.getParams().size())+" # Guardo valor de retorno del metodo "+m.getNombre());
			}
			else {
				GenCode.gen().write("STORE "+(4+m.getParams().size())+" # Guardo valor de retorno del metodo "+m.getNombre());
			}
			if(m.getVars().size()-m.getParams().size() > 0) {
				GenCode.gen().write("FMEM "+(m.getVars().size()-m.getParams().size())+" # Libero espacio de variable locales al metodo "+m.getNombre());
			}
			GenCode.gen().write("STOREFP");

			if(m.getForma().equals("static")) { //Si es estatico
				GenCode.gen().write("RET "+m.getParams().size()+" # Retorno y libero espacio de los parametros del metodo "+m.getNombre());
			}
			else { //Si es dinamico
				GenCode.gen().write("RET "+(m.getParams().size()+1)+" # Retorno y libero espacio de los parametros del metodo y del THIS "+m.getNombre());
			}
		}
		else {
			if(!m.getTipo().getNombre().equals("void")) {
				throw new Exception("Error en linea "+linea+". El metodo "+m.getNombre()+" en linea "+m.getLinea()+" debe retornar algo de tipo "+m.getTipo().getNombre());
			}
		}
		//STORE ACA EN EL OFF DEL VALOR DE RET
		//LIBERAR ESPACIO DE VARS LOCALES VIVAS
		//STOREFP
		//RET
		m.setTieneReturn(true);
	}
}