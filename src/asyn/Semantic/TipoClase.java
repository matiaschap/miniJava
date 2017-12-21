package asyn.Semantic;

import asyn.*;

public class TipoClase extends Tipo{
	Token t;

	public TipoClase(Token t) {
		this.t = t;
		nombre = t.getLexema();
	}
	/*
	 * @see asyn.Semantic.TipoMetodo#esCompatible(asyn.Semantic.TipoMetodo)
	 * 
	 * chequeo que la asignación sea compatible, t = tipo
	 */
	public boolean esCompatible(TipoMetodo tipo) throws Exception{

		if(nombre.equals("null") || nombre.equals("Object")){
//		if(nombre.equals("null")){
			return true;
		}

		if(AnalizadorSyn.getTs().estaClase(nombre)) {
			if(AnalizadorSyn.getTs().estaClase(tipo.getNombre())) {
				AnalizadorSyn.getTs();
				Clase actual = TablaSimbolos.getClase(nombre);
				while(!actual.getNombre().equals("Object")) {
					if(actual.getNombre().equals(tipo.getNombre())) {
						return true;
					}
					AnalizadorSyn.getTs();
					actual = TablaSimbolos.getClase(actual.getHereda());
				}
				//Agregado correccion
				if(actual.getNombre().equals(tipo.getNombre())) {
					return true;
				}
				//Fin agregado
				throw new Exception("El tipo "+nombre+" no es compatible con el tipo "+tipo.getNombre()+" en la linea "+t.getnLinea());
			}
			else {
				throw new Exception("Error en la linea "+tipo.getLinea()+": El tipo "+tipo.getNombre()+" no es compatible con "+nombre);
			}
		}
		else {
			throw new Exception("El tipo "+nombre+" utilizado en la linea "+t.getnLinea()+" no existe.");
		}
	}

	public boolean check() {
		return (AnalizadorSyn.getTs().estaClase(nombre));
	}

	public void gen(String s) {}

}