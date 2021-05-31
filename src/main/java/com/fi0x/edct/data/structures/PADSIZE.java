package com.fi0x.edct.data.structures;

public enum PADSIZE
{
    S,
    M,
    L;

    public static PADSIZE getFromString(String name)
    {
        switch(name)
        {
            case "S":
                return S;
            case "M":
                return M;
            default:
                return L;
        }
    }
}