package asyn.Semantic;

import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class Construir extends Primario{
	protected Argumentos args;

	public Construir(Token tok, Argumentos args, Encadenado enc) {
		this.tok = tok;
		this.args = args;
		this.enc = enc;
		esLadoIzq = false;
	}

	public TipoMetodo check() throws Exception {

		AnalizadorSyn.getTs();
		Clase c = TablaSimbolos.getClase(tok.getLexema());

		if(c == null) {
			throw new Exception("Error en la linea "+tok.getnLinea()+" la clase "+tok.getLexema()+ " no esta declarada");
		}
		GenCode.gen().write("RMEM 1 # Reservo lugar para el constructor de "+c.getNombre());
		GenCode.gen().write("PUSH "+(c.getVariables().size()+2)+" # Reservo lugar para variables de instancia y VT de la clase " + c.getNombre());
		GenCode.gen().write("PUSH lmalloc # Apilo la etiqueta del lmalloc");
		GenCode.gen().write("CALL # Llamada al metodo malloc");
		GenCode.gen().write("DUP");
		GenCode.gen().write("PUSH VT_"+c.getNombre()+" # Apilo direccion de la VTable de la clase "+c.getNombre());
		GenCode.gen().write("STOREREF 0 # Guardamos la Referencia a la VT en el CIR que creamos");
		GenCode.gen().write("DUP");

		Metodo m = c.getConstructor();

		validarArgs(m);

		if(enc != null) {
			enc.setEsLadoIzq(this.esLadoIzq());
			return enc.check(new TipoClase(tok));
		}

		GenCode.gen().write("PUSH "+m.getLabel()+" # Apilo etiqueta del constructor");
		GenCode.gen().write("CALL # Llamo al constructor");

		return new TipoClase(tok);

	}

	private void validarArgs(Metodo m) throws Exception{
		//imprimirArgs(m);
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
			actual = actual.getArgs();
			GenCode.gen().write("SWAP");
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