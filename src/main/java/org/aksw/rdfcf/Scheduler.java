package org.aksw.rdfcf;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class Scheduler {

    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
    
    /**
     * Removes data-sets older then 30 minutes from the file-system
     * each 5min
     */
    @Scheduled(fixedRate = 5 * 60000)
    public void deleteOldFiles() {
    	
    	log.info("remove old data");
    	
    	SingletonStore store = SingletonStore.getInstance();
    	
    	store.autoDelete();
    	
    }
}
