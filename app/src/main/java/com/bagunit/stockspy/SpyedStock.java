package com.bagunit.stockspy;

public class SpyedStock {

    private String ticker , id;
    private double staticPrice,buffer;
    private boolean isTSX;

    public SpyedStock( String ticker, double staticPrice ,  double buffer , String id , boolean isTSX){
        this.ticker = ticker;
        this.staticPrice = staticPrice;
        this.buffer = buffer;
        this.id = id;
        this.isTSX = isTSX;
    }

    public SpyedStock(){
        ticker = id = "";
        staticPrice = buffer = 0.0;
        isTSX = false;
    }


    public String getTicker() {
        return ticker;
    }

    public boolean isTSX() {
        return isTSX;
    }


    public String getId() {
        return id;
    }


    public double getStaticPrice() {
        return staticPrice;
    }

    public void setStaticPrice(double staticPrice) {
        this.staticPrice = staticPrice;
    }

    public double getBuffer() {
        return buffer;
    }

    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }
}
