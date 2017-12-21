package asyn.Semantic;

import asyn.Token;
import gci.GenCode;

public class ExpBinaria extends Expresion {

	protected Expresion exp1;
	protected Expresion exp2;

	public ExpBinaria(Token tok, Expresion exp1, Expresion exp2) {
		this.tok = tok;
		this.exp1 = exp1;
		this.exp2 = exp2;
	}

	public Expresion getExp1() {
		return exp1;
	}

	public void setExp1(Expresion exp1) {
		this.exp1 = exp1;
	}

	public Expresion getExp2() {
		return exp2;
	}

	public void setExp2(Expresion exp2) {
		this.exp2 = exp2;
	}

	public TipoMetodo check() throws Exception {
		
		TipoMetodo tipoEI = exp1.check();
		String op = tok.getLexema();
		TipoMetodo tipoED =null;
		if (!op.equals("instanceof"))
			tipoED = exp2.check();
		
		//	    System.out.println("Tipo expD: "+exp2.getToken().getLexema());
		//		System.out.println("Tipo expI: "+tipoEI.getNombre());
		//		System.out.println("Token: "+tok.getLexema());
		switch(op) {
		case "+" : case "-" : case "/": case "*" : case "%" :
		{
			if(!tipoED.getNombre().equals("int") || !tipoEI.getNombre().equals("int")) {
				throw new Exception("Error, los tipos de las expresiones en la linea "+tok.getnLinea()+" no son compatibles");
			}
			switch(op) {
			case "+" :{
				GenCode.gen().write("ADD # Suma");
				break;
			}
			case "-" :{
				GenCode.gen().write("SUB # Resta");
				break;
			}
			case "*" :{
				GenCode.gen().write("MUL # Multiplicacion");
				break;
			}
			case "/" :{
				GenCode.gen().write("Div # Division");
				break;
			}
			case "%" :{
				GenCode.gen().write("MOD # Modulo");
				break;
			}
			}
			return new TipoInt(tok.getnLinea());
		}
		case "<" : case ">" : case "<=" : case ">=" :
		{
			if(!tipoED.getNombre().equals("int") || !tipoEI.getNombre().equals("int")) {
				throw new Exception("Error, los tipos de las expresiones en la linea "+tok.getnLinea()+" no son compatibles");
			}
			switch(op) {
			case "<" :{
				GenCode.gen().write("LT # Menor");
				break;
			}
			case ">" :{
				GenCode.gen().write("GT # Mayor");
				break;
			}
			case ">=" :{
				GenCode.gen().write("GE # Mayor o igual");
				break;
			}
			case "<=" :{
				GenCode.gen().write("LE # Menor o igual");
				break;
			}
			}

			return new TipoBool(tok.getnLinea());
		}
		case "==" : case "!=" :
		{
			if(!tipoED.esCompatible(tipoEI)) {
				throw new Exception("Error, los tipos de las expresiones en la linea "+tok.getnLinea()+" no son compatibles");
			}
			switch(op) {
			case "==" :{
				GenCode.gen().write("EQ # Igual");
				break;
			}
			case "!=" :{
				GenCode.gen().write("GT # Not Igual");
				break;
			}
			}

			return new TipoBool(tok.getnLinea());
		}
		case "instanceof": 
			tipoED = new TipoClase(((Literal)exp2).getToken());
			if (!tipoED.check()) {
				throw new Exception("Error en la linea: "+tok.getnLinea()+". El tipo '"+tipoED.getNombre()+"' es invalido.");	
			} else if (!tipoED.esCompatible(tipoEI)) {
				throw new Exception("Error en la linea: "+tok.getnLinea()+". El tipo '"+tipoEI.getNombre()+"' no es compatible con '"+tipoED.getNombre()+"'.");
			} else {
				int id = TablaSimbolos.getClase(tipoED.getNombre()).getId();
				//    id = TablaSimbolos.clases.get(tipoED.getNombreClase()).getId();
				GenCode.gen().write("LOADREF 1 #Cargo el ID desde el CIR de la expresion analizada (izq)");
				GenCode.gen().write("PUSH "+id+"  #Cargo el ID de la Clase (der)");
				GenCode.gen().write("EQ #Comparo los IDs. El resultado queda en el tope de la pila");
				return new TipoBool(tipoED.getLinea());
			}
		default: //&& o ||
		{
			if(!tipoED.getNombre().equals("boolean") || !tipoEI.getNombre().equals("boolean")) {
				throw new Exception("Error, los tipos de las expresiones en la linea "+tok.getnLinea()+" no son compatibles");
			}
			switch(op) {
			case "&&" :{
				GenCode.gen().write("AND # And");
				break;
			}
			case "||" :{
				GenCode.gen().write("OR # Or");
				break;
			}
			}
			return new TipoBool(tok.getnLinea());
		}
		}
	}

}