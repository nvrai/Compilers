/**
 * COSC 4400 - Project #3
 * Represents the boolean type in the AST.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Absyn;
public class BooleanType extends Type { public void accept(Visitor v) { v.visit(this); } }
