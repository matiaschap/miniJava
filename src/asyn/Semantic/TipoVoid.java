package asyn.Semantic;

public class TipoVoid extends TipoMetodo{

	public TipoVoid() {
		nombre = "void";
	}

	public boolean esCompatible(TipoMetodo t) {

		return (t.getNombre().equals(this.nombre));
	}
	public boolean esVoid() {
		return true;
	}
	public boolean check() {
		return true;
	}
	public void gen(String s) {}

}