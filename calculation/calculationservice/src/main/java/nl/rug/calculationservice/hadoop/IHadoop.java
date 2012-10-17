/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.rug.calculationservice.hadoop;

/**
 *
 * @author frbl
 */
public interface IHadoop {

    /**
     * Performs the mane calculation. This will most probably start a job, which
     * Hadoop can use to perform its mapping and reducing steps.
     */
    public void performCalculation();
}
