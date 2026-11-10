/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */

package Translate;
import java.io.PrintWriter;
import java.util.Iterator;

//import Semant.*;
import Absyn.Program;
import Parse.*;

import java.util.HashMap; 
import java.util.List;
import java.util.LinkedList;

//import Translate.Translate;

public class Main 
{
    public static void main(String args[])
    {
	InputStreamReader isr =	new InputStreamReader(System.in);
        java.io.Reader reader = new BufferedReader(isr);

	Absyn.Program parse = null;
	try
	{
	    parse = new Parse.ReadTypes(reader).Program();
	}
	catch (ParseException p)
	{
	    System.err.println(p.toString());
	    System.exit(-1);
	}
		
	Translate translate = new Translate(new Arm.ArmFrame());
	parse.accept(translate);
		
	PrintWriter writer = new PrintWriter(System.out);
		
	for (Iterator frags = translate.results().iterator(); 
	     frags.hasNext(); ) 
	{
	    Frag f = (Frag)frags.next();
	    if (f instanceof DataFrag) 
	    {
		writer.println("DataFrag(");
		writer.println(f);
		writer.println(")");
	    } 
	    else 
	    {
		writer.println("ProcFrag(");
		ProcFrag p = (ProcFrag)f;
		p.frame.printFrame(writer);
		if (p.body != null) 
		{
		    new Tree.Print(writer, p.body);
		    writer.println();
		}
		writer.println(")");
	    }
	}  
	writer.flush();
    }
}	

