public class CampusServerStarter {

    public static void main(String[] args) {
//        try {
//            // create and initialize the ORB
//            //get reference to rootpoa &amp; activate the POAManager
//            String[] params = {"-ORBInitialPort", "6666", "-ORBInitialHost", "localhost"};
//            ORB orb = ORB.init(params, null);
//            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
//            rootPOA.the_POAManager().activate();
//
//            //create servant and register it with the ORB
//            CampusServerObj campusServerObj = new CampusServerObj(orb);
//
//            //get object reference from the servant
//            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(campusServerObj);
//            CampusServerInterface href = CampusServerInterfaceHelper.narrow(ref);
//
//            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
//            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
//
//            NameComponent path[] = ncRef.to_name("Dorval");
//            ncRef.rebind(path, href);
//
//            System.out.println(ncRef.toString());
//
//            System.out.println("Campus server ready");
//
//            while(true){
//                orb.run();
//            }
//
//        } catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName | ServantNotActive | WrongPolicy | CannotProceed | NotFound invalidName) {
//            invalidName.printStackTrace();
//        }
        System.out.println("HelloServer Exiting ...");
    }
}
