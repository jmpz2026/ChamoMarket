package com.chamo.chamomarket.repository;


public class MessageRepository {
    // Productos
    public static String PRODUCT_NOT_FOUND = "NO SE ENCONTRO EL PRODUCTO";
    public static String PRODUCT_FOUND = "EL PRODUCTO SE ENCONTRO";
    public static String PRODUCT_CREATED = "EL PRODUCTO HA SIDO CREADO";

    // Categorias
    public static String CATEGORY_NOT_FOUND = "NO SE ENCONTRO LA CATEGORIA";
    public static String CATEGORY_FOUND = "LA CATEGORIA SE ENCONTRO";
    public static String CATEGORY_CREATED = "LA CATEGORIA HA SIDO CREADA";
    public static String CATEGORY_CONFLICT_NAME = "YA EXISTE UNA CATEGORIA CON ESTE NOMBRE";
    public static String CATEGORY_CONFLICT_STATUS = "ESTADO INVALIDO";
    public static String CATEGORY_UPDATED = "LA CATEGORIA HA SIDO MODIFICADA";
    public static String CATEGORY_DISABLED = "LA CATEGORIA HA SIDO DESACTIVADA";
}
