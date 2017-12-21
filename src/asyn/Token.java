package asyn;

/**
 * @author Matias Marzullo
 *
 */
public class Token {
	private String name;
	private String lexema;
	private int nLinea;	

	public Token(String name, String lexema, int nLinea) {
		this.name = name;
		this.lexema = lexema;
		this.nLinea = nLinea;
	}
	public String getName() {
		return name;
	}
	public String getLexema() {
		return lexema;
	}
	public int getnLinea() {
		return nLinea;
	}

    /**
     * Compara el token con una cadena pasada como parametro.
     *
     * Requerido por: Analizador sintactico
     *
     * @param s
     * @return true si el token es igual a la cadena pasada como parametro,
     * false en caso contrario.
     */
    public boolean equals(String s) {
        return name.equals(s);
    }
}