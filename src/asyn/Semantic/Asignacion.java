package asyn.Semantic;

import asyn.Token;

public class Asignacion extends Sentencia{

	private Expresion exp;
	private Primario li;

	public Asignacion(Expresion exp, Primario li, Token t) {
		this.exp = exp;
		this.li = li;
		linea = t.getnLinea();
	}

	public void check() throws Exception {

		li.setEsLadoIzq(true);
		TipoMetodo tipoExp = exp.check();
		TipoMetodo tipoLi = li.check();

		if(tipoExp.esVoid()) 
			throw new Exception("Error en la linea "+linea+". La expresion debe devolver un tipo valido. El metodo llamado es void");
		
		if (li.terminaEnLlamada())
			throw new Exception("Error en la linea "+linea+". El lado izquierdo no puede terminar en una llamada.");

		if(!tipoExp.esCompatible(tipoLi)) {
			throw new Exception("Tipos no compatibles en la asignacion en linea "+linea);
		}


	}
}