
package org.fbb.balkna.model.primitives.history;

import java.util.ArrayList;

/**
 *
 * @author jvanek
 * @param <E>
 */
public class NonRepeatedArrayList<E> extends  ArrayList<E> {

    @Override
    public synchronized boolean add(E e) {
        if(this.isEmpty()){
            return super.add(e);
            
        } else {
            if (!super.get(super.size()-1).equals(e)){
                return super.add(e);
            }
        }
        return false;
    }
    
    
    
    
}
