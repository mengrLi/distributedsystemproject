package user_v2;

import CampusServerCorba.CampusServerInterface;
import CampusServerCorba.CampusServerInterfaceHelper;
import domain.Campus;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

public abstract class ClientV2 implements Runnable{
    protected Campus campus;
    protected int id;
    protected String fullID;
    protected CampusServerInterface campusInterface;

    ClientV2(Campus campus, int id){
        this.campus = campus;
        this.id = id;
        setConnection();
    }

    private void setConnection() {
        try {
            String[] params = {"-ORBInitialPort", "6666", "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(params, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            campusInterface = CampusServerInterfaceHelper.narrow(ncRef.resolve_str(campus.abrev));
        } catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }
    }
}
