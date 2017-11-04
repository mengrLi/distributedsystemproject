import domain.Campus;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import service.server.CampusServerCorba.CampusServerInterface;
import service.server.CampusServerCorba.CampusServerInterfaceHelper;

import java.util.Scanner;

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
                System.out.println("Enter a:");
                String aa = c.nextLine();
                System.out.println("Enter b:");
                String bb = c.nextLine();
                int a = Integer.parseInt(aa);
                int b = Integer.parseInt(bb);
                long r = addobj.add(a, b);
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
