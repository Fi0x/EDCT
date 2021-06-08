package com.fi0x.edct.util;

public class NumberConverter
{
    public static String convertToString(long number, String space)
    {
        StringBuilder visual = new StringBuilder(String.valueOf(number));
        for(int i = visual.length() - 3; i > 0; i-= 3)
        {
            visual.insert(i, space);
        }
        return visual.toString();
    }
}
