package asyn;

import java.util.ArrayList;

import asyn.Semantic.*;
import exceptions.*;

public class AnalizadorSyn {

	private Token lookAhead;
	private AnalizadorLexico aLex;
	static TablaSimbolos ts;

	public AnalizadorSyn(AnalizadorLexico aLex) {
		this.aLex = aLex;
		ts = new TablaSimbolos();
	}
	/**
	 * Inicia el proceso de análisis obteniendo el primer token y procesando la
	 * regla inicial de la gramática.
	 * @throws Exception 
	 */

	public void analize() throws  LexicalError, SyntaxError, ExceptionSem, Exception  {

		try {
			lookAhead = aLex.getToken();		
			inicial();
			ts.check();
			System.out.println("La Sintáxis y Semántica del programa es correcta.");
		} catch (LexicalError e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
		} catch (SyntaxError e2) {
			System.out.println(e2.getMessage());
			e2.printStackTrace();
		} catch (ExceptionSem e3) {
			System.out.println(e3.getMessage());
			e3.printStackTrace();
		}catch (Exception e4) {
			System.out.println(e4.getMessage());
			e4.printStackTrace();
		}
	}

	public static TablaSimbolos getTs() {
		return ts;
	}

	/**
	 * Compara el token encontrado con lo que se esperaba de acuerdo a la
	 * gramatica. En caso de encontrar un token no esperado o el fin de linea
	 * durante el proceso de analisis, se procedera a detenerlo y a notificar el
	 * error.
	 *
	 * @param token
	 * @throws LexicalError
	 * @throws SyntaxError
	 */
	public void match(String token) throws LexicalError, SyntaxError {
		if (lookAhead.equals(token)) {
			if (!token.equals("EOF")) {
				lookAhead = aLex.getToken();
			} else {
				System.err.println("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se alcanzo el fin de archivo durante el analisis sintactico.");
			}
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba: '" + token + "'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	// Todos los metodos que siguen corresponden a las reglas de la gramatica de MiniJava
	// Existe un metodo por no-terminal de la gramatica, y el proceso se lleva adelante siguiendo el flujo de ejecucion normal
	private void inicial() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		clase();
		listaClases();
	}

	private void listaClases() throws LexicalError, SyntaxError, ExceptionSem, Exception { 
		if (lookAhead.equals("class")) 
			inicial();
		else if (lookAhead.equals("EOF")) {

		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la palabra reservada 'class'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void clase() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Token aux;
		if (lookAhead.equals("class")) {
			match("class");
			aux = lookAhead;
		}else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la palabra reservada 'class'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
		if (!lookAhead.equals("IdClase")) {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el nombre de la clase. Se encontro: '" + lookAhead.getLexema() + "'.");
		}

		match("IdClase");
		String h= herencia();
		Clase c;
		if(!ts.estaClase(aux.getLexema())){
			c = new Clase(aux);
			ts.addClase(c);
			//System.out.println("Se agrego la clase: "+c.getNombre());
			c.setHereda(h);
		}
		else 
			throw new ExceptionSem("Linea: "  +aux.getnLinea()+" - Error Semantico: Se declaró nuevamente la clase " +aux.getLexema());

		// el control de { lo hace herencia()
		match("{");
		listaMiembros();
		// el control de } lo hace listaMiembros()
		match("}");
	}

	private String herencia() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("extends")) {
			match("extends");
			String h = lookAhead.getLexema();
			match("IdClase");
			return h;
		} else if (lookAhead.equals("{")) {
			return "Object";
			// Herencia -> lambda
			// No hay herencia 
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el comienzo de la clase '{' o la especificacion de herencia. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void listaMiembros() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("}")) {
			// ListaMiembros -> lambda
			// No hay mas miembros
		} else {
			miembro();
			listaMiembros();
			// Para no duplicar codigo el control lo realiza Miembro()
		}
	}

	private void miembro() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("public") || lookAhead.equals("private")) {
			atributo();
		} else if (lookAhead.equals("IdClase")) {
			ctor();
		} else if (lookAhead.equals("static") || lookAhead.equals("dynamic")) {
			metodo();
		} else if (isType(lookAhead)) {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: No se encontro el modificador del metodo.");
		} else if (lookAhead.equals("EOF")) {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el cierre de la clase '}'. Se encontro: '" + lookAhead.getLexema() + "'.");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la definicion de atributos, constructores o metodos. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}
	/**
	 * Control de declaracion de metodos
	 *
	 * @param lookAhead
	 * @return true si se trata de un tipo, false en caso contrario
	 */
	private boolean isType(Token lookAhead) {
		return lookAhead.equals("void") || lookAhead.equals("boolean") || lookAhead.equals("char") || lookAhead.equals("int") || lookAhead.equals("String") || lookAhead.equals("IdClase");
	}

	private void atributo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		String v = visibilidad(); 
		Tipo tip = tipo();
		ArrayList<Token> l = listaDecVars(new ArrayList<Token>());
		for (Token token : l) {
			ts.getClaseActual().addVaribale(token, v, tip);
			//System.out.println("Se agrego: "+token.getLexema()+" a la clase: "+ts.getClaseActual().getNombre() + " con tipo "+tip.getNombre());
		}
		match(";");
	}

	private void metodo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		String f = formaMetodo();
		TipoMetodo tipo = tipoMetodo();
		Token aux = lookAhead;
		match("idMetVar");
		Metodo m = new Metodo(aux,f,tipo,ts.getClaseActual());
		ts.getClaseActual().addMetodo(m);
		m.setClase(ts.getClaseActual());
		ts.setMetodoActual(m);
		argsFormales(); 
		m.setCuerpo(bloque());
	}

	public String formaMetodo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("static")) {
			match("static");
			return "static";
		} else if (lookAhead.equals("dynamic")) {
			match("dynamic");
			return "dynamic";
		}else {
			throw new SyntaxError("Linea: "  +lookAhead.getnLinea() +" - Error sintactico: Se esperaba dynamic|static. Se encontro: "+ lookAhead.getLexema());
		}
	}

	private void ctor() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Token aux = lookAhead;
		match("IdClase");
		if(!aux.getLexema().equals(ts.getClaseActual().getNombre())){
			throw new Exception("Linea: "  +lookAhead.getnLinea() +" - Error semantico: el constructor no tiene el mismo nombre que la clase, se encontro '" +aux.getLexema()+ "' se esperaba '"+ts.getClaseActual().getNombre()+"'.");
		}
		if(!ts.getClaseActual().getTieneConst()){
			Metodo m = ts.getClaseActual().addConstructor(aux,ts.getClaseActual());
			ts.setMetodoActual(m);
		}
		else {
			throw new Exception("Error semantico: la clase ya tiene un constructor definido y se encontro otro en la linea "  +aux.getnLinea());
		}
		argsFormales();
		ts.getMetodoActual().setCuerpo(bloque());
	}

	private String visibilidad() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("public")) {
			match("public");
			return "public";
		} else if (lookAhead.equals("private")) {
			match("private");
			return "private";
		}else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba public | private. Se encontro: "+ lookAhead.getLexema() + "'.");
		}
	}

	private void argsFormales() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("(")) {
			match("(");
			argsFormalesP();
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la apertura de una lista de argumentos formales '('. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void argsFormalesP() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(")")) {
			// ArgsFormales_ -> )
			// No hay mas argumentos formales
			match(")");
		} else {
			listaArgsFormales();
			if (lookAhead.equals(")")) {
				match(")");
			} else {
				throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el cierre de la lista de argumentos formales ')'. Se encontro: '" + lookAhead.getLexema() + "'.");
			}
		}
	}

	private void listaArgsFormales() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		argFormal();
		listaArgsFormalesP();
	}

	private void listaArgsFormalesP() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(")")) {
			// ListaArgsFormalesP -> lambda
			// No hay mas argumentos formales
		} else if (lookAhead.equals(",")) {
			match(",");
			listaArgsFormales();
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba un nuevo argumento formal o el cierre de la lista de argumentos formales ')'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void argFormal() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Tipo tip = tipo();
		Token aux = lookAhead;
		match("idMetVar");
		ts.getMetodoActual().addParametro(aux, tip);
	}

	private TipoMetodo tipoMetodo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("void")) {
			match("void");
			return new TipoVoid();
		} else {
			return tipo();
		}
	}

	private Tipo tipo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("IdClase")){
			Token aux = lookAhead;
			match("IdClase");
			return new TipoClase(aux);
		}else 
			return tipoPrimitivo();

	}

	private TipoPrim tipoPrimitivo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Token aux = lookAhead;
		switch (lookAhead.getLexema()) {
		case "boolean":
			match("boolean");
			return new TipoBool(aux.getnLinea());
		case "char":
			match("char");
			return new TipoChar(aux.getnLinea());
		case "int":
			match("int");
			return new TipoInt(aux.getnLinea());
		case "String":
			match("String");
			return new TipoString(aux.getnLinea());
		default:
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: En el típo de la definición de método. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private ArrayList<Token> listaDecVars(ArrayList<Token> l) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		l.add(lookAhead);
		if (lookAhead.equals("idMetVar")) {
			match("idMetVar");
		} else if (lookAhead.equals(",")) {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Falta especificar variables.");                                                                                                                                                
		} else if (lookAhead.equals(";")) {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Falta especificar variables.");                                                                                                                                                 
		}
		return listaDecVarsP(l);

	}

	private ArrayList<Token> listaDecVarsP(ArrayList<Token> l) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(";")) {
			return l;
			// ListaDecVarsP -> lambda
		} else if (lookAhead.equals(",")) {
			match(",");
			if (!lookAhead.equals("idMetVar")) {
				throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaban más atributos. Se encontro: '" + lookAhead.getLexema() + "'.");
			} else
				return listaDecVars(l);
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el terminador de la lista de variables ';'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}

	}

	private Bloque bloque() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("{")) {
			match("{");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la apertura de un bloque '{'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
		Bloque b = new Bloque();
		Bloque aux = ts.getBloqueActual();
		ts.setBloqueActual(b);
		listaSentencias();
		if (lookAhead.equals("}")) {
			match("}");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el cierre de un bloque '}'. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
		ts.setBloqueActual(aux);
		return b;
	}

	private void listaSentencias() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("}")) {
			// Listasentencias -> lambda
			// No hay mas sentencias
		} else {
			Sentencia s = sentencia();
			// El control de terminales o no-terminales lo realiza sentencia()
			ts.getBloqueActual().addSentencia(s);
			listaSentencias();
		}
	}

	private Sentencia sentencia() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		switch (lookAhead.getName()) {
		case ";":
			match(";");
			return new SentenciaVacia();
		case "(": case "this": case "idMetVar": case "new":
			Token t = lookAhead;
			Primario p= primario();
			if (lookAhead.equals(";")) {
				match(";");
				return new SentenciaLlamada(p,t);
			} else {
				Asignacion a = asignacion(p);
				match(";");
				return a;
			}
		case "boolean": case "char": case "int": case "String": case "IdClase":
			Tipo tip = tipo();
			ArrayList<Token> vars = listaDecVars(new ArrayList<Token>());
			match(";");
			ArrayList<Variable> vars2 = new ArrayList<Variable>();
			for (Token tok : vars) {
				Variable v = new Variable(tok,"public", tip);
				vars2.add(v);
				ts.getBloqueActual().addVariable(v);
			}
			return new DecVars(tip, vars2);
		case "if":
			match("if");
			match("(");
			Expresion e = expresion();
			match(")");
			Sentencia s = sentencia();
			return sentenciaElse(e,s);
		case "while":
			match("while");
			match("(");
			e = expresion();
			match(")");
			s = sentencia();
			return new While(e,s);
		case "{":
			return bloque();
		case "return":
			Token aux = lookAhead;
			match("return");
			e = sentenciaP();
			match(";");
			return new Return(e,aux);
		case "=":
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Falta el lado izquierdo de la asignacion. Se encontro: '" + lookAhead.getLexema() + "'.");
		default:
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba una sentencia o el cierre de bloque. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	//	private void cuerpo() throws LexicalError, SyntaxError, ExceptionSem, Exception {
	//		match("(");
	//		expresion();
	//		match(")");
	//		sentencia();
	//	}

	private If sentenciaElse(Expresion e, Sentencia s1) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("else")) {
			match("else");
			Sentencia s2 = sentencia();
			return new Else(e,s1,s2);
		} else {
			// sentenciaP -> lambda
			// if-then sin else
			return new If(e,s1);
		}
	}

	private Expresion sentenciaP() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(";")) {
			return null;
			// sentenciaP -> lambda
		} else {
			return expresion();
		}
	}
	//	private void sentenciaLlamada() throws LexicalError, SyntaxError, ExceptionSem, Exception {
	//		primario();
	//		asignacion();
	//	}

	private Asignacion asignacion(Primario izq) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("=")) {
			Token aux = lookAhead;
			match("=");
			Expresion der = expresion();
			//izq convertir a Idizq!!!!

			return new Asignacion(der,izq,aux);
		}
		return null;// se podría consultar por el ";"
	}

	private Expresion expresion() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpAnd();
		return ExprP(e);
	}

	private Expresion ExprP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("||")) {
			Token t2 = lookAhead;
			match("||");
			Expresion e2 = ExpAnd();
			Expresion aux = new ExpBinaria(t2,e,e2);
			return ExprP(aux); 
			//	else  
			//	throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba || , ) o ;. Se encontro: '" + lookAhead.getLexema() + "'.");
		} else {
			return e;
			// ExprP -> lambda
		}
	}

	private Expresion ExpAnd() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpIg();
		return ExpAndP(e);
	}

	private Expresion ExpAndP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("&&")) {
			Token t2 = lookAhead;
			match("&&");
			Expresion e2 = ExpIg();
			Expresion aux = new ExpBinaria(t2,e,e2);
			return ExpAndP(aux);
		}
		else {
			return e;
			// ExpAndP -> lambda
		}
	}

	private Expresion ExpIg() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpComp();
		return ExpIgP(e);		
	}

	private Expresion ExpIgP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("==") || lookAhead.equals("!=")) {
			Token t2 = lookAhead;
			OpIg();
			Expresion e2 = ExpComp();
			Expresion aux = new ExpBinaria(t2,e,e2);
			return ExpIgP(aux);
		}
		else {
			return e;
			// ExpIgP -> lambda
		}
	}

	private Expresion ExpComp() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpAd();
		return ExpCompP(e);
	}

	private Expresion ExpCompP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("<") || lookAhead.equals(">") || lookAhead.equals("<=") || lookAhead.equals(">=")) {
			Token t2 = lookAhead;
			OpComp();
			Expresion e2 = ExpAd();
			return new ExpBinaria(t2,e,e2);
		} else if (lookAhead.equals("instanceof")) {
			Token t2 = lookAhead;
			match("instanceof");
			Expresion e2 = new Literal(lookAhead);
			match("IdClase");	
			return new ExpBinaria(t2,e,e2);
		}else {
			return e;
			// ExpCompP -> lambda
		}
	}

	private Expresion ExpAd() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpMul();
		return ExpAdP(e);
	}

	private Expresion ExpAdP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("+") || lookAhead.equals("-")) {
			Token t2 = lookAhead;
			OpAd();
			Expresion e2 = ExpMul();
			Expresion aux = new ExpBinaria(t2,e,e2);
			return ExpAdP(aux);
		}
		else {
			return e;
			// ExpAdP -> lambda
		}
	}

	private Expresion ExpMul() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = ExpUn();
		return ExpMulP(e);
	}

	private Expresion ExpMulP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("*") || lookAhead.equals("/") || lookAhead.equals("%")) {
			Token t2 = lookAhead; 
			OpMul();
			Expresion e2 = ExpUn();
			Expresion aux = new ExpBinaria(t2,e,e2);
			return ExpMulP(aux);
		}
		else {
			return e;
			// ExpMulP -> lambda
		}
	}

	private Expresion ExpUn() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("!") || lookAhead.equals("+") || lookAhead.equals("-")) {
			Token t2 = lookAhead;
			OpUn();
			Expresion e2 = ExpUn();
			return new ExpUnaria(t2,e2);
		} else {
			return expCast();
		}
	}

	private Expresion expCast() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Token t = lookAhead;
		if (lookAhead.equals("[")) {
			match("[");
			t = lookAhead;
			match("IdClase");
			match("]");
			return new ExpUnaria(t, operando());
		} 
		return operando();

	}

	private void OpIg() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("==")) {
			match("==");
		} else if (lookAhead.equals("!=")) {
			match("!=");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba == o != . Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void OpComp() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		switch (lookAhead.getLexema()) {
		case "<":
			match("<");
			break;
		case ">":
			match(">");
			break;
		case "<=":
			match("<=");
			break;
		case ">=":
			match(">=");
			break;
		default:
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba <, >, <= o >= . Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void OpAd() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("+")) {
			match("+");
		} else if (lookAhead.equals("-")) {
			match("-");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba + o - . Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void OpUn() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("+")) {
			match("+");
		} else if (lookAhead.equals("-")) {
			match("-");
		} else if (lookAhead.equals("!")) {
			match("!");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba !, + o - . Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private void OpMul() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("*")) {
			match("*");
		} else if (lookAhead.equals("/")) {
			match("/");
		} else if (lookAhead.equals("%")) {
			match("%");
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba *, / o % . Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private Expresion operando() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		switch (lookAhead.getName()) {
		case "null": 	   case "true": 	   case "false": 
		case "intLiteral": case "charLiteral": case "stringLiteral":
			return literal();
		case "(": 		 case "this": 	   
		case "idMetVar": case "new": 
			return primario();
		default:
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba un operando. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private Expresion literal() throws LexicalError, SyntaxError, ExceptionSem, Exception {			
		Token t2 = lookAhead;
		switch (lookAhead.getName()) {
		case "null": 
			match("null");
			return new Literal(t2);
		case "true": 
			match("true");
			return new Literal(t2);
		case "false": 
			match("false");
			return new Literal(t2);
		case "intLiteral":
			match("intLiteral");
			return new Literal(t2);
		case "charLiteral": 
			match("charLiteral");
			return new Literal(t2);
		case "stringLiteral": 
			match("stringLiteral");
			return new Literal(t2);	
		default: //nunca va a entrar!!!	
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba un literal. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private Primario primario() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		switch (lookAhead.getName()) {
		case "(":
			match("(");
			Primario p = primarioP();
			match(")");
			Encadenado enc = llamadaoIdEnc();
			return new ExpConEncadenado(p,enc);
		case "this":
			Token t2 = lookAhead;
			match("this");
			if (lookAhead.equals(".")) {
				enc = llamadaoIdEnc();
				return new NodoThis(t2,enc);
			} else { 
				return new NodoThis(t2);
			}			
		case "idMetVar":
			t2 = lookAhead;
			match("idMetVar");
			return primarioPP(t2);
		case "new":
			match("new");
			Token t5 = lookAhead;
			match("IdClase");
			Argumentos args = argsActuales();
			enc = llamadaoIdEnc();
			return new Construir(t5,args,enc);
		default:
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba un primario. Se encontro: '" + lookAhead.getLexema() + "'.");
		}

	}

	private Primario primarioP() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("IdClase")) {
			Token t = lookAhead;
			match("IdClase");
			match(".");
			Token t1 = lookAhead;
			Argumentos args = llamada();
			Encadenado enc = llamadaoIdEnc();
			return new LlamadaConClase(t,t1,args,enc);
		}else{
			Expresion e = expresion();
			//match(")");
			Encadenado enc = llamadaoIdEnc();
			return new ExpConEncadenado(e,enc);
		}
	}

	private Primario primarioPP(Token t) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("(")) {
			Argumentos a = argsActuales();
			Encadenado enc = llamadaoIdEnc();
			return new Llamada(t,a,enc);
		}
		else {
			Encadenado enc = llamadaoIdEnc();
			return new LlamadaVar(t,enc);
		}
	}

	private Encadenado llamadaoIdEnc() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(".")) {
			Encadenado enc = llamadaoIdEncadenado();
			Encadenado enc2 = llamadaoIdEnc();
			enc.setEnc(enc2);
			return enc;
		} else { 
			return null;
			// ListaLlamadas -> lambda
		}
	}

	private Encadenado llamadaoIdEncadenado() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Token t2;
		match(".");
		t2 = lookAhead;
		match("idMetVar");
		return LlamadaoIdEncadenadoP(t2);
	}

	private Encadenado LlamadaoIdEncadenadoP(Token t2) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("(")) {
			Argumentos args = argsActuales();
			return new LlamadaEncadenada(t2,null,args);
		} else {
			return new IdEncadenado(t2,null);
			// LlamadaoIdEncadenadoP -> lambda
		}
	}

	private Argumentos llamada() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("idMetVar")) {
			match("idMetVar");
			return argsActuales();
		} else
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba un identificador de metodos o variable'. Se encontro: '" + lookAhead.getLexema() + "'.");
	}

	private Argumentos argsActuales() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals("(")) {
			match("(");
			return argsActualesP();
		} else {
			throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba la apertura de una lista de argumentos actuales '('. Se encontro: '" + lookAhead.getLexema() + "'.");
		}
	}

	private Argumentos argsActualesP() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(")")) {
			// ArgsActuales_ -> )
			// No hay mas argumentos actuales
			match(")");
			return new Argumentos(null,null);
		} else {
			Argumentos args = listaExps();
			if (lookAhead.equals(")")) {
				match(")");
				return args;
			} else {
				throw new SyntaxError("Linea: " + lookAhead.getnLinea() + " - Error sintactico: Se esperaba el cierre de la lista de argumentos actuales ')'. Se encontro: '" + lookAhead.getLexema() + "'.");
			}
		}
	}

	private Argumentos listaExps() throws LexicalError, SyntaxError, ExceptionSem, Exception {
		Expresion e = expresion();
		return listaExpsP(e);
	}

	private Argumentos listaExpsP(Expresion e) throws LexicalError, SyntaxError, ExceptionSem, Exception {
		if (lookAhead.equals(",")) {
			match(",");
			Argumentos ar = listaExps();
			return new Argumentos(e,ar);
		} else {
			return new Argumentos(e,null);
			// ListaExps_ -> lambda
		}
	}
}