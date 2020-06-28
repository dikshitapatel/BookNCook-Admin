package com.abcd.bncserver.Common;

import com.google.firebase.database.DatabaseReference;

/**
 * Created by Karan Patel on 21-01-2018.
 */

public class Common {
    public static DatabaseReference currentUser;

    public static final String UPDATE="Update";
    public static final String DELETE="Delete";
    public static final int PICK_IMAGE_REQUEST=71;
    public static final String USER_KEY="User";
    public static final String PWD_KEY="Password";


    public static String codeConversion(String code)
    {
        if ("0".equals(code))
            return "Placed";
        else if("1".equals(code))
            return "On The Way";
        else
            return "Shipped";
    }
}
