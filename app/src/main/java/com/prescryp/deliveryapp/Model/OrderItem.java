package com.prescryp.deliveryapp.Model;

public class OrderItem {
    private String MedicineName;
    private String Quantity;
    private String PackageContain;
    private String ItemStatus;

    public OrderItem(String medicineName, String quantity, String packageContain, String itemStatus) {
        MedicineName = medicineName;
        Quantity = quantity;
        PackageContain = packageContain;
        ItemStatus = itemStatus;
    }

    public String getMedicineName() {
        return MedicineName;
    }

    public void setMedicineName(String medicineName) {
        MedicineName = medicineName;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getPackageContain() {
        return PackageContain;
    }

    public void setPackageContain(String packageContain) {
        PackageContain = packageContain;
    }

    public String getItemStatus() {
        return ItemStatus;
    }

    public void setItemStatus(String itemStatus) {
        ItemStatus = itemStatus;
    }
}
