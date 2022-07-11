package models;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.PropertyName;

import java.util.Map;

import myapp.utils.FirestoreFieldNames;

public class DataModel {
    @PropertyName(FirestoreFieldNames.DATA_COUNT)
    private Long count;

    @PropertyName(FirestoreFieldNames.DATA_TIMESTAMP)
    private Timestamp ts;

    @PropertyName(FirestoreFieldNames.DATA_ADDED_BY)
    private String who;

    @PropertyName(FirestoreFieldNames.DATA_FACTORY_LINK)
    private String factory;

    @PropertyName(FirestoreFieldNames.DATA_SECTION)
    private String section;

    @PropertyName(FirestoreFieldNames.DATA_ORDER_MAP)
    private Map<String, Object> order;

    @PropertyName(FirestoreFieldNames.DATA_MONTH)
    private Long month;

    @PropertyName(FirestoreFieldNames.DATA_DATE)
    private Long date;

    @PropertyName(FirestoreFieldNames.DATA_TIMEBUCKET)
    private Long timeBucket;

    @PropertyName(FirestoreFieldNames.DATA_YEAR)
    private Long year;

    @PropertyName(FirestoreFieldNames.DATA_FULL_DATE)
    private String fullDate;

    @PropertyName(FirestoreFieldNames.DATA_COUNT)
    public Long getCount() {
        return count;
    }

    @PropertyName(FirestoreFieldNames.DATA_COUNT)
    public void setCount(Long count) {
        this.count = count;
    }

    @PropertyName(FirestoreFieldNames.DATA_TIMESTAMP)
    public Timestamp getTs() {
        return ts;
    }

    @PropertyName(FirestoreFieldNames.DATA_TIMESTAMP)
    public void setTs(Timestamp ts) {
        this.ts = ts;
    }

    @PropertyName(FirestoreFieldNames.DATA_ADDED_BY)
    public String getWho() {
        return who;
    }

    @PropertyName(FirestoreFieldNames.DATA_ADDED_BY)
    public void setWho(String who) {
        this.who = who;
    }

    @PropertyName(FirestoreFieldNames.DATA_FACTORY_LINK)
    public String getFactory() {
        return factory;
    }

    @PropertyName(FirestoreFieldNames.DATA_FACTORY_LINK)
    public void setFactory(String factory) {
        this.factory = factory;
    }

    @PropertyName(FirestoreFieldNames.DATA_SECTION)
    public String getSection() {
        return section;
    }

    @PropertyName(FirestoreFieldNames.DATA_SECTION)
    public void setSection(String section) {
        this.section = section;
    }

    @PropertyName(FirestoreFieldNames.DATA_ORDER_MAP)
    public Map<String, Object> getOrder() {
        return order;
    }

    @PropertyName(FirestoreFieldNames.DATA_ORDER_MAP)
    public void setOrder(Map<String, Object> order) {
        this.order = order;
    }

    @PropertyName(FirestoreFieldNames.DATA_MONTH)
    public Long getMonth() {
        return month;
    }

    @PropertyName(FirestoreFieldNames.DATA_MONTH)
    public void setMonth(Long month) {
        this.month = month;
    }

    @PropertyName(FirestoreFieldNames.DATA_DATE)
    public Long getDate() {
        return date;
    }

    @PropertyName(FirestoreFieldNames.DATA_DATE)
    public void setDate(Long date) {
        this.date = date;
    }

    @PropertyName(FirestoreFieldNames.DATA_YEAR)
    public Long getYear() {
        return year;
    }

    @PropertyName(FirestoreFieldNames.DATA_YEAR)
    public void setYear(Long year) {
        this.year = year;
    }

    @PropertyName(FirestoreFieldNames.DATA_TIMEBUCKET)
    public Long getTimeBucket() {
        return timeBucket;
    }

    @PropertyName(FirestoreFieldNames.DATA_TIMEBUCKET)
    public void setTimeBucket(Long timeBucket) {
        this.timeBucket = timeBucket;
    }

    @PropertyName(FirestoreFieldNames.DATA_FULL_DATE)
    public String getFullDate() {
        return fullDate;
    }

    @PropertyName(FirestoreFieldNames.DATA_FULL_DATE)
    public void setFullDate(String fullDate) {
        this.fullDate = fullDate;
    }
}
