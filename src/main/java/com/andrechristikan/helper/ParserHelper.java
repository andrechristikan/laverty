/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.andrechristikan.helper;

import io.vertx.sqlclient.data.Numeric;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Syn-User
 */
public class ParserHelper {
        
    public final LocalDate parseLocalDate(String value, LocalDate def) {
        try {
            if (value == null) {
                return def;
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate dt = LocalDate.parse(value,dtf);
                return dt;
            }
        } catch (Exception ex) {
            return def;
        }
    }
    
    
    public final LocalDateTime parseLocalDateTime(String value, LocalDateTime def) {
        try {
            if (value == null) {
                return def;
            } else {
                DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd H:m:s");
                LocalDateTime dt = LocalDateTime.parse(value, dtf);
                return dt;
            }
        } catch (Exception ex) {
            return def;
        }
    }

    public final int parseInt(String value, int def) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public final String parseString(String value, String def) {
        try {
            return String.valueOf(value);
        } catch (Exception ex) {
            return def;
        }
    }
        
    public final Long parseLong(String value, Long def) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public final Double parseDouble(String value, Double def) {
        try {
            if (value == null) {
                return def;
            } else {
                return Double.parseDouble(value);
            }
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public final Numeric parseNumeric(String value, Numeric def) {
        try {
            if (value == null) {
                return def;
            } else {
                return Numeric.parse(value);
            }
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public final Boolean parseBoolean(String value, Boolean def) {
        try {
            if (value == null) {
                return def;
            } else {
                return Boolean.parseBoolean(value);
            }
        } catch (NumberFormatException ex) {
            return def;
        }
    }
    
    public final OffsetDateTime parseOffsetDateTime(String value, OffsetDateTime def) {
        try {
            if (value == null) {
                return def;
            } else {
                
                DateTimeFormatter dtf = DateTimeFormatter.ISO_INSTANT;
                Instant instant = Instant.from(dtf.parse(value));
                OffsetDateTime offsetDateTime = OffsetDateTime.ofInstant(instant, ZoneOffset.UTC);
                return offsetDateTime;
            }
        } catch (NumberFormatException ex) {
            return def;
        }
    }
}
