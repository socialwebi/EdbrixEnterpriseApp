package com.edbrix.enterprise.Models;

/**
 * Created by rajk on 05/01/18.
 */

public class SalutationsData {
    private String Salutation;

    private int Id;

    public String getSalutation() {
        return Salutation;
    }

    public void setSalutation(String Salutation) {
        this.Salutation = Salutation;
    }

    public int getId() {
        return Id;
    }

    public void setId(int Id) {
        this.Id = Id;
    }

    @Override
    public String toString() {
        return "ClassPojo [Salutation = " + Salutation + ", Id = " + Id + "]";
    }
}
