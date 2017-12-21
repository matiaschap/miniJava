package asyn.Semantic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import asyn.AnalizadorSyn;
import gci.GenCode;

public class Bloque extends Sentencia{

	protected ArrayList<Sentencia> sentencias;
	protected Map<String, Variable> varLocales;

	public Bloque() {
		sentencias = new ArrayList<Sentencia>();
		varLocales = new HashMap<String, Variable>();
	}

	public void addSentencia(Sentencia s) {
		sentencias.add(s);
	}

	public void addVariable(Variable v) throws Exception{
		if(!varLocales.containsKey(v.getNombre()))
			varLocales.put(v.getNombre(), v);
		else throw new Exception("Error semantico: Variable repetida: '"+v.getNombre()+ "' en linea "+v.getLinea()+" (declarada anteriormente en linea "+varLocales.get(v.getNombre()).getLinea()+")");
	}

	public ArrayList<Sentencia> getSentencias() {
		return sentencias;
	}

	public void setSentencias(ArrayList<Sentencia> sentencias) {
		this.sentencias = sentencias;
	}

	public Map<String, Variable> getVarLocales() {
		return varLocales;
	}

	public void setVarLocales(Map<String, Variable> varLocales) {
		this.varLocales = varLocales;
	}

	public void check() throws Exception {
		AnalizadorSyn.getTs().setBloqueActual(this);

		for (Sentencia s : sentencias) {
			s.check();
		}
		//FMEM DE LA CANT DE VARS LOCALES
		GenCode.gen().write("FMEM "+varLocales.size()+" # Libero espacio de variables locales al bloque");
		//CAMBIAR OFF VARS LOCALES
		AnalizadorSyn.getTs().getMain().setOffVar(AnalizadorSyn.getTs().getMain().getOffVar()-varLocales.size());

		for (Variable v : varLocales.values()) {
			AnalizadorSyn.getTs().getMetodoActual().eliminarVar(v.getNombre());
		}
	}

}