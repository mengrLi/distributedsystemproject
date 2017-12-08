package Main;

import service.Properties;
import service.rm.ReplicaManager;

public class RM3_Run {
    public static void start(){
        System.out.println("Initializing " + Properties.rm3Name
                + " at " + Properties.RM_3_INET + ":" + Properties.RM_3_LISTENING_PORT);

        ReplicaManager replicaManager3 = new ReplicaManager(Properties.rm3Name, Properties.RM_3_INET, Properties.RM_3_LISTENING_PORT);

        new Thread(replicaManager3).start();

        System.out.println(Properties.rm3Name + " at " + Properties.RM_3_INET
                + ":" + Properties.RM_3_LISTENING_PORT + " initialized");
    }
    public static void main(String[] args){
        start();
    }
}
