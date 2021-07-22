package com.fi0x.edct.logic.helper;

public class NumberConverter
{
    public static String getAgeText(long age)
    {
        String text = "Local data age: ";
        if(age < (60 * 1000)) text += age/1000 + "s";
        else if(age < (60 * 60 * 1000)) text += age/(60 * 1000) + "min";
        else if(age < (24 * 60 * 60 * 1000)) text += age/(60 * 60 * 1000) + "h";
        else text += age/(24 * 60 * 60 * 1000) + "d";

        return text;
    }
}
