package org.fbb.balkna.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jvanek
 */
public interface ImagesSaver {

    public void writeExercisesImagesToDir(File imgDir, List<String> i) throws IOException;

    public void writeTrainingsImagesToDir(File imgDir, List<String> is) throws IOException;

}
