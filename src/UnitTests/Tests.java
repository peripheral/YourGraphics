package UnitTests;

import static org.junit.Assert.*;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.junit.Before;
import org.junit.Test;

import app.Parser;
import app.Parser.TOKEN;
import app.Tokenizer;
import app.Tokenizer.CharIterator;

public class Tests {
	private Tokenizer tokenizer ;
	private String text ;
	private Parser parser;
	private Stack<TOKEN> expectedTokens;
	@Before
	public void init(){
		text="obj1 {instruction_1(); instruction_2(23.4,15);instruction()}obj2{}";
		tokenizer= new Tokenizer(text);
		initExpected();
		
	}
	
	@Test
	public void TokenizerTest(){
		TOKEN t =tokenizer.lookUp();
		int i = 0;
		while(t!=TOKEN.EOF){
			System.out.print(t);
			System.out.println(tokenizer.consume(t));
			t=tokenizer.lookUp();
		}
		System.out.println(t);
	}
	
	@Test
	public void ParserTest(){
		parser= new Parser(new Tokenizer(text));
		Map map =parser.getStacks();
		Stack s = (Stack) map.get("tokens");
		Stack data=(Stack)map.get("data");
		while(!s.isEmpty()){
			//System.out.println(s.pop());
			assertEquals(expectedTokens.pop(),s.pop());
		}

	}
	
	public void initExpected(){
		expectedTokens = new Stack<TOKEN>();
		expectedTokens.push(TOKEN.OBJECT);
		expectedTokens.push(TOKEN.NAME);
		expectedTokens.push(TOKEN.NAME);
		expectedTokens.push(TOKEN.NAME);
		expectedTokens.push(TOKEN.ARG);
		expectedTokens.push(TOKEN.ARG);
		expectedTokens.push(TOKEN.NAME);
		expectedTokens.push(TOKEN.OBJECT);
		expectedTokens.push(TOKEN.NAME);
		expectedTokens.push(TOKEN.EOF);
	}
	

}
