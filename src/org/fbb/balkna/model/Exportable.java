package org.fbb.balkna.model;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 *
 * @author jvanek
 */
public interface Exportable {

    public String getStory();

    public String getStory(boolean html);

    public File export(File root, ImagesSaver im) throws IOException;

    public String[] getImages();

    public List<String> getExerciseImages();
;

}
