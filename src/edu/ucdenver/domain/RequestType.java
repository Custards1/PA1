package edu.ucdenver.domain;

public enum RequestType {
    CREATE_USER,
    AUTHENTICATE_USER,
    CREATE_CATAGORY,
    REMOVE_CATAGORY,
    SET_DEFAULT_CATAGORY,
    GET_DEFAULT_CATAGORY,
    ADD_PRODUCT_TO_CATALOG,
    GET_PRODUCTS_FROM_CATAGORY,
    REMOVE_PRODUCT_FROM_CATALOG,
    ADD_CATAGORY_TO_PRODUCT,
    GET_ALL_CATAGORIES,
    GET_ALL_PRODUCTS,
    SEARCH,
    REMOVE_CATAGORY_FROM_PRODUCT,
    TERMINATE,
    ERROR,
    NOOP,
    OK,
    PICTURE
}
