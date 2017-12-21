package asyn.Semantic;

import java.util.HashMap;
import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class Clase {
	private Token t;
	private String nombre;
	private String hereda;
	private Map<String, Variable> variables;
	private Map<String, Metodo> metodos;	
	private boolean tieneConst = false;
	private int linea;
	private boolean visitado,actualizado;
	private Metodo constructor;
	private boolean contructPredef;
	private int cantMetDyn = 0;
	private int id;


	public Clase(Token t) {
		this.t = t;
		nombre = t.getLexema();
		linea = t.getnLinea();
		hereda = "Object";
		variables = new HashMap<String, Variable>();
		metodos = new HashMap<String, Metodo>();
		visitado = false;
		actualizado = false;
		id = GenCode.contadorClase();
	}

	public Token getTok() {
		return t;
	}

	public void setTok(Token t) {
		this.t = t;
	}

	public String getNombre() {
		return nombre;
	}

	//	public String getHerencia(){
	//		return hereda;
	//	}

	public void addMetodo(Metodo m) throws Exception{
		if(!metodos.containsKey(m.getNombre())){
			metodos.put(m.getNombre() , m);
			if(m.getForma().equals("dynamic")){ 
				cantMetDyn++;
			}
		}
		else throw new Exception("Error semantico en linea "+m.getLinea()+" : El metodo "+m.getNombre()+" ya estaba declarado en la linea " + metodos.get(m.getNombre()).getLinea());

	}

	public void addMetodo(String s, Metodo m) throws Exception{
		if(!metodos.containsKey(m.getNombre())){
			metodos.put(s , m);
			if(m.getForma().equals("dynamic")) {
				cantMetDyn++;
			}
		}
		else throw new Exception("Error semantico: El metodo "+m.getNombre()+" ya estaba declarado en la linea " + metodos.get(m.getNombre()).getLinea());
	}

	public void addMetodo(Token tok,String formaMetodo,TipoMetodo retorno, Clase c){
		metodos.put(tok.getLexema() , new Metodo(tok, formaMetodo, retorno, c));
		if(formaMetodo.equals("dynamic")) {
			cantMetDyn++;
		}
	}

	public void addVaribale(Token tok, String visib,Tipo tip) throws Exception{
		if(!variables.containsKey(tok.getLexema()))
			variables.put(tok.getLexema(), new Variable(tok,visib,tip));
		else 				
			throw new Exception("Error semantico: Variable repetida: "+tok.getLexema()+ " en la linea "  +tok.getnLinea()+", ya estaba en la linea "+variables.get(tok.getLexema()).getLinea());
	}

	public void addVaribale(String k, Variable v) throws Exception{
		variables.put(k, v);
	}

	public Metodo addConstructor(Token t, Clase c){
		Metodo m = new Metodo(t,"dyanmic",new TipoVoid(), c);
		m.setClase(this);
		metodos.put(t.getLexema(), m);
		tieneConst = true;
		constructor = m;
		return m;
	}



	public String getHereda() {
		return hereda;
	}

	public void setHereda(String hereda) {
		this.hereda = hereda;
	}

	public boolean getTieneConst() {
		return tieneConst;
	}

	public void setTieneConst(boolean tieneConst) {
		this.tieneConst = tieneConst;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void generarConstPredef(Clase c){
		addConstructor(new Token("idMetVar",nombre,0),c);
		contructPredef = true;
	}

	public void chequearDeclaraciones(Clase c)  throws Exception{	
		if(!tieneConst) {
			generarConstPredef(c);
		}
		chequearMetodos();
		chequearHerencia();
		chequearVariables();
	}

	public void chequearMetodos() throws Exception{
		for (Metodo m : metodos.values()) {
			m.chequearDeclaraciones();
			if(m.getNombre().equals("main")) {
				if(!m.getForma().equals("static")) {
					throw new Exception("El metodo main en linea "+m.getLinea()+" de la clase "+nombre+" debe ser estatico");
				}
				if(m.getParams().size()==0) {//controlar mas de 1 main
					AnalizadorSyn.getTs().mainDeclarado=true;
					AnalizadorSyn.getTs().setMain(m);
				}
				else {
					throw new Exception("Error: el metodo main en la linea "+m.getLinea()+" tiene parametros");
				}
			}
		}

	}

	public boolean isVisitado() {
		return visitado;
	}

	public void setVisitado(boolean visitado) {
		this.visitado = visitado;
	}

	public void chequearHerencia() throws Exception{
		if(!getNombre().equals("Object")){
			if(!AnalizadorSyn.getTs().estaClase(hereda)){
				throw new Exception ("La clase "+hereda+" en la linea "+linea+ " de la que quiere heredar "+nombre+", no existe");
			}
			chequearCircular();
		}
	}

	private void chequearCircular() throws Exception {
		if(!actualizado) {
			if(!visitado){
				visitado = true;
				Clase padre=getPadre();
				padre.chequearHerencia();

				//System.out.println("Actualizando clase "+nombre );
				//System.out.println("Padre: "+padre.getNombre());
				setOffsets();
				//Agrego variables de mi ancestro

				for(String nv : padre.getVariables().keySet()) {
					Variable v = padre.getVariables().get(nv);
					//System.out.println("Visibilidad "+v.getVisibilidad());
					if (nv.charAt(0)!='@'){
						if(v.getVisibilidad().equals("private")) {
							nv = "@"+nv;
							//	System.out.println("agrego variable '"+nv+"' a la clase '"+nombre);
						}
					}
					//System.out.println("agrego variable "+nv+" a la clase "+nombre);
					addVaribale(nv,v);
				}
				//Agrego metodos de mi ancestro controlando que si redefini alguno este bien
				for(Metodo m : padre.getMetodos().values()) {
					//Si el metodo esta redefinido
					if(estaMetodo(m)) {
						if(!m.getForma().equals("private")) {
							//System.out.println("Chequeando metodo "+m.getNombre()+" de la clase "+nombre);
							Metodo actual = metodos.get(m.getNombre());
							//Chequeo que la cantidad y tipo de parametros sea igual
							if(igualesParametros(m.getParams(),actual.getParams())) {
								//Chequeo que el tipo de retorno sea igual	
								if(m.getTipo().esCompatible(actual.getTipo())) {
									//Chequeo que su forma sea igual
									if(!m.getForma().equals(actual.getForma())) {
										throw new Exception("Error semántico en la linea: "+m.getLinea()+" .El metodo "+m.getNombre()+" esta mal redefinido (forma de metodo diferente) en linea");//add nLinea
									}
									else {
										//System.out.println("Redefino el metodo "+m.getNombre()+" en la clase "+nombre);
										//int x = m.getOffset();
										//System.out.println("off "+x);
										actual.setOffset(m.getOffset());

									}
								}
								else {
									throw new Exception("Error semántico en la linea: "+m.getLinea()+" .El metodo "+m.getNombre()+" esta mal redefinido (tipo de retorno no compatible)");
								}
							}
							else {
								throw new Exception("Error semántico en la linea: "+m.getLinea()+" .El metodo "+m.getNombre()+" esta mal redefinido (parametros diferentes)");
							}
						}
					}
					//Si no esta redefinido lo agrego
					else {
						if(!m.equals(padre.getNombre())) //Si es el constructor no lo agrego
							addMetodo(m);
					}
				}
				setOffsetsMetodos();
				actualizado = true;
				visitado = false;
			}
			else //add linea
				throw new Exception ("Error semántico en la linea: "+linea+" .En la jerarquia de herencia de la clase "+nombre+" hay herencia circular");
		}
	}

	public void chequearVariables() throws Exception{
		for (Variable v : variables.values()) {
			if(metodos.containsKey(v.getNombre())) {
				throw new Exception("Error: la variable '"+v.getNombre()+"' en la linea "+v.getLinea()+", tiene el mismo nombre que el metodo en la linea "+metodos.get(v.getNombre()).getLinea());
			}
			if(!v.getTipo().check()){
				throw new Exception("Error: la variable '"+v.getNombre()+"' en la linea "+v.getLinea()+" esta declarada de un tipo inexistente ("+v.getTipo().getNombre()+").");
			}
		}
	}

	public void chequearSentencias() throws Exception {

		GenCode.gen().write("# Clase "+nombre);
		GenCode.gen().write("# Creo la VTable");
		GenCode.gen().nl();
		GenCode.gen().nl();

		GenCode.gen().write(".DATA");

		String ls="DW ";

//		for (int i = 0; i < cantMetDyn; i++) {
//			for (Metodo m : metodos.values()) {
//				if(m.getOffset()==i) {
//					ls+=m.getLabel()+",";
//				}
//			}
//		}
		
		int cant = 0;
			for (Metodo m : metodos.values()) {
				if (!m.getNombre().equals(m.getClaseImplementadora())) { // si no es ctor
					if (m.getForma().equals("dynamic")) {
						ls+=m.getLabel()+",";
						cant++;
					}
				}
			}

		if(cant>0) {
			ls = ls.substring(0,ls.length()-1); //Elimino la ultima coma
			GenCode.gen().write("VT_"+nombre+": "+ls);
		}
		else {
			GenCode.gen().write("VT_"+nombre+": NOP");
		}

		GenCode.gen().nl();
		GenCode.gen().nl();
		GenCode.gen().write(".CODE");


//		AnalizadorSyn.getTs().setMetodoActual(constructor);
//		constructor.chequearSentencias();

		for (Metodo m : metodos.values()) {
			AnalizadorSyn.getTs().setMetodoActual(m);

			if(m.getClase().getNombre().equals(AnalizadorSyn.getTs().getClaseActual().getNombre()))
				m.chequearSentencias();

		}
	}

	public Clase getPadre() {
		AnalizadorSyn.getTs();
		return TablaSimbolos.getClase(hereda);
	}

	public  Map<String, Variable> getVariables() {
		return variables;
	}

	public  void setVariables(Map<String, Variable> variables) {
		this.variables = variables;
	}

	public  Map<String, Metodo> getMetodos() {
		return metodos;
	}

	public  void setMetodos(Map<String, Metodo> metodos) {
		this.metodos = metodos;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public boolean isActualizado() {
		return actualizado;
	}

	public void setActualizado(boolean actualizado) {
		this.actualizado = actualizado;
	}

	public boolean estaVariable(Variable v) {
		return variables.containsKey(v.getNombre());
	}

	public boolean estaVariable(String s) {
		return variables.containsKey(s);
	}

	public boolean estaMetodo(Metodo m) {
		return metodos.containsKey(m.getNombre());
	}

	public boolean estaMetodo(String s) {
		return metodos.containsKey(s);
	}

	public boolean igualesParametros(Map<String, Parametro> p1, Map<String, Parametro> p2) throws Exception{
		if(p1.size()!=p2.size()) {
			return false;
		}
		for (Parametro p : p1.values()) {
			for (Parametro p3  : p2.values()) {
				if(p.getPosicion()==p3.getPosicion()) {
					if(!p.getTipo().esCompatible(p3.getTipo())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public Metodo getConstructor() {
		return constructor;
	}

	public void setConstructor(Metodo constructor) {
		this.constructor = constructor;
	}

	public Variable getVar(String s) {
		if(variables.containsKey(s)) {
			return variables.get(s);
		}
		else {
			return null;
		}
	}
	
	private void setOffsets() {
		AnalizadorSyn.getTs();
		Clase padre = TablaSimbolos.getClase(hereda);
		int cantAtrPadre = padre.getVariables().size();

		for (Variable v : variables.values()) {
			//cantAtrPadre++;
			v.setOffset(++cantAtrPadre);
		}

	}
	
	private void setOffsetsMetodos() {

		AnalizadorSyn.getTs();
		Clase padre = TablaSimbolos.getClase(hereda);

		AnalizadorSyn.getTs();
		int cantMetodosPadre = TablaSimbolos.getClase(hereda).getCantMetDyn();

		//System.out.println("Clase "+nombre+" cant de metodos de mi padre "+cantMetodosPadre);




		for (String s : metodos.keySet()) {
			if(s.charAt(0)!='@') {
				Metodo m = metodos.get(s);
				if(!m.getNombre().equals(nombre) && !m.getForma().equals("static")) {
					if(!padre.estaMetodo(m)) {
						m.setOffset(cantMetodosPadre++);
						//System.out.println("Clase "+nombre);
					}
				}
			}
		}
	}
	
	public int getCantMetDyn() {
		return cantMetDyn;
	}

	public void setCantMetDyn(int cantMetDyn) {
		this.cantMetDyn = cantMetDyn;
	}
	
	public int getId() {
		return id;
	}
}