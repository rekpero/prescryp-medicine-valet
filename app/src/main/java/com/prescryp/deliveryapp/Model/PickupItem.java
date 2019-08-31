package com.prescryp.deliveryapp.Model;

import java.util.List;

public class PickupItem {
    private String SellerMobileNumber;
    private String SellerStoreName;
    private String SellerStoreContact;
    private String SellerStoreAddress;
    private String StoreLatitude;
    private String StoreLongitude;
    private List<OrderItem> orderItemsList;

    public PickupItem(String sellerMobileNumber, String sellerStoreName, String sellerStoreContact, String sellerStoreAddress, String storeLatitude, String storeLongitude, List<OrderItem> orderItemsList) {
        SellerMobileNumber = sellerMobileNumber;
        SellerStoreName = sellerStoreName;
        SellerStoreContact = sellerStoreContact;
        SellerStoreAddress = sellerStoreAddress;
        StoreLatitude = storeLatitude;
        StoreLongitude = storeLongitude;
        this.orderItemsList = orderItemsList;
    }

    public String getSellerMobileNumber() {
        return SellerMobileNumber;
    }

    public void setSellerMobileNumber(String sellerMobileNumber) {
        SellerMobileNumber = sellerMobileNumber;
    }

    public String getSellerStoreName() {
        return SellerStoreName;
    }

    public void setSellerStoreName(String sellerStoreName) {
        SellerStoreName = sellerStoreName;
    }

    public String getSellerStoreContact() {
        return SellerStoreContact;
    }

    public void setSellerStoreContact(String sellerStoreContact) {
        SellerStoreContact = sellerStoreContact;
    }

    public String getSellerStoreAddress() {
        return SellerStoreAddress;
    }

    public void setSellerStoreAddress(String sellerStoreAddress) {
        SellerStoreAddress = sellerStoreAddress;
    }

    public String getStoreLatitude() {
        return StoreLatitude;
    }

    public void setStoreLatitude(String storeLatitude) {
        StoreLatitude = storeLatitude;
    }

    public String getStoreLongitude() {
        return StoreLongitude;
    }

    public void setStoreLongitude(String storeLongitude) {
        StoreLongitude = storeLongitude;
    }

    public List<OrderItem> getOrderItemsList() {
        return orderItemsList;
    }

    public void setOrderItemsList(List<OrderItem> orderItemsList) {
        this.orderItemsList = orderItemsList;
    }
}
