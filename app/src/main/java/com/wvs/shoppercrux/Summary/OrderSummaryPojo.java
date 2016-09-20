package com.wvs.shoppercrux.Summary;

/**
 * Created by Varuni on 19-09-2016.
 */
public class OrderSummaryPojo {

    private String productName,quanity,total;

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setQuanity(String quanity) {
        this.quanity = quanity;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getProductName() {
        return productName;
    }

    public String getQuanity() {
        return quanity;
    }

    public String getTotal() {
        return total;
    }
}
