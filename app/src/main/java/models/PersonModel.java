package models;

import com.google.firebase.firestore.PropertyName;

import myapp.utils.FirestoreFieldNames;

public class PersonModel {
    @PropertyName(FirestoreFieldNames.PERSON_NAME)
    private String name;

    @PropertyName(FirestoreFieldNames.PERSON_EMAIL)
    private String email;

    @PropertyName(FirestoreFieldNames.PERSON_MEMBER_OF)
    private String factory;

    @PropertyName(FirestoreFieldNames.PERSON_STATUS)
    private Long status;

    @PropertyName(FirestoreFieldNames.PERSON_SECTION)
    private String section;

    @PropertyName(FirestoreFieldNames.PERSON_NAME)
    public String getName() {
        return name;
    }

    @PropertyName(FirestoreFieldNames.PERSON_NAME)
    public void setName(String name) {
        this.name = name;
    }

    @PropertyName(FirestoreFieldNames.PERSON_EMAIL)
    public String getEmail() {
        return email;
    }

    @PropertyName(FirestoreFieldNames.PERSON_EMAIL)
    public void setEmail(String email) {
        this.email = email;
    }

    @PropertyName(FirestoreFieldNames.PERSON_MEMBER_OF)
    public String getFactory() {
        return factory;
    }

    @PropertyName(FirestoreFieldNames.PERSON_MEMBER_OF)
    public void setFactory(String factory) {
        this.factory = factory;
    }

    @PropertyName(FirestoreFieldNames.PERSON_MEMBER_OF)
    public Long getStatus() {
        return status;
    }

    @PropertyName(FirestoreFieldNames.PERSON_MEMBER_OF)
    public void setStatus(Long status) {
        this.status = status;
    }

    @PropertyName(FirestoreFieldNames.PERSON_SECTION)
    public String getSection() {
        return section;
    }

    @PropertyName(FirestoreFieldNames.PERSON_SECTION)
    public void setSection(String section) {
        this.section = section;
    }
}
