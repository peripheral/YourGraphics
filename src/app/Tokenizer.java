package app;

import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;

import app.Parser.TOKEN;



public class Tokenizer {

	Scanner sc;
	private String token="";
	private CharIterator it;
	public Tokenizer(String src){
		it = new CharIterator(src);
	}

	public String consume(TOKEN t){
		if(t==lookUp()){
			String temp = token;
			token="";
			return temp;
		}
		throw new RuntimeException("Token missmatch Expected: "+t.name()+" Given: "+lookUp().name());
	}

	public TOKEN lookUp(){
		if(token.isEmpty()&&it.hasNext())
			readNextToken();
		sc=new Scanner(token);
		if(sc.hasNext("[a-zA-Z][[0-9][_][a-zA-Z]]*"))
			return TOKEN.STRING;
		if(sc.hasNext("[0-9]+?([.][0-9])??[0-9]*?"))
			return TOKEN.NUMBER;
		else if(sc.hasNext("[(]"))
			return TOKEN.LPARANTESIS;
		else if(sc.hasNext("[)]"))
			return TOKEN.RPARANTESIS;
		else if(sc.hasNext("[{]"))
			return TOKEN.LEFTBRACKET;

		else if(sc.hasNext("[}]")) 
			return TOKEN.RIGHTBRACKET;
		else if(token.isEmpty())
			return TOKEN.EOF;
		return TOKEN.INVALID;
	}

	private void readNextToken() {
		Pattern p=Pattern.compile("[\\s]|[;,]");
		while(p.matcher(it.peek()+"").matches()){
			it.next();
		}

		p = Pattern.compile("[a-zA-Z]");
		if(p.matcher(it.peek()+"").matches()){
			p=Pattern.compile("[_|[a-zA-Z_0-9]]*");
			do{
				token=token+it.next();
			}while(p.matcher(it.peek()+"").matches());
		}
		if(!token.isEmpty()){
			return;
		}
		p=Pattern.compile("[0-9]");
		while(p.matcher(it.peek()+"").matches()){
			token=token+it.next();
			if(it.peek().equals('.')){
				token=token+it.next();
			}
		}
		
		if(!token.isEmpty()){
			return;
		}
		if(it.peek().equals('(')){
			token=it.next()+"";
		}
		if(!token.isEmpty()){
			return;
		}
		if(token.isEmpty()&&it.peek().equals(')') ){
			token=it.next()+"";
		}
		if(!token.isEmpty()){
			return;
		}
		if(it.peek().equals('{') ){
			token=it.next()+"";
		}
		if(!token.isEmpty()){
			return;
		}
		if(it.peek().equals('}') ){
			token=it.next()+"";
		}
	}

	public class CharIterator {
		Scanner sc;
		String str="";
		int pos = -1;
		public CharIterator(String src){
			if(src!=null){
				if(!src.isEmpty()){
					try{
						File f = new File(src);
						if(f.isFile()&&f.getName().contains(".txt"))
							sc= new Scanner(f);
						else
							sc= new Scanner(src);
					}
					catch(Exception e){
						sc= new Scanner(src);
					}
				}
				else
					throw new RuntimeException("Argument is empty");
			}
			else
				throw new RuntimeException("No argument is given");
			loadString();
		}

		void loadString(){
			if(sc.hasNext()){
				str= sc.nextLine();
				if(!str.isEmpty()){
					pos=0;
				}
				else
					pos=-1;
			}
		}

		public boolean hasNext() {
			if(pos==str.length()){
				pos=-1;
				loadString();
			}
			return pos>-1;
		}

		public Character next() {
			if(pos==str.length()){
				pos=-1;
				loadString();
			}
			if(pos==-1)
				throw new RuntimeException("Iterator is empty");
			pos++;
			return str.charAt(pos-1);
		}


		public Character peek() {
			if(pos==str.length()){
				pos=-1;
				loadString();
			}
			if(pos==-1){
				throw new RuntimeException("Iterator is empty!");
			}
			return str.charAt(pos);		
		}
	}
}
