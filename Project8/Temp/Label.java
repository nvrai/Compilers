/**
 * COSC 4400 - Project #8
 * Represents symbolic code and data addresses used throughout the compiler.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
/* Copyright (C) 1997-2003, Purdue Research Foundation of Purdue University.
 * All rights reserved.  */
package Temp;
import Symbol.Symbol;

/**
 * A Label represents an address in assembly language.
 */

public class Label  
{
   private String name;		/* We give a name to the label */
   private static int count=0;

	/**
	 * a printable representation of the label, for use in assembly 
	 * language output.
	 */
	public String toString() {return name;}

	/** Renames an automatically generated label while preserving its identity. */
	public void rename(String n) { name = n; }

	/** Sets the number used by the next automatically generated label. */
	public static void setCount(int next) { count = next; }
	
	/**
	 * Makes a new label that prints as "name".
	 * Warning: avoid repeated calls to <tt>new Label(s)</tt> with
	 * the same name <tt>s</tt>.
	 */
	public Label(String n) 
	{
		name = n;
	}
	
	/**
	 * Makes a new label with an arbitrary name.
	 */
	public Label() 
	{
		this("L" + count++);
	}
	
	/**
	 * Makes a new label whose name is the same as a symbol.
	 */
	public Label(Symbol s) 
	{
		this(s.toString());
	}
}
