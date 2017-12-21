package asyn;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import exceptions.LexicalError;
/**
 * @author Matias Marzullo
 */
public class AnalizadorLexico {

	private enum ErrorCode {
		CharacterLiteral, Character, Comment, NumberLiteral, StringLiteral
	};

	private List<String> PR; // palabras reservadas
	private BufferedReader buffer;
	private DataInputStream dis;
	private FileInputStream fstream;
	private int linea, col;
	private char last;
	private boolean eof, eol;
	/**
	 * Constructor para el analizador lexico, que recibe la direccion del codigo fuente.
	 * 
	 * @param path
	 *            direccion del archivo codigo fuente.
	 * @throws IOException
	 *             si el archivo no existe, o no es posible accederlo.
	 */
	public AnalizadorLexico(String path) throws IOException {

		inicializarPR();
		// read file.
		fstream = new FileInputStream(path);
		dis = new DataInputStream(fstream);
		buffer = new BufferedReader(new InputStreamReader(dis));
		eof = false;
		eol = false;
		linea = 1;
		col = 1;
		last = nextChar();
	}
	/**
	 * Lee el siguiente caracter
	 */
	private char nextChar() {
		if (eol) {
			eol = false;
			col = 1;
			linea++;
		} else
			col++;
		char ret;
		try {
			ret = (char) buffer.read();
		} catch (IOException e) {
			e.printStackTrace();
			eof = true;
			return (char) -1;
		}
		if (ret == (char) -1)
			eof = true;
		else if (ret == '\n')
			eol = true;
		return ret;
	}
	/**
	 * Inicializa la lista de palabras reservadas.
	 */
	private void inicializarPR() {
		PR = new ArrayList<String>();
		PR.add("class"); PR.add("extends"); PR.add("static"); 
		PR.add("dynamic"); PR.add("public"); PR.add("private");
		PR.add("void"); PR.add("boolean"); PR.add("char"); 
		PR.add("int"); PR.add("String"); PR.add("if");
		PR.add("else"); PR.add("while"); PR.add("return");
		PR.add("instanceof"); PR.add("this"); PR.add("new");
		PR.add("null"); PR.add("true"); PR.add("false");
	}
	/**
	 * Retorna un token.
	 * 
	 * @return Token.
	 * @throws LexicalError
	 *             si se encontro un error al reconocer el token.
	 */
	public Token getToken() throws LexicalError {
		// last = nextChar();
		int state = 0;
		boolean isUpper=false;
		String readed = "";
		//char aux;

		while (true) {
			if (eof) {
				switch (state) {
				case 6:
				case 7:
				case 8:
					error(ErrorCode.Comment,linea,col,"");
				case 10:
					error(ErrorCode.StringLiteral,linea,col,readed);
				case 37:
				case 38:
				case 40:
				case 41:
				case 42:
				case 44:
					error(ErrorCode.CharacterLiteral,linea,col,readed);
				}
				return new Token("EOF","EOF",linea);
			}
			switch (state) {
			case 0:
				// consume delimitadores. (espacios, tabuladores, enters)
				if (last == '\t' || last == ' ' || last == '\n'
				|| last == (char) 13 || last == (char) 10) {
					last = nextChar();
					break;
				}
				switch (last) {
				case '/':
					state = 2;
					readed += last;
					break;
				case '"':
					state = 10;
					break;
				case '(':
					state = 12;
					readed += last;
					break;
				case ')':
					state = 13;
					readed += last;
					break;
				case '{':
					state = 14;
					readed += last;
					break;
				case '}':
					state = 15;
					readed += last;
					break;
				case ';':
					state = 16;
					readed += last;
					break;
				case ',':
					state = 17;
					readed += last;
					break;
				case '.':
					state = 18;
					readed += last;
					break;
				case '[':
					state = 19;
					readed += last;
					break;
				case ']':
					state = 20;
					readed += last;
					break;
				case '>':
					state = 21;
					readed += last;
					break;
				case '<':
					state = 23;
					readed += last;
					break;
				case '!':
					state = 25;
					readed += last;
					break;
				case '=':
					state = 27;
					readed += last;
					break;
				case '+':
					state = 29;
					readed += last;
					break;
				case '*':
					state = 30;
					readed += last;
					break;
				case '-':
					state = 31;
					readed += last;
					break;
				case '&':
					state = 32;
					readed += last;
					break;
				case '|':
					state = 34;
					readed += last;
					break;
				case '%':
					state = 36;
					readed += last;
					break;
				case '\'':
					state = 37;
					break;
				default:
					if (Character.isLowerCase(last)) { //es minuscula
						state = 1;
						readed += last;
						break;
					}
					if (Character.isUpperCase(last)) {//es mayuscula
						state = 1;
						readed += last;
						isUpper = true;
						break;
					}
					if (Character.isDigit(last)) { //es digito
						state = 9;
						readed += last;
						break;
					}
					error(ErrorCode.Character, linea,col,""+last);
				}
				break;
			case 1:
				last = nextChar();
				if (Character.isLetterOrDigit(last) || last == '_') {
					readed += last;
					break;
				}
				if (PR.contains(readed))		//es una PR
					return new Token(readed, readed, linea);
				else
					if (isUpper){
						return new Token("IdClase", readed, linea);
					}
					else 
						return new Token("idMetVar", readed, linea);
			case 2:
				last= nextChar();
				if (last=='/'){
					state=3;
					break;
				}
				if(last=='*'){ 
					state=6;
					break;
				}
				return new Token("/", readed, linea);
			case 3:
				last = nextChar();
				switch (last) {
				case '*':
					state = 6;
					break;
				case '\n':
					state = 5;
					break;
				default:
					state = 4;
					break;
				}
				break;
			case 4:
				last = nextChar();
				state = last == '\n' ? 5 : 4;
				break;
			case 5:
				last = nextChar();
				state = 0;
				readed = "";
				break;
			case 6:
				last = nextChar();
				state = last == '*' ? 7 : 6;
				break;
			case 7:
				last = nextChar();
				switch (last) {
				case '*':
					break;
					// Me quedo en el mismo estado.
				case '/':
					state = 5;  
					break;
				default:
					state = 6;
					break;
				}
				break;
			case 8:
				last = nextChar();
				state = last == '/' ? 5 : 6;
				break;
			case 9:
				last = nextChar();
				if (Character.isDigit(last)) {
					readed += last;
					break;
				} else if (Character.isLetter(last) || last == '('
						|| last == '{' || last == '\"' || last == '\''
						|| last == '.' || last == '[')
					error(ErrorCode.NumberLiteral, linea,col,readed+last);

				return new Token("intLiteral", readed, linea);
			case 10:
				last = nextChar();
				switch (last) {
				case '"':
					state = 11;
					break;
				case '\n':
					error(ErrorCode.StringLiteral,linea,col,readed);
				default:
					readed += last;
				}
				break;
			case 11:
				last = nextChar();
				return new Token("stringLiteral", readed, linea);
			case 12:
				last = nextChar();
				return new Token("(", readed, linea);
			case 13:
				last = nextChar();
				return new Token(")", readed, linea);
			case 14:
				last = nextChar();
				return new Token("{", readed, linea);
			case 15:
				last = nextChar();
				return new Token("}", readed, linea);
			case 16:
				last = nextChar();
				return new Token(";", readed, linea);
			case 17:
				last = nextChar();
				return new Token(",", readed, linea);
			case 18:
				last = nextChar();
				return new Token(".", readed, linea);
			case 19:
				last = nextChar();
				return new Token("[", readed, linea);
			case 20:
				last = nextChar();
				return new Token("]", readed, linea);
			case 21:
				last = nextChar();
				if (last == '=') {
					readed += last;
					state = 22;
					break;
				}
				return new Token(">", readed, linea);
			case 22:
				last = nextChar();
				return new Token(">=", readed, linea);
			case 23:
				last = nextChar();
				if (last == '=') {
					readed += last;
					state = 24;
					break;
				}
				return new Token("<", readed, linea);
			case 24:
				last = nextChar();
				return new Token("<=", readed, linea);
			case 25:
				last = nextChar();
				if (last == '=') {
					readed += last;
					state = 26;
					break;
				}
				return new Token("!", readed, linea); // negative
			case 26:
				last = nextChar();
				return new Token("!=", readed, linea); // not equal
			case 27:
				last = nextChar();
				if (last == '=') {
					readed += last;
					state = 28;
					break;
				}
				return new Token("=", readed, linea);
			case 28:
				last = nextChar();
				return new Token("==", readed, linea);
			case 29:
				last = nextChar();
				return new Token("+", readed, linea);
			case 30:
				last = nextChar();
				return new Token("*", readed, linea);
			case 31:
				last = nextChar();
				return new Token("-", readed, linea);
			case 32:
				last = nextChar();
				if (last == '&') {
					readed += last;
					state = 33;
				}
				break;
			case 33:
				last = nextChar();
				return new Token("&&", readed, linea);
			case 34:
				last = nextChar();
				if (last == '|') {
					readed += last;
					state = 35;
				}
				break;
			case 35:
				last = nextChar();
				return new Token("||", readed, linea);
			case 36:
				last = nextChar();
				return new Token("%", readed, linea);
			case 37:
				last = nextChar();
				if (last != '\\' && last != '\'' && last != '\n') {
					readed += last;
					state = 38;
				} else if (last == '\\' && last=='@' && last=='&' 
						&& last=='|' && last=='?' && last=='$') {
					state = 40;
				}
				break;
			case 38:
				//aux = last;
				last = nextChar();
				if (last == '\'') {
					state = 39;
					break;
				}
				error(ErrorCode.CharacterLiteral, linea,col,""+readed+last);
			case 39:
				last = nextChar();
				return new Token("charLiteral", readed, linea);
			case 40:
				last = nextChar();
				if (last != 'n' && last != 't') {
					readed += last;
					state = 38;
				} else if (last == 'n') {
					readed += '\n';
					state = 38;
				} else {
					readed += '\t';
					state = 38;
				}
				break;
			}
		}
	}
	public void error(ErrorCode code, int linea, int col, String readed)
			throws LexicalError {
		String str = "Error Lexico en la linea "+linea+", columna "+col+". ";
		switch (code) {
		case Character:
			str += "El caracter \'"+readed+"\' es invalido.";
			break;
		case CharacterLiteral:
			str += "El literal caracter es invalido.";
			break;
		case Comment:
			str += "El bloque de comentario no esta cerrado y se alcanzo el fin de archivo.";
			break;
		case NumberLiteral:
			str += "El literal numero \'"+readed+"\' es invalido.";
			break;
		case StringLiteral:
			str += "El literal String \'"+readed+"\' es invalido.";
			break;
		}
		throw new LexicalError(str);
	}
	/**
	 * Cierra el archivo del codigo fuente.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		dis.close();
	}
}
