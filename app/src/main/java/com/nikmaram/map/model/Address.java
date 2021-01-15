package com.nikmaram.map.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Address {

@SerializedName("formatted")
@Expose
private String formatted;
@SerializedName("components")
@Expose
private List<Component> components = null;

public String getFormatted() {
return formatted;
}

public void setFormatted(String formatted) {
this.formatted = formatted;
}

public List<Component> getComponents() {
return components;
}

public void setComponents(List<Component> components) {
this.components = components;
}

}