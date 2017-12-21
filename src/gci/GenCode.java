package gci;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.PrintWriter;

public class GenCode {


	public static String path;
	private static GenCode gc;
	private int labelNbr = 0;
	private static int cantClases;


	private PrintWriter printWriter;


	public GenCode() {

		try {
			printWriter = new PrintWriter(new BufferedWriter( new FileWriter(path)));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}


	public static GenCode gen() {
		if(gc==null) {
			gc = new GenCode();
			cantClases=0;
		}

		return gc;

	}

	public void nl() {
		printWriter.println();
	}

	public void comment(String c) {
		printWriter.println("# " + c);
	}

	public void write(String c) {
		printWriter.println(c);
	}

	public void inicioUnidad() {
		printWriter.println("LOADFP # Guardo enlace dinamico");
		printWriter.println("LOADSP # Inicializo FP");
		printWriter.println("STOREFP");
		printWriter.println();
	}

	public void close() {
		printWriter.close();
	}

	public String genLabel() {
		return "l"+labelNbr++;
	}

	public static int contadorClase() {
		return cantClases++;
	}
}