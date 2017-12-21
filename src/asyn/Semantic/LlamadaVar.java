package asyn.Semantic;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class LlamadaVar extends Primario{

	public LlamadaVar(Token tok, Encadenado enc) {
		this.tok =tok;
		this.enc = enc;
		esLadoIzq = false;
	}

	public TipoMetodo check() throws Exception {

		Clase c = AnalizadorSyn.getTs().getClaseActual();
		Metodo m = AnalizadorSyn.getTs().getMetodoActual();
		String nombreVar = tok.getLexema();
		Variable v;
		TipoMetodo ret;
		v = m.getVars().get(nombreVar);
		//		if(m.estaVar(nombreVar)) {
		if (esLadoIzq() && enc==null){
			if (m.estaVar(nombreVar) || m.getParams().containsKey(nombreVar))
				GenCode.gen().write("STORE "+v.getOffset()+" # ");
			else {
				// var instancia
				if(c.estaVariable(nombreVar)) {
					if(!m.getForma().equals("static")) {
						v = c.getVar(nombreVar);
						GenCode.gen().write("LOAD 3 # Cargo This");
						GenCode.gen().write("SWAP");
						GenCode.gen().write("STOREREF "+v.getOffset()+" # Cargo el valor de la variable");
					} else 
						throw new Exception("Se intento referenciar a una variable de instancia en la linea "+tok.getnLinea()+" desde un metodo estatico");
				} else 
					throw new Exception("La variable "+nombreVar+" en la linea "+tok.getnLinea()+" no esta definida");
			}
		} else {
			if (m.estaVar(nombreVar) || m.getParams().containsKey(nombreVar))
				GenCode.gen().write("LOAD "+v.getOffset()+" # ");
			else {
				// var instancia
				if(c.estaVariable(nombreVar)) {
					if(!m.getForma().equals("static")) {
						v = c.getVar(nombreVar);
						GenCode.gen().write("LOAD 3 # Cargo This");
//						GenCode.gen().write("SWAP");
						GenCode.gen().write("LOADREF "+v.getOffset()+" # Cargo el valor de la variable");
					} else 
						throw new Exception("Se intento referenciar a una variable de instancia en la linea "+tok.getnLinea()+" desde un metodo estatico");
				} else 
					throw new Exception("La variable "+nombreVar+" en la linea "+tok.getnLinea()+" no esta definida");
			}
		}

		ret = v.getTipo();

		if(enc!=null) {
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(ret);
		}
		return ret;
	}

	public boolean terminaEnVar() {
		if (this.getEnc()==null)
			return true;
		else return this.getEnc().terminaEnVar();
	}

	public boolean terminaEnLlamada() {
		if (this.getEnc()==null)
			return false;
		else return this.getEnc().terminaEnLlamada();
	}
}