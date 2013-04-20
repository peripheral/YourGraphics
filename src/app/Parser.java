package app;

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class Parser {
	public enum TOKEN{NAME,ARG,OBJTYPE,OBJECT,LEFTBRACKET,RIGHTBRACKET,
		LPARANTESIS,RPARANTESIS,STRING,INVALID,NUMBER,EOF} 
	public Stack<TOKEN> tokens = new Stack<TOKEN>();
	public Stack<String> data = new Stack<String>();
	private Tokenizer tokenizer;
	public Parser(Tokenizer t){
		tokenizer = t;
		start();
	}
	
	void start(){
		TOKEN t = tokenizer.lookUp();
		while(t!=TOKEN.EOF&&t!=TOKEN.INVALID){
			object();
			t=tokenizer.lookUp();
			System.out.println("Next Object"+t);
		}
		tokens.push(TOKEN.EOF);
	}
	void object(){
		tokens.push(TOKEN.OBJECT);
		TOKEN t = tokenizer.lookUp();
		if(t==TOKEN.STRING){
			tokens.push(TOKEN.NAME);
			data.push(tokenizer.consume(t));
			System.out.println("New Object:"+data.peek());
		}
		t=tokenizer.lookUp();
		if(t==TOKEN.LEFTBRACKET){
			tokenizer.consume(t);
			instructions();
			t=tokenizer.lookUp();
			if(t==TOKEN.RIGHTBRACKET){
				tokenizer.consume(t);
			}
			else
				throw new RuntimeException("Expected end Square bracket. after:"+data.peek());
		}
	}
	
	void instructions(){
		TOKEN t = tokenizer.lookUp();
		while(t==TOKEN.STRING){
			tokens.push(TOKEN.NAME);
			data.push(tokenizer.consume(t));
			System.out.println("Instruction consumed: "+data.peek());
			t=tokenizer.lookUp();
			if(t==TOKEN.LPARANTESIS){
				tokenizer.consume(t);
				args();
				t=tokenizer.lookUp();
				if(t==TOKEN.RPARANTESIS){
					tokenizer.consume(t);
				}
				else
					throw new RuntimeException("Expected end parantesis. after:"+data.peek());
			}
			t=tokenizer.lookUp();
		}
	}
	
	void args(){
		TOKEN t= tokenizer.lookUp();
		while(t==TOKEN.NUMBER){
			tokens.push(TOKEN.ARG);
			data.push(tokenizer.consume(t));
			t=tokenizer.lookUp();
			System.out.println("Argument consumed :"+data.peek());
		}
	}
	
	public Map getStacks(){
		Map map = new HashMap();
		map.put("tokens",tokens );
		map.put("data",data);
		return map;	
	}
	
	
	
}
