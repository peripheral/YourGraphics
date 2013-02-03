package app;

public class Compiler {
	
	public Compiler(String path){
		Tokenizer t = new Tokenizer(path);
		Parser p= new Parser(t);
	}
}
