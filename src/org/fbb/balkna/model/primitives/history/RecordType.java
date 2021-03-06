package org.fbb.balkna.model.primitives.history;

import org.fbb.balkna.model.Translator;

/**
 *
 * @author jvanek
 */
public enum RecordType {
    FINISHED, STARTED, CANCELED, FINISHED_WITH_SKIPPS, CONTINUED, MODIFIED, NOW /*meta element for now*/;
    
    
    public String toNiceString() {
        return Translator.R(toString());
    }
}
