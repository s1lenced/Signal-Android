package org.thoughtcrime.securesms.registerService.entity;


import java.io.Serializable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ContactFromApi implements Serializable
{

    @SerializedName("number")
    @Expose
    private String number;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("surname")
    @Expose
    private String surname;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("reg_status")
    @Expose
    private String regStatus;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("is_online")
    @Expose
    private boolean isOnline;
    @SerializedName("verificationCode")
    @Expose
    private String verificationCode;
    @SerializedName("token")
    @Expose
    private String token;
    private final static long serialVersionUID = -4201972001652756229L;

    /**
     * No args constructor for use in serialization
     *
     */
    public ContactFromApi() {
    }

    /**
     * @param number
     * @param name
     * @param surname
     * @param email
     * @param role
     * @param status
     * @param created
     * @param updated
     * @param isOnline
     * @param token
     */
    public ContactFromApi(String number, String name, String surname, String email, String role, String status, String created, String updated, boolean isOnline, String verificationCode, String token) {
        super();
        this.number = number;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.role = role;
        this.regStatus = status;
        this.created = created;
        this.updated = updated;
        this.isOnline = isOnline;
        this.verificationCode = verificationCode;
        this.token = token;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getRegStatus() {
        return regStatus;
    }

    public void setRegStatus(String regStatus) {
        this.regStatus = regStatus;
    }

    public String getVerificationCode() {
        return verificationCode;
    }

    public void setVerificationCode(String verificationCode) {
        this.verificationCode = verificationCode;
    }

    public boolean getIsOnline() {
        return isOnline;
    }

    public void setIsOnline(boolean isOnline) {
        this.isOnline = isOnline;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
