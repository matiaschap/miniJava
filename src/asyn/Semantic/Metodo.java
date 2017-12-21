package asyn.Semantic;

import java.util.HashMap;
import java.util.Map;

import asyn.AnalizadorSyn;
import asyn.Token;
import gci.GenCode;

public class Metodo {

	private String nombre;
	private String forma;
	private TipoMetodo tipoRetorno;
	private Map<String, Parametro> params;
	private Map<String, Variable> vars;
	private int offset;
	private int linea;
	private Bloque cuerpo;
	private boolean tieneReturn;
	private Clase clase;
	private Clase claseImplementadora;
	private String label;
	private int offParam;
	private int offVar;


	public Metodo(Token t, String forma, TipoMetodo tipoRetorno, Clase claseImplementadora) {
		nombre = t.getLexema();
		linea = t.getnLinea();
		this.forma = forma;
		this.tipoRetorno = tipoRetorno;
		this.claseImplementadora = claseImplementadora;
		params = new HashMap<String, Parametro>();
		vars = new HashMap<String, Variable>();
	}

	public Clase getClaseImplementadora() {
		return claseImplementadora;
	}

	public void setClaseImplementadora(Clase claseImplementadora) {
		this.claseImplementadora = claseImplementadora;
	}

	public void chequearDeclaraciones() throws Exception{
		if(!tipoRetorno.check()) {
			throw new Exception("El tipo de retorno '"+tipoRetorno.getNombre()+"' del metodo '"+nombre+"' en la linea "+linea+" no existe");//Completar
		}

		for (Parametro p : params.values()) {
			p.chequearDeclaraciones();
			if(forma.equals("static") && !nombre.equals(clase.getNombre())) {
				p.setOffset(2 + cantParams() - p.getPosicion()); //3+cantParams-nroParam
			}
			else {
				p.setOffset(3 + cantParams() - p.getPosicion());
			}

			//Agrego a los parametros como variables locales
			vars.put(p.getNombre(), p);
		}
	}

	public void chequearSentencias() throws Exception{
		//System.out.println("chequeando metodo: "+nombre+" de la clase "+clase.getNombre());
		if(AnalizadorSyn.getTs().getClaseActual().getNombre().equals(clase.getNombre()))

			/**********************GenCode****************************/

			if(nombre.equals(clase.getNombre())) { //Es constructor
				GenCode.gen().write(getLabel()+": NOP # CONSTRUCTOR "+nombre);
			}
			else {
				GenCode.gen().write(getLabel()+": NOP # METODO "+nombre);
			}


		GenCode.gen().inicioUnidad();
		/************************Fin GenCode***********************************/

		/************************Chequeo Sentencias****************************/
		//System.out.println("Metodo "+nombre+" offvar "+offVar);
		setOffVar(0);//???????????????
		if(cuerpo != null) {
			cuerpo.check();
		}
		/**********************GenCode****************************************/
		GenCode.gen().write("STOREFP # Restablezco el contexto");

		if(forma.equals("static") && !nombre.equals(clase.getNombre())) { //Si es estatico y no es el constructor
			GenCode.gen().write("RET "+params.size()+" # Retorno y libero espacio de los parametros del metodo "+nombre);
		}
		else { //Si es dinamico o es constructor
			GenCode.gen().write("RET "+(params.size()+1)+" # Retorno y libero espacio de los parametros del metodo y del THIS "+nombre);
		}
		GenCode.gen().nl();
	}


	public void addParametro(Token t, Tipo tip) throws Exception{
		if(!params.containsKey(t.getLexema())) {
			params.put(t.getLexema(), new Parametro(t,tip,params.size()));
		}
		else {
			Metodo m = AnalizadorSyn.getTs().getMetodoActual();
			throw new Exception("Hay 2 parametros con el mismo nombre en el metodo '"+m.getNombre()+"' en la linea "+m.getLinea());
		}
	}

	public void addVar(Variable v) throws Exception{
		if(!vars.containsKey(v.getNombre()))
			vars.put(v.getNombre(), v);
		else {
			throw new Exception("Varible "+v.getNombre()+" repetida en linea "+v.getLinea()+" ya estaba declarada en la linea "+vars.get(v.getNombre()).getLinea());
		}
	}


	public boolean estaVar(String n) {
		return vars.containsKey(n);
	}

	public int cantParams() {
		return params.size();
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}


	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getForma() {
		return forma;
	}

	public void setForma(String forma) {
		this.forma = forma;
	}

	public TipoMetodo getTipo() {
		return tipoRetorno;
	}

	public void setTipo(Tipo tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	public Map<String, Parametro> getParams() {
		return params;
	}

	public void setParams(Map<String, Parametro> params) {
		this.params = params;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

	public Map<String, Variable> getVars() {
		return vars;
	}

	public void setVars(Map<String, Variable> vars) {
		this.vars = vars;
	}

	public Bloque getCuerpo() {
		return cuerpo;
	}

	public void setCuerpo(Bloque cuerpo) {
		this.cuerpo = cuerpo;
	}

	public void setTipo(TipoMetodo tipoRetorno) {
		this.tipoRetorno = tipoRetorno;
	}

	public Parametro getParamPos(int i) {
		for (Parametro p : params.values()) {
			if(p.getPosicion()==i) {
				return p;
			}
		}
		return null;
	}

	public void eliminarVar(String n) {
		vars.remove(n);
	}

	public void setTieneReturn(boolean tieneReturn) {
		this.tieneReturn = tieneReturn;
	}

	public boolean getTieneReturn() {
		return tieneReturn;
	}

	public Clase getClase() {
		return clase;
	}

	public void setClase(Clase clase) {
		this.clase = clase;
	}

	public String getLabel() {
		if(label == null) {
			label = ""+nombre+"_"+clase.getNombre();
		}
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getOffParam() {
		return offParam;
	}

	public void setOffParam(int offParam) {
		this.offParam = offParam;
	}

	public int getOffVar() {
		return offVar;
	}

	public void setOffVar(int offVar) {
		this.offVar = offVar;
	}

}