package org.udg.pds.springtodo.entity;

public class SearchCriteria {

    public SearchCriteria(String key, String operation, Object value){
        this.key = key;
        this.operation = operation;
        this.value = value;
    }

    private String key;
    private String operation;
    private Object value;

    public String getKey() {
        return this.key;
    }

    public String getOperation() {
        return this.operation;
    }

    public Object getValue() {
        return this.value;
    }

}
