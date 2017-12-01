package service.frontend;

import CampusServerCorba.CampusServerInterface;
import CampusServerCorba.CampusServerInterfaceHelper;
import CampusServerCorba.CampusServerInterfacePOA;
import domain.Lock;
import lombok.Getter;
import org.omg.CORBA.ORB;
import org.omg.CORBA.ORBPackage.InvalidName;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.CosNaming.NamingContextPackage.CannotProceed;
import org.omg.CosNaming.NamingContextPackage.NotFound;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;
import org.omg.PortableServer.POAManagerPackage.AdapterInactive;
import org.omg.PortableServer.POAPackage.ServantNotActive;
import org.omg.PortableServer.POAPackage.WrongPolicy;
import service.Properties;
import service.domain.RmResponse;

import java.util.Calendar;

public class FrontEnd extends CampusServerInterfacePOA implements Runnable{
    /**
     * Orb initialization parameters
     */
    private final String[] params;

//    public final Logger log;
    /**
     * Message Grouper
     */
    @Getter
    private final MessageBook messageBook;
    /**
     * lock for message map
     */
    @Getter
    private final Lock mapLock = new Lock();



    /**
     * Constructor
     * @param params orb initialization parameters
     */
    public FrontEnd(String[] params){
        this.params = params;
        messageBook = new MessageBook();
//        log = Logger.getLogger(Server.class.toString());//todo
        initLogger();
    }
    @Override
    public void run(){
        initUdpListeningPort();
        initializeORB();
    }
    /**
     * initiate log file
     */
    private void initLogger(){

    }
    private void initializeORB() {
        try {
            // create and initialize the ORB
            //getInboundMessage reference to rootpoa &amp; activate the POAManager
            System.out.println("Front end initializing CORBA");
            ORB orb = ORB.init(params, null);
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            //getInboundMessage object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(this);
            CampusServerInterface href = CampusServerInterfaceHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name(Properties.ORB_SERVER_NAME);
            ncRef.rebind(path, href);

            System.out.println(Properties.ORB_SERVER_NAME + " ready");
//            synchronized (this.logLock) {
//                log.info("\n" + campus.name + " ORB initialized and listening"
//                        + "\n" + campus.name + " has been initialized");
//            }
            while (true) {
                orb.run();
            }
        } catch (InvalidName | AdapterInactive | org.omg.CosNaming.NamingContextPackage.InvalidName
                | ServantNotActive | WrongPolicy | CannotProceed | NotFound invalidName) {
            invalidName.printStackTrace();
        }
    }

    @Override
    @Deprecated
    public int getUdpPort(){
        return 0;
    }


    @Override
    public String createRoom(String json){
        System.out.println("Front end receives the client request for room creation " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "create", this);
        return message.process();
    }

    @Override
    public String deleteRoom(String json){
        System.out.println("Front end receives the client request for room deletion " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "delete", this);
        return message.process();
    }

    @Override
    public String bookRoom(String json){
        System.out.println("Front end receives the client request for room booking " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "book", this);
        return message.process();
    }

    @Override
    public String switchRoom(String json){
        System.out.println("Front end receives the client request for room switch " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "switch", this);
        return message.process();
    }

    @Override
    public String getAvailableTimeSlotCount(String json){
        System.out.println("Front end receives the client request for room count" + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "count", this);
        return message.process();
    }

    @Override
    public String getAvailableTimeSlotByRoom(String json){
        System.out.println("Front end receives the client request for room info " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "room", this);
        return message.process();
    }

    @Override
    public String cancelBooking(String json){
        System.out.println("Front end receives the client request for room cancellation " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "cancel", this);
        return message.process();
    }

    @Override
    public boolean checkAdminId(String json){
        System.out.println("Front end receives the client request for admin check " + Calendar.getInstance().getTime());
        ClientInboundMessage message = new ClientInboundMessage(json, "check", this);
        String response = message.process();
        return Boolean.parseBoolean(response);
    }

    /**
     * listen to RM responses
     */
    private void initUdpListeningPort(){
        System.out.println("Front End initializing Udp Listener at port " + Properties.FRONTEND_UDP_LISTENING_PORT);
        new Thread(new FrontEndUdpListener(this)).start();
        System.out.println("Front End Udp listener initialized");
    }

    public void addRmResponseToInboundMessage(String msgId, RmResponse rmResponse) {
        synchronized (this.getMapLock()){
            messageBook.getInboundMessage(msgId).addRmResponseToInboundMessage(rmResponse);
        }
    }
}
