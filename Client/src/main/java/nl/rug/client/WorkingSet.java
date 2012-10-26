package nl.rug.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import nl.rug.client.database.ChangedPath;
import nl.rug.client.database.Repository;
import nl.rug.client.model.Util;

/**
 * This class keeps track of what has been done and can return the values that 
 * are needed. When the Chord network get a request, this class is responsible 
 * for returning the correct value. This class also determines the order in 
 * which the work is done. This is to make sure our own keyspace is done first 
 * and the surrounding next. The surrounding keyspace has priority over the rest
 * to make a relocation on the ring (surrounding node leaving) less of a 
 * problem, data loss in minimized in this case. 
 * 
 * @author wesschuitema
 */
public class WorkingSet {
    
    private static final Logger logger = Logger.getLogger(WorkingSet.class.getName());
    
    // a list of changed paths in order to account for possible collisions
    private Map<String, List<ChangedPath>> jobs = new HashMap<String, List<ChangedPath>>();
    private LinkedList<String> hashes;
    
    // start out with zero finished jobs, a possible previous run is handled in createJobs()
    private int finishedJobs = 0;
    // total number of jobs is set in createJobs()
    private int numberOfJobs = 0;
    
    /**
     * Create a new WorkingSet. Load all of the ChangedPaths from the database 
     * and create a list of work to be done.
     * 
     * @param repository_address - The address of the repository we are working 
     *  on
     * @param addressHash - The hash that determines the range of jobs we have 
     *  to do first
     */
    public WorkingSet(String repository_address, String addressHash) {
        
        logger.log(Level.INFO, "Initializing working set");
        
        logger.log(Level.INFO, "Fetching repository information from database");
        
        Repository repository = Repository.findByURL(repository_address);
        
        createJobs(repository, addressHash);
        
    }
    
    public int getNumberOfJobs() {
        
        return numberOfJobs;
        
    }
    
    public int getFinishedNumberOfJobs() {
        
        return finishedJobs;
        
    }
    
    public List<ChangedPath> getComplexity(String hash) {
        
        return jobs.get(hash);
        
    }
    
    public void setComplexity(List<ChangedPath> changedPaths) {
        
        // This is not optimal, now for every changedPath the other method will 
        // loop through all other jobs. This is a quick fix, this will keep the 
        // finishedJobs counter correct and was faster to implement.
        for (ChangedPath changedPath : changedPaths) {
            
            setComplexityForFile(changedPath.getPath(), changedPath.getRevision().getNumber(), changedPath.getComplexity());
            
        }
        
    }
    
    private void setComplexityForFile(String file, long revision, int complexity) {

        String hash = Util.getHash(file + revision);
        
        List<ChangedPath> list = jobs.get(hash);
        
        for (ChangedPath path : list) {
            
            if (path.getPath().equals(file)
                    && path.getRevision().getNumber() == revision) {
                
                // update finished job count if complexity has been calculated
                if (complexity != -1) {
                    
                    path.setComplexity(complexity);
                    path.save();

                    finishedJobs++;

                }
                
                return; // done here
                
            }
            
        }
        
    }
    
    /**
     * This method will return the next job to be done. This will be the first 
     * ChangedPath that has a complexity value of -1 that lies within the range
     * of this node. If no jobs remain within the range it will return a job for
     * a value that lies close to this node's range. This means that surrounding 
     * hash values have a priority; this will make leaving nodes less of a 
     * problem
     * 
     * In order to handle collisions, a job can actually be multiple jobs. The 
     * list of jobs have the same hash. This is not very likely but we want to
     * take in into account anyway.
     * 
     * @return A list of jobs with the same hash value
     */
    public List<ChangedPath> nextJob() {
        
        return jobs.get(hashes.pop());
        
    }
    
    /**
     * First set the map used for easy retrieval and then set the linked list 
     * with hashes that we use to keep track of what we need to do next
     * 
     * @param repository The Repository we are working on, needed to get the 
     *  relevant changed paths
     */
    private void createJobs(Repository repository, String addressHash) {
        
        logger.log(Level.INFO, "Retrieving file information to work on from the database");
        
        // gets all the .java files
        List<ChangedPath> changedPaths = ChangedPath.getWorkingSet(repository);
        
        // every changed path represents a job
        numberOfJobs = changedPaths.size();
        
        for (ChangedPath changedPath : changedPaths) {
            
            List<ChangedPath> list = jobs.get(changedPath.getHash());
            
            if (list == null) {
                
                list = new ArrayList<ChangedPath>();
                
            }
            
            list.add(changedPath);
            
            jobs.put(changedPath.getHash(), list);
            
            // update initial finished job count (+1 for every job with a 
            // complexity that's not -1
            if (changedPath.getComplexity() != -1) {
                
                finishedJobs++;
                
            }
            
        }
        
        logger.log(Level.INFO, "Finding the job hash nearest to or equal to {0}", addressHash);
        
        // this can be done more efficiently, we don't care _yet_        
        // determine last value before or equal to addressHash
        int comparisonResult = Integer.MAX_VALUE;
        String found = "";
        for (String value : jobs.keySet()) {
            
            if (value.compareTo(found) < comparisonResult) {
                
                comparisonResult = value.compareTo(found);
                found = value;
                
            }
            
        }
        
        logger.log(Level.INFO, "Last key is {0} for address hash {1}", new Object[]{found, addressHash});
        
        logger.log(Level.INFO, "Creating and soring the list of jobs");
        
        List<String> tmp = new LinkedList<String>(jobs.keySet());
        Collections.sort(tmp);
        Collections.reverse(tmp);
        int foundIndex = tmp.lastIndexOf(found);
        
        List<String> after = tmp.subList(foundIndex, tmp.size() -1);
        List<String> before = tmp.subList(0, foundIndex - 1);
        after.addAll(before);
        
        hashes = new LinkedList<String>(after);
        
        logger.log(Level.INFO, "Job list created with {0} jobs", numberOfJobs);
        
    }
    
}
