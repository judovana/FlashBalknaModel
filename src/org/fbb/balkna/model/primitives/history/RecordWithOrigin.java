/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.fbb.balkna.model.primitives.history;

import org.fbb.balkna.model.Substituable;
import org.fbb.balkna.model.primitives.Cycle;
import org.fbb.balkna.model.primitives.Exercise;
import org.fbb.balkna.model.primitives.Training;
import org.fbb.balkna.swing.locales.SwingTranslator;

/**
 *
 * @author jvanek
 */
public class RecordWithOrigin implements Comparable<RecordWithOrigin> {

    private final Substituable origin;
    private final Record record;

    public RecordWithOrigin(Substituable origin, Record record) {
        this.origin = origin;
        this.record = record;
    }

    public Substituable getOrigin() {
        return origin;
    }

    public Record getRecord() {
        return record;
    }

    @Override
    public int compareTo(RecordWithOrigin t) {
        return (int) (record.getWhen() - t.record.getWhen());
    }

    @Override
    public String toString() {
        return record.toNiceString() + " - " + origin.getName() + " (" + classToString() + ")";
    }

    private String classToString() {
        if (origin instanceof Exercise) {
            return (SwingTranslator.R("mainTabExercise"));
        } else if (origin instanceof Training) {
            return (SwingTranslator.R("mainTabTrainings"));
        } else if (origin instanceof Cycle) {
            return (SwingTranslator.R("mainTabCycles"));
        } else {
            return "?";
        }
    }

}
