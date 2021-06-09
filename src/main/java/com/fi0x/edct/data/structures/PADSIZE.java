package com.fi0x.edct.data.structures;

public enum PADSIZE
{
    NONE,
    S,
    M,
    L;

    public static PADSIZE getFromString(String name)
    {
        switch(name)
        {
            case "NONE":
                return NONE;
            case "S":
                return S;
            case "M":
                return M;
            default:
                return L;
        }
    }
}
