package org.fbb.balkna.model;

import org.fbb.balkna.model.primitives.Training;


/**
 *
 * @author jvanek
 */
public interface Trainable extends  Substituable, Statisticable, Exportable{
    
     public Training getTraining();
     public int  getTrainingPointer();
    
   
    
}
