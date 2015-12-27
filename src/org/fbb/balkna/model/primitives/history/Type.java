package org.fbb.balkna.model.primitives.history;

import org.fbb.balkna.model.Translator;

/**
 *
 * @author jvanek
 */
public enum Type {
    FINISHED, STARTED, CANCELED, FINISHED_WITH_SKIPPS;
    
    
    public String toNiceString() {
        return Translator.R(toString());
    }
}
