package com.leewyatt.fxtools.utils.convert;

public class RGB {
 
    int red;
     
    int green;
     
    int blue;
     
    public RGB(){ }
     
    public RGB(int red,int green,int blue){
        this.red = red;
        this.blue = blue;
        this.green = green;
    }
     
    @Override
    public String toString() {
        return "RGB {" + red + ", " + green + ", " + blue + "}";  
    }  
     
}
