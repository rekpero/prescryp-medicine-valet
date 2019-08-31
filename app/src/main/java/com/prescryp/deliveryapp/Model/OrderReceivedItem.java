package com.prescryp.deliveryapp.Model;

import java.util.List;

public class OrderReceivedItem {
    private String OrderNumber;
    private String DateOfOrder;
    private String TimeOfOrder;
    private String Status;
    private String GrandTotal;
    private List<String> OrderItems;

    public OrderReceivedItem(String orderNumber, String dateOfOrder, String timeOfOrder, String status, String grandTotal, List<String> orderItems) {
        OrderNumber = orderNumber;
        DateOfOrder = dateOfOrder;
        TimeOfOrder = timeOfOrder;
        Status = status;
        GrandTotal = grandTotal;
        OrderItems = orderItems;
    }

    public String getOrderNumber() {
        return OrderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        OrderNumber = orderNumber;
    }

    public String getDateOfOrder() {
        return DateOfOrder;
    }

    public void setDateOfOrder(String dateOfOrder) {
        DateOfOrder = dateOfOrder;
    }

    public String getTimeOfOrder() {
        return TimeOfOrder;
    }

    public void setTimeOfOrder(String timeOfOrder) {
        TimeOfOrder = timeOfOrder;
    }

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getGrandTotal() {
        return GrandTotal;
    }

    public void setGrandTotal(String grandTotal) {
        GrandTotal = grandTotal;
    }

    public List<String> getOrderItems() {
        return OrderItems;
    }

    public void setOrderItems(List<String> orderItems) {
        OrderItems = orderItems;
    }
}
