package asyn.Semantic;

import asyn.Token;

public class Variable {

	protected String nombre;
	protected String visibilidad;
	protected Tipo tipo;
	protected int offset;
	protected int linea;

	public Variable(Token tok,  String visibilidad, Tipo tipo) {
		nombre = tok.getLexema();
		linea = tok.getnLinea();
		this.visibilidad = visibilidad;
		this.tipo = tipo;
	}

	public void chequearDeclaraciones() throws Exception{
		if(!tipo.check()) {
			throw new Exception("Error, tipo mal declarado para la variable "+nombre+" en la linea "+linea);
		}
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

	public String getVisibilidad() {
		return visibilidad;
	}

	public void setVisibilidad(String visibilidad) {
		this.visibilidad = visibilidad;
	}

	public Tipo getTipo() {
		return tipo;
	}

	public void setTipo(Tipo tipo) {
		this.tipo = tipo;
	}

	public int getOffset() {
		return offset;
	}

	public void setOffset(int offset) {
		this.offset = offset;
	}

}