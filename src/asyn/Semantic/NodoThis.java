package asyn.Semantic;

import asyn.*;
import gci.GenCode;

public class NodoThis extends Primario{

	public NodoThis(Token tok) {
		this.tok = tok;
		esLadoIzq = false;
	}

	public NodoThis(Token tok, Encadenado enc) {
		this.tok = tok;
		this.enc = enc;
	}

	public TipoMetodo check() throws Exception {
		if(AnalizadorSyn.getTs().getMetodoActual().getForma().equals("static")) {
			throw new Exception("Error en linea "+tok.getnLinea()+ " .No se puede utilizar This en un metodo estatico");
		}

		TipoMetodo tipoPri = new TipoClase(AnalizadorSyn.getTs().getClaseActual().getTok());

		GenCode.gen().write("LOAD 3 # Cargo el THIS");
		
		if (enc == null)
			return tipoPri;
		else{
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(tipoPri);
		}
	}

	public boolean terminaEnVar() {
		if (this.getEnc()==null)
			return false;
		else return this.getEnc().terminaEnVar();
	}

	public boolean terminaEnLlamada() {
		if (this.getEnc()==null)
			return false;
		else return this.getEnc().terminaEnLlamada();
	}
}