package user_v2;

import CampusServerCorba.CampusServerInterface;
import CampusServerCorba.CampusServerInterfaceHelper;
import com.sun.corba.se.impl.activation.ORBD;
import domain.Campus;
import domain.Lock;
import domain.Room;
import org.omg.CORBA.ORB;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import service.Properties;
import service.remote_interface.UserInterface;
import service.server.requests.GetTimeSlotByRoomRequest;

import java.io.IOException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public abstract class ClientV2 implements UserInterface {
    protected final Campus CAMPUS;
    protected final int ID;
    protected final String FULL_ID;
    protected CampusServerInterface campusInterface;
    protected final Logger LOG;
    private FileHandler fileHandler;
    protected final Lock LOG_LOCK = new Lock();


    ClientV2(Campus campus, String type, int id) {
        this.CAMPUS = campus;
        this.ID = id;
        FULL_ID = campus.abrev + type + id;
        LOG = Logger.getLogger(FULL_ID + " " + ClientV2.class);
        initLogger();
        setConnection();
    }

    private void setConnection() {
        try {
            String[] params = {"-ORBInitialPort", Properties.ORB_PORT, "-ORBInitialHost", "localhost"};
            ORB orb = ORB.init(params, null);
            org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
            NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
            campusInterface = CampusServerInterfaceHelper.narrow(ncRef.resolve_str(Properties.ORB_SERVER_NAME));
        } catch (Exception e) {
            System.out.println("Hello Client exception: " + e);
            e.printStackTrace();
        }
    }

    private void initLogger() {
        try {
            String dir = "src/client_log/";
            LOG.setUseParentHandlers(false);
            fileHandler = new FileHandler(dir + FULL_ID + ".LOG", true);
            LOG.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Map<String, Room> getAvailableTimeSlot(Calendar date, Campus campus) {
        synchronized (this.LOG_LOCK) {
            LOG.info("\n" + FULL_ID + " check all available time slots on " + date.getTime() + " in " + campus.name);
        }
        return new GetTimeSlotByRoomRequest(date, campus).sendRequest(campusInterface);
    }

    @Override
    public void logout() {
        synchronized (this.LOG_LOCK) {
            LOG.info("\n" + FULL_ID + "Logged out");
        }
        fileHandler.close();
    }

    @Override
    public void closeLogFileHandler() {
        fileHandler.close();
    }
}
