package com.iulyus01.mazegenerator.api;

public class ApiJsonObject {
    public String algName;
    public int width;
    public int height;
    public int[][] maze;

    public ApiJsonObject(String algName, int width, int height) {
        this.algName = algName;
        this.width = width;
        this.height = height;
    }
}
