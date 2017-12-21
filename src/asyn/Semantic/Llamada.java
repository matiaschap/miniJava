package asyn.Semantic;


import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class Llamada extends Primario{
	protected Argumentos args;

	public Llamada(Token tok, Argumentos args, Encadenado enc) {
		this.enc = enc;
		this.args = args;
		this.tok = tok;
		esLadoIzq = false;
	}

	public TipoMetodo check() throws Exception {

		Clase c = AnalizadorSyn.getTs().getClaseActual();
		Metodo m;
		String nombreMet = tok.getLexema();
		TipoMetodo ret;

		if(c.estaMetodo(nombreMet)) {
			m = c.getMetodos().get(nombreMet);
			Metodo actual = AnalizadorSyn.getTs().getMetodoActual();
			if(actual.getForma().equals("static")) {
				if(!m.getForma().equals("static"))
					throw new Exception("No se puede realizar una llamada a un metodo dinamico desde un metodo estatico. Error en linea "+tok.getnLinea());
			}
			/*****************************************GENCODE*******************************************/
			if(!m.getTipo().getNombre().equals("void")) {
				GenCode.gen().write("RMEM 1 # Reservo lugar para el retorno del metodo");
				if(m.getForma().equals("dynamic")) {
					GenCode.gen().write("SWAP");
				}
			}
			/*******************************************************************************************/
			validarArgs(m);
			/*****************************************GENCODE*******************************************/
			if(m.getForma().equals("static")) {
				GenCode.gen().write("PUSH "+m.getLabel()+" # Apilo la etiqueta del metodo");
				GenCode.gen().write("CALL # Llamo al metodo");
			}
			else {
				GenCode.gen().write("LOAD 3 # Cargo THIS");
				GenCode.gen().write("DUP");
				GenCode.gen().write("LOADREF 0 # Cargo la VTable");
				GenCode.gen().write("LOADREF "+m.getOffset()+" # Cargo el metodo "+m.getNombre());
				GenCode.gen().write("CALL # Llamo al metodo");
			}
			/*******************************************************************************************/
			ret = m.getTipo();
		}
		else {
			throw new Exception("El metodo "+nombreMet+" no se encuentra declarado para la clase "+c.getNombre());
		}
		if(enc!=null) {
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(ret);
		}
		return ret;
	}


	private void validarArgs(Metodo m) throws Exception{

		Argumentos actual = args;
		Map<String, Parametro> params = m.getParams();

		for (int i = 0; i < params.size(); i++) {
			if(actual == null) {
				throw new Exception("Cantidad de parametros invalida para el metodo "+m.getNombre()+" en la linea "+args.getExp().getToken().getnLinea());
			}
			Parametro p = m.getParamPos(i);
			if(actual.getExp() == null) {
				throw new Exception("Faltan parametros en la llamada al metodo "+m.getNombre()+" en la linea "+tok.getnLinea());
			}
			TipoMetodo tActual = actual.getExp().check();
			if(!tActual.esCompatible(p.getTipo())) {
				throw new Exception("Los tipos de los parametros en la llamada al metodo "+m.getNombre()+" no se corresponden en la linea "+args.getExp().getToken().getnLinea());
			}
			if(m.getForma().equals("dynamic")) {
				GenCode.gen().write("SWAP");
			}
			actual = actual.getArgs();
		}
		if(actual!=null)
			if(actual.getExp()!=null) {
				throw new Exception("Cantidad de parametros invalida para el metodo "+m.getNombre()+" en la linea "+args.getExp().getToken().getnLinea());
			}

	}

	public boolean terminaEnVar() {
		if (this.getEnc()==null)
			return false;
		else return this.getEnc().terminaEnVar();
	}

	public boolean terminaEnLlamada() {
		if (this.getEnc()==null)
			return true;
		else return this.getEnc().terminaEnLlamada();
	}
}