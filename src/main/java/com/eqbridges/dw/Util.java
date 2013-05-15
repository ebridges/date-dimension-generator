package com.eqbridges.dw;

/**
 * User: ebridges
 * Date: 11/4/12
 * Time: 3:26 PM
 */
public class Util {
    public static int formatDateId(int year, int month, int day) {
        String id = String.format("%04d%02d%02d", year, month, day);
        return Integer.valueOf( id );
    }
}
