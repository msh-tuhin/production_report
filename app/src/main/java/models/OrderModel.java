package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import myapp.utils.FirestoreFieldNames;

public class OrderModel {
    @PropertyName(FirestoreFieldNames.ORDER_NAME)
    private String name;

    @PropertyName(FirestoreFieldNames.ORDER_QUANTITY)
    private Long quantity;

    @PropertyName(FirestoreFieldNames.ORDER_PICTURE)
    private String picture;

    @PropertyName(FirestoreFieldNames.ORDER_ADD_TIME)
    private Timestamp timestamp;

    @PropertyName(FirestoreFieldNames.ORDER_FACTORY_LINK)
    private String factory_link;

    @PropertyName(FirestoreFieldNames.ORDER_CREATOR)
    private String orderCreator;

    @PropertyName(FirestoreFieldNames.ORDER_NAME)
    public String getName() {
        return name;
    }

    @PropertyName(FirestoreFieldNames.ORDER_NAME)
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName(FirestoreFieldNames.ORDER_QUANTITY)
    public Long getQuantity() {
        return quantity;
    }

    @PropertyName(FirestoreFieldNames.ORDER_QUANTITY)
    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    @PropertyName(FirestoreFieldNames.ORDER_PICTURE)
    public String getPicture() {
        return picture;
    }

    @PropertyName(FirestoreFieldNames.ORDER_PICTURE)
    public void setPicture(String picture) {
        this.picture = picture;
    }

    @PropertyName(FirestoreFieldNames.ORDER_ADD_TIME)
    public Timestamp getTimestamp() {
        return timestamp;
    }

    @PropertyName(FirestoreFieldNames.ORDER_ADD_TIME)
    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    @PropertyName(FirestoreFieldNames.ORDER_FACTORY_LINK)
    public String getFactory_link() {
        return factory_link;
    }

    @PropertyName(FirestoreFieldNames.ORDER_FACTORY_LINK)
    public void setFactory_link(String factory_link) {
        this.factory_link = factory_link;
    }

    @PropertyName(FirestoreFieldNames.ORDER_CREATOR)
    public String getOrderCreator() {
        return orderCreator;
    }

    @PropertyName(FirestoreFieldNames.ORDER_CREATOR)
    public void setOrderCreator(String orderCreator) {
        this.orderCreator = orderCreator;
    }
}
