package asyn.Semantic;

import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class LlamadaEncadenada extends Encadenado{
	protected Argumentos args;

	public LlamadaEncadenada(Token tok, Encadenado enc, Argumentos args) {
		this.tok = tok;
		this.enc = enc;
		this.args = args;
		esLadoIzq = false;
	}

	public Argumentos getArgs() {
		return args;
	}

	public void setArgs(Argumentos args) {
		this.args = args;
	}

	public TipoMetodo check(TipoMetodo tipo) throws Exception {

		TipoMetodo ret;

		if(args == null) { //Variable o clase
			if(tipo.getNombre().equals("int") || tipo.getNombre().equals("boolean") || tipo.getNombre().equals("char") || tipo.getNombre().equals("String")) {
				throw new Exception("La variable "+tok.getLexema()+" en la linea "+tok.getnLinea()+" no puede realizar llamadas ya que es de un tipo primitvo");
			}

			AnalizadorSyn.getTs();
			Clase c = TablaSimbolos.getClase(tipo.getNombre());

			if(c.estaVariable(tok.getLexema())) {
				Variable var = c.getVariables().get(tok.getLexema());
				ret = var.getTipo();
			}
			else {
				throw new Exception("La variable "+tok.getLexema()+" en la linea "+tok.getnLinea()+" no se encuentra declarada en la clase "+c.getNombre());
			}
		}
		else { //Llamada a un metodo

			AnalizadorSyn.getTs();
			Clase c = TablaSimbolos.getClase(tipo.getNombre());

			if(c == null) {
				throw new Exception("La clase a la que se le esta queriendo pedir el metodo '"+tok.getLexema()+"' en la linea "+tok.getnLinea()+" no existe");
			}


			if(c.estaMetodo(tok.getLexema())) {
				Metodo m = c.getMetodos().get(tok.getLexema());
				//chequear que metodo no sea privado

				/*****************************************GENCODE*******************************************/
				if(!m.getTipo().getNombre().equals("void")) {
					GenCode.gen().write("RMEM 1 # Reservo lugar para el retorno del metodo");

					if(m.getForma().equals("dynamic")) {
						GenCode.gen().write("SWAP");
					}
				}

				if(m.getForma().equals("static")) {
					GenCode.gen().write("POP");
				}

				validarArgs(m);

				if(m.getForma().equals("static")) {
					GenCode.gen().write("PUSH "+m.getLabel()+" # Apilo la etiqueta del metodo");
					GenCode.gen().write("CALL # Llamo al metodo");
				}
				else {
					GenCode.gen().write("DUP");
					GenCode.gen().write("LOADREF 0 # Cargo la VTable");
					GenCode.gen().write("LOADREF "+m.getOffset()+" # Cargo el metodo "+m.getNombre());
					GenCode.gen().write("CALL # Llamo al metodo");
				}

				ret = m.getTipo();
			}
			else {
				throw new Exception("El metodo "+tok.getLexema()+" en linea "+tok.getnLinea()+" no se encuentra declarado");
			}
		}
		if(enc != null) {
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

		//		if(actual!=null)
		//			if(actual.getExp()!=null) {
		//				throw new Exception("Cantidads de parametros invalida para el metodo "+m.getNombre()+" en la linea "+args.getExp().getToken().getnLinea());
		//			}

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