package com.moonstoneid.web3login.ens.model;

public enum TextRecords implements TextRecordsInterface {

    EMAIL("email"),
    URL("url"),
    AVATAR("avatar"),
    VND_TWITTER("vnd.twitter"),
    VND_TELEGRAM_UPPER_CASE("VND.TELEGRAM"),
    VND_TELEGRAM_LOWER_CASE("vnd.telegram"),
    VND_GITHUB("vnd.github"),
    DESCRIPTION("description"),
    NOTICE("notice"),
    KEYWORDS("keywords"),
    COM_TWITTER("com.twitter"),
    COM_GITHUB("com.github"),
    ORG_TELEGRAM("org.telegram"),
    COM_LINKEDIN("com.linkedin"),
    NAME("name"),
    COM_DISCORD("com.discord"),
    COM_REDDIT("com.reddit"),
    LOCATION("location"),
    ETH_ENS_DELEGATE("eth.ens.delegate"),
    SNAPSHOT("snapshot"),
    HEADER("header");

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

