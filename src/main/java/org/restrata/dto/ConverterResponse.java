package org.restrata.dto;

import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
public class ConverterResponse {
    public List<String> jsonObjects;

    public List<String> getService() {
        return jsonObjects;
    }

    public void setService(List<String> jsonObjects) {
        this.jsonObjects = jsonObjects;
    }
}
