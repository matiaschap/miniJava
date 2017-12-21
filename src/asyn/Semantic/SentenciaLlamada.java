package asyn.Semantic;

import asyn.Token;
import gci.GenCode;

public class SentenciaLlamada extends Sentencia{
	private Primario p;

	public SentenciaLlamada(Primario p, Token t) {
		this.p = p;
		linea = t.getnLinea();
	}
	public Primario getLlamada() {
		return p;
	}

	public void setLlamada(Primario p) {
		this.p = p;
	}

	public void check() throws Exception{
		//p.check();
		TipoMetodo tm = p.check();
//		if (!p.terminaEnLlamada()) 
//			throw new Exception("Error Semántico: La sentencia no termina en una llamada en la linea "+linea);
		if(!tm.getNombre().equals("void")) {
			GenCode.gen().write("POP");
		}
	}
}