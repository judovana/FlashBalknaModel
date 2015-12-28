package org.fbb.balkna.model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.List;
import org.fbb.balkna.model.primitives.history.Record;

/**
 *
 * @author jvanek
 */
public interface Statisticable {
    
    public String getId();
    public void load(BufferedReader br) throws IOException;
    public void save(BufferedWriter bw) throws IOException;
    public List<Record> getRecords();
    public File getFile();
    public void addRecord(Record r);
    
}
