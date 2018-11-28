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
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("role")
    @Expose
    private String role;
    @SerializedName("status")
    @Expose
    private String status;
    private final static long serialVersionUID = -4201972001652756229L;

    /**
     * No args constructor for use in serialization
     *
     */
    public ContactFromApi() {
    }

    /**
     *
     * @param status
     * @param email
     * @param name
     * @param role
     * @param number
     */
    public ContactFromApi(String number, String name, String email, String role, String status) {
        super();
        this.number = number;
        this.name = name;
        this.email = email;
        this.role = role;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
