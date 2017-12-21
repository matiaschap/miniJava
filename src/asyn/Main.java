package asyn;

import java.io.IOException;
import gci.GenCode;

import exceptions.*;

/**
 * @author Matias Marzullo
 *
 */
public class Main {
	public static void main(String args[]) throws LexicalError, SyntaxError,Exception{
		try {
//			if (args.length < 1) {
//				System.out.println("Se esperaba un archivo de origen.");
//				System.out.println("Use: input [output]");
//				return;
//			}
//			if (args.length > 2) {
//				System.out.println("Demasiados argumentos.");
//				System.out.println("Use: input [output]");
//				return;
//			}
			GenCode.path = "/home/mati/Escritorio/Test/aCorregir/out.ceiasm";

//			if(args.length == 2)
//				GenCode.path = args[1];
//			else {
//				GenCode.path = args[0] + ".out.ceiasm";
//				System.out.println("No se especifico el archivo de salida."); 
//				System.out.println("Se creó un archivo de salida con nombre: "+GenCode.path);
//			}

			GenCode.gen();
			AnalizadorLexico a = new AnalizadorLexico("/home/mati/Escritorio/Test/aCorregir/prueba.mjava");
			//AnalizadorLexico a = new AnalizadorLexico(args[0]);
			AnalizadorSyn s= new AnalizadorSyn(a);

			s.analize();
			a.close();
			GenCode.gen().close();

		} catch (IOException e) {
			System.out.println("No se puede abrir el archivo de origen.");
		} 
	}

}