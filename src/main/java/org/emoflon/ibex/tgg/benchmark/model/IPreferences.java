package org.emoflon.ibex.tgg.benchmark.model;

import javax.json.JsonObject;

public interface IPreferences {
    
    /**
     * Converts the preferences object into a {@link JsonObject}.
     * 
     * @return JSON representation
     */
    public JsonObject toJson();
    
}
