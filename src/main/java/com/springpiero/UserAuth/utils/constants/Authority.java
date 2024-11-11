package com.springpiero.UserAuth.utils.constants;

public enum Authority {

    READ,
    WRITE,
    UPDATE,
    USER,//Can update, delete self object, read anything
    ADMIN // read update delete any object
}
