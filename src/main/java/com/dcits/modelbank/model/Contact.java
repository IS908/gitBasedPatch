package com.dcits.modelbank.model;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created on 2017-12-14 23:00.
 *
 * @author kevin
 */
public class Contact {
    @JsonProperty
    public String name;
    @JsonProperty
    public int age;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public int getAge() {
        return age;
    }
    public void setAge(int age) {
        this.age = age;
    }
}
