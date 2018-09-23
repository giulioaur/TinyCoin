/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import java.util.Arrays;

/**
 *
 * @author Giulio Auriemma
 */
public class StringParser {
    /**
     * Transform an array of int represented as string into an array of int.
     * 
     * @param str the string representation of the array.
     * @return    the array of int represeted by the string.
     */
    public static int []stringToIntArray(String str){
        return Arrays.stream(str.substring(1, str.length()-1).split(","))
                    .map(String::trim).mapToInt(Integer::parseInt).toArray();
    }
}
