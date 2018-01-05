package com.edbrix.enterprise.Models;

/**
 * Created by rajk on 05/01/18.
 */

public class SalutationsData {
    private String Salutation;

    private String Id;

    public String getSalutation() {
        return Salutation;
    }

    public void setSalutation(String Salutation) {
        this.Salutation = Salutation;
    }

    public String getId() {
        return Id;
    }

    public void setId(String Id) {
        this.Id = Id;
    }

    @Override
    public String toString() {
        return "ClassPojo [Salutation = " + Salutation + ", Id = " + Id + "]";
    }
}
