package service.frontend;

import CampusServerCorba.CampusServerInterface;
import CampusServerCorba.CampusServerInterfaceHelper;
import CampusServerCorba.CampusServerInterfacePOA;
import com.google.gson.GsonBuilder;
import domain.Lock;
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
import service.domain.RmResponse;
import service.server.Server;
import service.server.UdpResponder;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.logging.Logger;

public class FrontEnd extends CampusServerInterfacePOA implements Runnable{

    private final String[] params;
    public final Logger log;
    private final String SERVER_NAME= "server";
    private final MessageRecords messageMap;
    private final int udpListeningPort;


    private final Lock mapLock = new Lock();


    public FrontEnd(String[] params, int udpListeningPort){
        this.params = params;
        messageMap = new MessageRecords();
        log = Logger.getLogger(Server.class.toString());//todo
        initLogger();
        this.udpListeningPort = udpListeningPort;


    }

    private void initLogger(){

    }

    /**
     * listen to RM responses
     */
    private void initUdpListeningPort(){
        DatagramSocket socket = null;
        try {
            socket = new DatagramSocket(udpListeningPort);
            byte[] buffer;
            DatagramPacket request;
            while (true) {
                buffer = new byte[100000];
                request = new DatagramPacket(buffer, buffer.length);
                socket.receive(request);

                //getInboundMessage message from rm response, transform to string
                String rmResponseJson = new String(request.getData());
                //transform from json string to object
                RmResponse rmResponse = new GsonBuilder().create().fromJson(rmResponseJson, RmResponse.class);
                String msgId = rmResponse.getSequencerId();

                synchronized(mapLock){
                    messageMap.getInboundMessage(msgId).addRmResponseToInboundMessage(rmResponse);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (socket != null) socket.close();
        }
    }

    @Override
    public void run(){
        initializeORB();
    }

    private void initializeORB() {
        try {
            // create and initialize the ORB
            //getInboundMessage reference to rootpoa &amp; activate the POAManager

            ORB orb = ORB.init(params, null);
            POA rootPOA = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPOA.the_POAManager().activate();

            //getInboundMessage object reference from the servant
            org.omg.CORBA.Object ref = rootPOA.servant_to_reference(this);
            CampusServerInterface href = CampusServerInterfaceHelper.narrow(ref);

            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

            NameComponent path[] = ncRef.to_name(SERVER_NAME);
            ncRef.rebind(path, href);

            System.out.println(SERVER_NAME + " ready");
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
        ClientInboundMessage message = new ClientInboundMessage(json, "createRoom");
        return message.sendResponse();
    }

    @Override
    public String deleteRoom(String json){
        return null;
    }

    @Override
    public String bookRoom(String json){
        return null;
    }

    @Override
    public String switchRoom(String json){
        return null;
    }

    @Override
    public String getAvailableTimeSlotCount(String json){
        return null;
    }

    @Override
    public String getAvailableTimeSlotByRoom(String json){
        return null;
    }

    @Override
    public String cancelBooking(String json){
        return null;
    }

    @Override
    public boolean checkAdminId(String json){
        FrontEnd frontEnd = new FrontEnd();
//        frontEnd.sendResponse();
        return false;
    }


}
