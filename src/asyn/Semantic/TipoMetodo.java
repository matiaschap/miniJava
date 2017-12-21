package asyn.Semantic;

public abstract class TipoMetodo {
	protected String nombre;
	protected int linea;
	public abstract boolean esVoid();

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public abstract boolean esCompatible(TipoMetodo t) throws Exception;

	public abstract boolean check();

	public abstract void gen(String s);
}