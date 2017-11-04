import CampusServerCorba.CampusServerInterface;
import CampusServerCorba.CampusServerInterfaceHelper;
import domain.Campus;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;

import java.util.Scanner;

@Deprecated
public class CampusClient {
    Campus campus;

    public CampusClient(Campus campus) {
        this.campus = campus;
        try {
            String[] params = {"-ORBInitialPort", "6666", "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(params, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            CampusServerInterface addobj = CampusServerInterfaceHelper.narrow(ncRef.resolve_str(campus.abrev));

            Scanner c = new Scanner(System.in);
            System.out.println("Welcome to the addition system:");
            for (; ; ) {
                System.out.println("get udp port?");
                String aa = c.nextLine();
                long r = 0;
                if (aa.equals("yes")) r = addobj.getUdpPort();
                System.out.println("The result for addition is : " + r);
                System.out.println("-----------------------------------");
            }
        } catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        CampusClient campusClient = new CampusClient(Campus.getCampusName(args[0]));
    }
}
