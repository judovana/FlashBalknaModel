
package org.fbb.balkna.model.graphs;

import java.text.SimpleDateFormat;

/**
 *
 * @author jvanek
 */
public interface Steppable {

    public static final SimpleDateFormat sdfMore = new SimpleDateFormat("d.M");
    public static final SimpleDateFormat sdfDay = new SimpleDateFormat("HH:mm");
    
    public long getStep() ;

    public void setStep(long step) ;

    public void setEasyFormater(SimpleDateFormat easyFormater) ;

    public SimpleDateFormat getEasyFormater();

}
