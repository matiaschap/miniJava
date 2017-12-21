package asyn.Semantic;

import asyn.Token;

public class Parametro extends Variable{

	private int posicion;


	public Parametro(Token tok, Tipo tip, int posicion) {
		super(tok,"public",tip);
		this.posicion = posicion;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public int getPosicion() {
		return posicion;
	}

	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public void chequearDeclaraciones() throws Exception{
		if(!tipo.check()) {
			throw new Exception("Error, tipo de parametro '"+tipo.getNombre()+"' invalido en linea "+linea);
		}
	}


}