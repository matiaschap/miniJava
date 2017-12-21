package asyn.Semantic;

import asyn.Token;

public class Literal extends Expresion{

	public Literal(Token tok) {
		this.tok = tok;
	}

	public Tipo check() throws Exception{

		Tipo aux=null; 
		switch(tok.getName()) {
		case "null" : 
		{aux = new TipoClase(tok); break;}
		case "true" : 
		{ aux = new TipoBool(tok.getnLinea()); break;}
		case "false" : 
		{ aux = new TipoBool(tok.getnLinea()); break;}
		case "intLiteral" : 
		{ aux = new TipoInt(tok.getnLinea()); break;}
		//GenCode.gen().write("PUSH "+tok.getLexema()+" # Apilo el valor "+tok.getLexema());break;
		case "charLiteral" : 
		{ aux = new TipoChar(tok.getnLinea()); break;}
		case "stringLiteral" : 
		{ aux = new TipoString(tok.getnLinea()); break;}
		default : return null;
		}

		aux.gen(tok.getLexema());
		return aux;


	}
}