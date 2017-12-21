package asyn.Semantic;


import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class LlamadaConClase extends Primario{
	protected Token idMetVar;
	protected Argumentos args;

	public LlamadaConClase(Token t1, Token t2, Argumentos args,Encadenado enc) {
		this.enc = enc; 
		tok = t1;
		idMetVar = t2;
		this.args = args;
		esLadoIzq = false;
	}

	public TipoMetodo check() throws Exception {

		AnalizadorSyn.getTs();
		Clase c = TablaSimbolos.getClase(tok.getLexema());

		if(c == null) {
			throw new Exception("Error en linea "+tok.getnLinea()+" la clase "+tok.getLexema()+" no existe");
		}

		Metodo m = c.getMetodos().get(idMetVar.getLexema());

		if(m == null) {
			throw new Exception("El metodo "+idMetVar.getLexema()+" no esta definido para la calse" + c.getNombre()+" error en linea "+idMetVar.getnLinea());
		}
		else {
			validarArgs(m);
		}

		if(!m.getForma().equals("static")) {
			throw new Exception("El metodo "+idMetVar.getLexema()+" no es estatico en la clase "+c.getNombre()+". Error en linea "+idMetVar.getnLinea());
		}
		/*****************************************GENCODE*******************************************/
		if(!m.getTipo().getNombre().equals("void"))
			GenCode.gen().write("RMEM 1 # Reservo memoria para el retorno del metodo");
		GenCode.gen().write("PUSH "+m.getLabel()+" # Apilo la etiqueta del metodo");
		GenCode.gen().write("CALL # Llamo al metodo");
		/*************************************FIN GENCODE*******************************************/

		if(enc!=null) {
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(m.getTipo());
		}

		return m.getTipo();
	}

	private void validarArgs(Metodo m) throws Exception{

		Argumentos actual = args;
		//imprimirArgs(m);
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
//			actual = args.getArgs();
			 actual = actual.getArgs();
		}

		if(actual!=null)
			if(actual.getExp()!=null) {
				throw new Exception("Cantidad de parametros invalida para el metodo "+m.getNombre()+" en la linea "+args.getExp().getToken().getnLinea());
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
	
	public void imprimirArgs(Metodo m) {
		System.out.println("Metodo: "+m.getNombre());
		System.out.print("Argumentos: (");
		Argumentos actual = args;
		while(actual!=null) {
			if(actual.getExp()!=null)
				System.out.print(actual.getExp().getToken().getLexema()+", ");
			actual = actual.getArgs();
		}
		System.out.println(")");

		Map<String, Parametro> params = m.getParams();

		System.out.print("Parametros: (");
		for (int i = 0; i < params.size(); i++) {
			System.out.print(m.getParamPos(i).getNombre()+",");
		}
		System.out.println(")");
		System.out.println();
	}
	
}