/**
 * COSC 4400 - Project #3
 * Reads MiniJava from standard input, parses it, and prints its AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Parse;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;

public class Main
{   
    public static void main(String [] args) 
    {
	InputStreamReader isr =	new InputStreamReader(System.in);
        Reader reader = new BufferedReader(isr);

	try
	{
	Absyn.Program parse = new MiniJavaParser(reader).Goal();
	PrintWriter writer = new PrintWriter(System.out);
	Absyn.PrintVisitor pv =	new Absyn.PrintVisitor(writer);
	pv.visit(parse);
	writer.flush();
	}
    catch (ParseException p)
	{
	    System.out.println(p.toString());
	    System.exit(-1);
	}
    }
}
