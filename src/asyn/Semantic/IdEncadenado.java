package asyn.Semantic;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class IdEncadenado extends Encadenado{

	public IdEncadenado(Token tok, Encadenado enc) {
		this.tok = tok;
		this.enc = enc;
		esLadoIzq = false;
	}

	public TipoMetodo check(TipoMetodo tipo) throws Exception {

		AnalizadorSyn.getTs();
		Clase c = TablaSimbolos.getClase(tipo.getNombre());
		String nombreVar = tok.getLexema();
		Variable v;

		if(tipo.getNombre().equals("int") || tipo.getNombre().equals("boolean") || tipo.getNombre().equals("char") || tipo.getNombre().equals("String")) {
			throw new Exception("La variable "+tok.getLexema()+" en la linea "+tok.getnLinea()+" no puede realizar llamadas ya que es de un tipo primitvo");
		}
		if(enc==null) { //Sin encadenado
			if(c.estaVariable(nombreVar)) {
				v = c.getVariables().get(nombreVar);
				if(c.getVar(nombreVar).getVisibilidad().equals("private")) {
					throw new Exception("No se puede acceder a la variable "+nombreVar+" en la linea "+tok.getnLinea()+" ya que es "+v.getVisibilidad());
				}
				GenCode.gen().write("LOADREF "+v.getOffset()+" # Cargo variable de instancia "+v.getNombre()+" de la clase "+c.getNombre());
			}
			else {
				throw new Exception("No se encontro la variable "+nombreVar+" que se intenta usar en la linea "+tok.getnLinea());
			}
			return v.getTipo();
		}
		else { //Con encadenado

			if(c.estaVariable(nombreVar)) {
				v = c.getVariables().get(nombreVar);
				if(c.getVar(nombreVar).getVisibilidad().equals("private")) {
					throw new Exception("No se puede acceder a la variable "+nombreVar+" en la linea "+tok.getnLinea()+" ya que es "+v.getVisibilidad());
				}
				GenCode.gen().write("LOADREF "+v.getOffset()+" # Cargo variable de instancia "+v.getNombre()+" de la clase "+c.getNombre());
				Tipo aux = v.getTipo();
				return enc.check(aux);
			}
			else {
				throw new Exception("No se encontro la variable "+nombreVar+" que se intenta usar en la linea "+tok.getnLinea());
			}
		}
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