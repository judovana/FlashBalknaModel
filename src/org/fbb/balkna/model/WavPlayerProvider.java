package org.fbb.balkna.model;

import java.net.URL;

/**
 *
 * @author jvanek
 */
public interface  WavPlayerProvider {
    
    WavPlayer createPlayer(URL u);
    
}
