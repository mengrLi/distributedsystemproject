package service.user;

import domain.CampusName;
import domain.Room;
import domain.TimeSlot;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.Calendar;
import java.util.Map;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;


public class StudentClient extends Client implements UserInterface, Runnable {

    private Logger log = null;

    StudentClient(CampusName campus, int id){
        super(campus, id);
        fullID = campus.abrev + "s" + id;
        initLogger();
        log.info("Student " + fullID + " logged into student client");

    }

    private void initLogger() {
        try {
            String dir = "src/client_log/";
            log = Logger.getLogger(StudentClient.class.getName());
            log.setUseParentHandlers(false);
            FileHandler fileHandler = new FileHandler(dir + campusName.abrev + "s" + id + ".log", true);
            log.addHandler(fileHandler);
            SimpleFormatter simpleFormatter = new SimpleFormatter();
            fileHandler.setFormatter(simpleFormatter);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public String bookRoom(CampusName campusOfInterest, String roomNumber, Calendar date, TimeSlot timeSlot, CampusName campusOfID, int id){
        StringBuilder builder = new StringBuilder();
        builder.append(fullID).append(" books ")
                .append(campusOfInterest.name)
                .append(" room ").append(roomNumber)
                .append(" on ").append(date.getTime())
                .append(" from ").append(timeSlot.getStartTime().getTime())
                .append(" to ").append(timeSlot.getEndTime().getTime());
        try{
            String response = connect().bookRoom(campusOfInterest, roomNumber, date, timeSlot, campusOfID, id);
            if (response.startsWith("Error")) {
                log.severe(builder.append(" FAILED").toString());
            } else {
                log.info(builder.append(" SUCCEEDED").toString());
            }
            return response;
        }catch(RemoteException | NotBoundException e){
            log.severe(builder.append(" FAILED - CONNECTION ERROR").toString());
            return "error";
        }
    }

    @Override
    public Map<String, Integer> getAvailableTimeSlot(Calendar date){
        StringBuilder builder = new StringBuilder();
        builder.append(fullID).append(" check the availability of rooms of all campus on ").append(date.getTime());
        try{

            Map<String, Integer> ret = connect().getAvailableTimeSlot(date);
            if (ret != null) {
                log.info(builder.append(" SUCCEEDED").toString());
            }
            return ret;
        }catch(RemoteException | NotBoundException e){
            log.severe(builder.append(" FAILED").toString());
            System.err.println(e.getMessage());
            e.printStackTrace();
        }
        return null;
    }

    public Map<String, Room> getAvailableTimeSlot(Calendar date, CampusName campusName){
        try{
            return connect().getAvailableTimeSlot(date, campusName);
        }catch(RemoteException | NotBoundException e){
            System.err.println(e.getMessage());
        }
        return null;
    }

    public boolean cancelBooking(String bookingID) {
        StringBuilder builder = new StringBuilder();
        builder.append(fullID).append(" cancel booking using booking ID : ").append(bookingID);
        try{
            boolean result = connect().cancelBooking(bookingID, campusName, id);
            if (result) {
                log.info(builder.append("-SUCCEEDED").toString());
            } else {
                log.severe(builder.append("-FAILED").toString());
            }
            return result;
        }catch(RemoteException | NotBoundException e){
            log.severe(builder.append("-FAILED-CONNECTION-ERROR").toString());
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void run() {

        try {
            test1();
            Thread.currentThread().sleep(3000);
            test2();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    private void test1() {

        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 9, 10, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar start = (Calendar) calendar.clone();
        start.add(Calendar.HOUR, 8);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MINUTE, 119);
        TimeSlot slot = new TimeSlot(start, end);

        System.err.println(campusName.abrev + "s" + id + "Booking same room test");
        System.out.println(this.bookRoom(CampusName.DORVAL, "10", calendar, slot, this.campusName, this.id));
    }

    private void test2() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2017, 9, (int) Thread.currentThread().getId(), 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        Calendar start = (Calendar) calendar.clone();
        start.add(Calendar.HOUR, 8);
        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MINUTE, 119);
        TimeSlot slot = new TimeSlot(start, end);

        String bookingID = this.bookRoom(CampusName.KIRKLAND, "1", calendar, slot, this.campusName, this.id);
        if (bookingID.startsWith("Error")) {
            System.out.println(bookingID);
        } else {
            boolean cancel = this.cancelBooking(bookingID);
            if (cancel) {
                //book again
                bookingID = this.bookRoom(CampusName.KIRKLAND, "1", calendar, slot, this.campusName, this.id);
                if (!bookingID.startsWith("Error")) System.out.println("Book again successful");
                else System.err.println(bookingID);
            } else {
                System.out.println("Test failed");
            }
        }
    }
}
