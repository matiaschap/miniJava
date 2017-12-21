package asyn.Semantic;

import asyn.Token;
import gci.GenCode;

public class ExpUnaria extends Expresion {

	private Expresion exp;

	public ExpUnaria(Token tok, Expresion exp) {
		this.tok = tok;
		this.exp = exp;
	}


	public Expresion getExp() {
		return exp;
	}

	public void setExp(Expresion exp) {
		this.exp = exp;
	}

	public TipoMetodo check() throws Exception {

		TipoMetodo tipoExp = exp.check();
		//		System.out.println("go to check "+tipoExp.getNombre());

		switch (tok.getLexema()) {
		case "!":
		case "true":
		case "false":
			if(!tipoExp.getNombre().equals("boolean")) {
				throw new Exception("Error, el operador '!' no se puede aplicar a algo de tipo no booleano en linea "+tok.getnLinea());
			}
			GenCode.gen().write("NOT # Suma");
			return new TipoBool(tok.getnLinea());

		case "+":
		case "-": 	
			if(!tipoExp.getNombre().equals("int")) {
				throw new Exception("Error, el operador unario no se puede aplicar a algo de tipo no entero en linea "+tok.getnLinea());
			}
			GenCode.gen().write("NEG # Suma");
			return new TipoInt(tok.getnLinea());
		default: // CAST

			TipoClase tipoCx = new TipoClase(tok);
			if (!tipoCx.check()) 
				throw new Exception("Error, el tipo "+tok.getLexema()+" de la linea "+tok.getnLinea()+" no existe.");
			if (!tipoCx.esCompatible(tipoExp))
				throw new Exception("Error, el tipo "+tok.getLexema()+" no conforma con la subexpresion en la linea. "+tok.getnLinea());
			GenCode.gen().write("DUP  #Duplico la referencia al objeto");
			GenCode.gen().write("LOADREF 1  #Cargo el ID desde el CIR de la expresion analizada (der)");
			
			int id = TablaSimbolos.getClase(tok.getLexema()).getId();

			GenCode.gen().write("PUSH "+id+"  #Cargo el ID de la clase a castear (izq)");
			GenCode.gen().write("EQ  #Comparo los IDs");
			GenCode.gen().write("BF error_casting  #Casteo incorrecto. Lanzo excepcion");
			return tipoCx;	

		}



	}

}