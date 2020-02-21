/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.sama.carrental;

/**
 *
 * @author Test
 */
public class Version {

    private final String INFOTEXT = "Car Rental";
    private final int VERSION = 0;
    private final int RELEASE = 0;
    private final int SUBRELEASE = 1;
    private final String LIB_STATUS = "";

    public final String getDescription() {
        return INFOTEXT;
    }

    public final String getVersion() {
        String text;
        text = VERSION + "." + RELEASE + "." + SUBRELEASE;
        if (LIB_STATUS.length() != 0) {
            text = text + "-" + LIB_STATUS;
        }
        return text;
    }
}
