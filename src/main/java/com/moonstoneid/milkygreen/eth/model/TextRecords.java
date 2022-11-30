package com.moonstoneid.milkygreen.eth.model;

public enum TextRecords implements TextRecordsInterface {

    EMAIL("email"),
    URL("url"),
    NAME("name");

    private final String key;

    /**
     * Constructs a new enumeration constant with the provided key.
     *
     * @param key   The text record key.
     */
    TextRecords(String key) {
        this.key = key;
    }

    public String getValue(){
        return key;
    }

}

