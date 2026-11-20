/**
 * COSC 4400 - Project #8
 * Maps abstract temporaries to printable register or temporary names.
 * @authors Nick Raimondi and Payton Canegan
 * Instructor Dr Brylow
 * TA-BOT:MAILTO nicolas.raimondi@marquette.edu payton.canegan@marquette.edu
 */
package Temp;

/** Naming interface used when formatting assembly instructions. */
public interface TempMap {
    String tempMap(Temp temp);
}
