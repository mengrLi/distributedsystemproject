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

import java.io.IOException;
import java.util.Calendar;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    public final Logger log;
    private FileHandler fileHandler;


    /**
     * Constructor
     * @param params orb initialization parameters
     */
    public FrontEnd(String[] params){
        this.params = params;
        messageBook = new MessageBook();
        log = Logger.getLogger(FrontEnd.class.toString());
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
        try {
            String dir = "src/log/frontend_log/";
            log.setUseParentHandlers(false);
            fileHandler = new FileHandler(dir + "FrontEnd.LOG", Properties.appendLog);
            log.addHandler(fileHandler);
            SimpleFormatter formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);

            log.info("\nFront end log loaded");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void initializeORB() {
        try {
            // create and initialize the ORB
            //getInboundMessage reference to rootpoa &amp; activate the POAManager
            String l = "Front end initializing CORBA";
            log.info(l+"\n");
            System.out.println(l);
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

            l = Properties.ORB_SERVER_NAME + " ready";
            log.info(l+"\n");
            System.out.println(l);

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
        System.out.println(json);
        String l = "1. Front end receives the client request for room creation ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "create", this);
        return message.process();
    }

    @Override
    public String deleteRoom(String json){
        System.out.println(json);
        String l = "1. Front end receives the client request for room deletion ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "delete", this);
        return message.process();
    }

    @Override
    public String bookRoom(String json){
        System.out.println(json);
        String l = "1. Front end receives the client request for room booking ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "book", this);
        return message.process();
    }

    @Override
    public String switchRoom(String json){
        System.out.println(json);
        String l = "1. Front end receives the client request for room switch ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "switch", this);
        return message.process();
    }

    @Override
    public String getAvailableTimeSlotCount(String json){
        String l = "1. Front end receives the client request for room count";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "count", this);
        return message.process();
    }

    @Override
    public String getAvailableTimeSlotByRoom(String json){
        String l = "1. Front end receives the client request for room info ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "room", this);
        return message.process();
    }

    @Override
    public String cancelBooking(String json){
        System.out.println(json);
        String l = "1. Front end receives the client request for room cancellation ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "cancel", this);
        return message.process();
    }

    @Override
    public boolean checkAdminId(String json){
        String l = "1. Front end receives the client request for admin check ";
        log.info("\n"+l+"\n"+json+"\n");
        System.out.println(l);
        ClientInboundMessage message = new ClientInboundMessage(json, "check", this);
        String response = message.process();
        return Boolean.parseBoolean(response);
    }

    /**
     * listen to RM responses
     */
    private void initUdpListeningPort(){
        String l = "Front End initializing Udp Listener at port " + Properties.FRONTEND_UDP_LISTENING_PORT;

        System.out.println(l);
        new Thread(new FrontEndUdpListener(this)).start();

        String l1 = "Front End Udp listener initialized";

        log.info("\n"+l+"\n"+l1+"\n");
    }

    public void addRmResponseToInboundMessageFE(String msgId, RmResponse rmResponse) {
        synchronized (this.getMapLock()){
            messageBook.getInboundMessage(msgId).addRmResponseToInboundMessage(rmResponse);
        }
        log.info("Message id : " + msgId + "\n: " + rmResponse.getResponseMessage()
                + "\nRM response has been received from " + rmResponse.getInet()+":"+rmResponse.getRmPort() +"\n");
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        fileHandler.close();
    }
}
