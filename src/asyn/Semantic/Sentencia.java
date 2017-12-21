package asyn.Semantic;

public abstract class Sentencia {
	protected int linea;

	public int getLinea() {
		return linea;
	}

	public void setLinea(int linea) {
		this.linea = linea;
	}

	public abstract void check() throws Exception;

}