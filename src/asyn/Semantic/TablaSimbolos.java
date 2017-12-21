package asyn.Semantic;

import java.util.HashMap;
import java.util.Map;
import asyn.Token;
import gci.GenCode;


public class TablaSimbolos {

	private static Map<String, Clase> clases;
	private Clase claActual;
	private Metodo metActual;
	private Bloque bloActual;
	public boolean mainDeclarado = false;
	private Metodo main;

	public TablaSimbolos() {
		clases = new HashMap<String,Clase>();
		claActual = null;
		metActual = null;
		bloActual = null;
		addClaseObjet();
		try {
			addClaseSystem();
		}
		catch(Exception e) {
			System.out.println("Error al generar la clase System");
		}
	}

	public Bloque getBloqueActual() {
		return bloActual;
	}	
	public void setBloqueActual(Bloque bloActual) {
		this.bloActual = bloActual;
	}	
	public static Clase getClase(String clase){
		return clases.get(clase);
	}

	public void addClase(Clase clase){
		clases.put(clase.getNombre(), clase);
		claActual = clase;
	}

	public boolean estaClase(String clase){
		return clases.containsKey(clase);
	}

	private void addClaseObjet() {
		Clase c = new Clase(new Token("IdClase","Object",0));
		c.addConstructor(new Token("IdClase","Object",0),claActual);
		clases.put("Object", c);
		c.setActualizado(true);
		c.setVisitado(true);
	}

	private void addClaseSystem() throws Exception{
		Clase c = new Clase(new Token("IdClase","System",0));
		c.addConstructor(new Token("IdClase","Object",0),claActual);
		c.setHereda("Object");

		Metodo m = new Metodo(new Token("idMetVar","read",0),"static",new TipoInt(0),claActual);
		m.setClase(c);

		m.setOffset(0);
		String read = "READ\nPUSH 48\nSUB\nSTORE 3\nSTOREFP\nRET 0";
		m.setCuerpo(new BloqueAux(read));

		m.setTieneReturn(true);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar", "printB",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","b",0), new TipoBool(0));
		m.setClase(c);
		m.setOffset(1);
		String printB = "LOAD 3\nBPRINT";
		m.setCuerpo(new BloqueAux(printB));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printC",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","c",0), new TipoChar(0));
		m.setClase(c);
		m.setOffset(2);
		String printC = "LOAD 3\nCPRINT";
		m.setCuerpo(new BloqueAux(printC));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printI",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","i",0), new TipoInt(0));
		m.setClase(c);
		m.setOffset(3);
		String printI = "LOAD 3\nIPRINT";
		m.setCuerpo(new BloqueAux(printI));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printS",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","s",0), new TipoString(0));
		m.setClase(c);
		m.setOffset(4);
		String printS = "LOAD 3\nSPRINT";
		m.setCuerpo(new BloqueAux(printS));
		m.setOffParam(1);
		c.addMetodo(m);		

		m = new Metodo(new Token("idMetVar","println",0), "static", new TipoVoid(),claActual);
		m.setClase(c);
		m.setOffset(5);
		String println = "PRNLN";
		m.setCuerpo(new BloqueAux(println));
		m.setOffParam(0);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printBln",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","b",0), new TipoBool(0));
		m.setClase(c);
		m.setOffset(6);
		String printBln = printB + '\n' + println;
		m.setCuerpo(new BloqueAux(printBln));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printCln",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","c",0), new TipoChar(0));
		m.setClase(c);
		m.setOffset(7);
		String printCln = printC + '\n' + println;
		m.setCuerpo(new BloqueAux(printCln));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printIln",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","i",0), new TipoInt(0));
		m.setClase(c);
		m.setOffset(8);
		String printIln = printI + '\n' + println;
		m.setCuerpo(new BloqueAux(printIln));
		m.setOffParam(1);
		c.addMetodo(m);

		m = new Metodo(new Token("idMetVar","printSln",0), "static", new TipoVoid(),claActual);
		m.addParametro(new Token("","s",0), new TipoString(0));
		m.setClase(c);
		m.setOffset(9);
		String printSln = printS + '\n' + println;
		m.setCuerpo(new BloqueAux(printSln));
		m.setOffParam(1);
		c.addMetodo(m);


		c.setActualizado(true);
		c.setVisitado(true);
		clases.put("System", c);
	}

	public Clase getClaseActual() {
		return claActual;
	}	
	public void setClaseActual(Clase claActual) {
		this.claActual = claActual;
	}	
	public Metodo getMetodoActual() {
		return metActual;
	}	
	public void setMetodoActual(Metodo metActual) {
		this.metActual = metActual;
	}

	public void check() throws Exception{
		chequearDeclaraciones();
		chequearSentencias();
	}

	public void chequearDeclaraciones() throws Exception{

		for (Clase c : clases.values()) {
			if(!c.getNombre().equals("Object") && !c.getNombre().equals("System")) {
				c.chequearDeclaraciones(claActual);
			}
		}
		if(!mainDeclarado) {
			throw new Exception("No hay un metodo main declarado en ninguna clase");
		}
		//Descomentar esto para imprimir todas las clases con sus metodos
		//imprimirClases();
	}

	public void chequearSentencias() throws Exception{

		GenCode.gen().comment("<<<<< Inicializacion >>>>");
		GenCode.gen().nl();
		GenCode.gen().write(".CODE");
		GenCode.gen().write("PUSH lheap_init");
		GenCode.gen().write("CALL");
		GenCode.gen().write("PUSH "+ main.getLabel()+" # Metodo Main");
		GenCode.gen().write("CALL");
		GenCode.gen().write("HALT");
		GenCode.gen().nl();
		GenCode.gen().nl();

		GenCode.gen().write("lmalloc: LOADFP # Inicialización unidad");
		GenCode.gen().write("LOADSP");
		GenCode.gen().write("STOREFP # Finaliza inicialización del RA");
		GenCode.gen().write("LOADHL # hl");
		GenCode.gen().write("DUP # hl");
		GenCode.gen().write("PUSH 1 # 1");
		GenCode.gen().write("ADD # hl + 1");
		GenCode.gen().write("STORE 4 # Guarda resultado (puntero a base del bloque)");
		GenCode.gen().write("LOAD 3 # Carga cantidad de celdas a alojar (par´ametro)");
		GenCode.gen().write("ADD");
		GenCode.gen().write("STOREHL # Mueve el heap limit (hl)");
		GenCode.gen().write("STOREFP");
		GenCode.gen().write("RET 1 # Retorna eliminando el parámetro");
		GenCode.gen().nl();
		GenCode.gen().nl();

		GenCode.gen().write("lheap_init: RET 0 # Inicialización simplificada del .heap");
		GenCode.gen().nl();
		GenCode.gen().nl();

		GenCode.gen().write("# ---------GCI DEL CODIGO FUENTE---------------");
		GenCode.gen().write(".DATA");
		GenCode.gen().write("VT_Object: NOP");
		GenCode.gen().write("Object_Object: NOP");
		
		GenCode.gen().write("mensaje_error_casting: DW \"[Excepcion] Se produjo error al intentar castear\",0 #Mensaje para casteos erroneos");
		GenCode.gen().write(".CODE");
		GenCode.gen().write("error_casting: PUSH mensaje_error_casting #Apilo msj de error");
		GenCode.gen().write("SPRINT");
		GenCode.gen().write("HALT");



		for (Clase c : clases.values()) {
			if(!c.getNombre().equals("Object")) {
				claActual = c;
				c.chequearSentencias();
			}
		}
		
		GenCode.gen().close();

	}

	public Map<String, Clase> getClases() {
		return clases;
	}	
	public static void setClases(Map<String, Clase> clases) {
		TablaSimbolos.clases = clases;
	}
	public Metodo getMain() {
		return main;
	}
	public void setMain(Metodo main) {
		this.main = main;
	}

	//Imprime las clases con sus metodos.
	public void imprimirClases() {
		for (Clase c : clases.values()) {
			//			System.out.println("Clase "+c.getNombre());
			//			System.out.println("Metodos de la clase: ");
			for (Metodo m : c.getMetodos().values()) {
				System.out.println(m.getNombre()+": Tipo: "+m.getTipo().nombre+", Forma: "+m.getForma());
			}
			//System.out.println("Variables de la clase: ");

			for (String s : c.getVariables().keySet()) {
				System.out.println(s);
			}
			/*
			for (Variable v : c.getVariables().values()) {
				System.out.println(v.getNombre()+": Tipo: "+v.getTipo().nombre+", Visibilidad: "+v.getVisibilidad());
			}*/
		}
	}	
	public void imprimirCirs() {
		for (Clase c : clases.values()) {
			System.out.println("CIR de la clase "+c.getNombre());
			for(Variable v : c.getVariables().values()) {
				System.out.println(v.getNombre()+" OFF: "+v.getOffset());
			}
		}
	}
	
	public void imprimirVTs() {
		for (Clase c : clases.values()) {
			System.out.println("VT de la clase "+c.getNombre());
			for(Metodo m : c.getMetodos().values()) {
				//System.out.println("Metodo "+m.getNombre()+" Clase "+m.getClase().getNombre());
				if(!m.getNombre().equals(m.getClase().getNombre()) && !m.getForma().equals("static"))
					System.out.println(m.getNombre()+" OFF: "+m.getOffset());
			}
			System.out.println("=====================================================");
		}
		System.out.println("=================================================");
	}
	
	
	public void imprimirOffsetsMetodos() {
		for (Clase c : clases.values()) {
			System.out.println("Clase "+c.getNombre());
			for (Metodo m : c.getMetodos().values()) {
				System.out.println("Metodo "+m.getNombre());
				for (Parametro p : m.getParams().values()) {
					System.out.println("Parametro "+p.getNombre()+" OFF: "+p.getOffset());
				}
			}
		}
	}
	
}