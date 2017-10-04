package service.user;

import domain.CampusName;
import domain.Format;
import domain.TimeSlot;
import lombok.extern.log4j.Log4j2;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

@Log4j2
public class UserTerminal{
    private UserInterface client;

    private boolean admin;

    private CampusName campusName;

    private int id;

    private boolean exit;

    public UserTerminal(){
        init();
    }

    private void init(){
        System.out.println("Enter your username:");
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in));
        try{
            String username = null;
            do{
                if(username != null) System.err.println("Invalid username");
                username = bufferedReader.readLine();
            }while(!connectToServer(username));

            System.out.println("Hello, " + (admin ? "administrator" : "student") + " " + id + "\n"
                    + "You are logging to " + campusName.name);

            exit = false;
            do{
                System.out.println("Please enter your command:");
                String input = bufferedReader.readLine();
                parseInput(input.toLowerCase(), bufferedReader);
            }while(!exit);
            System.out.println("You are logged out");

        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    private void parseInput(String input, BufferedReader reader) throws IOException{
        if(admin){
            switch(input){
                case "create":
                    createRoom(reader);
                    break;
                case "delete":
                    deleteRoom(reader);
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.err.println("Please enter \n\"create\" to create a room\n" +
                            "\"delete\" to delete a room\n" +
                            "\"exit\" to exit\n");
                    break;
            }
        }else{
            switch(input){
                case "book":
                    bookRoom(reader);
                    break;
                case "check":
                    checkAvailability(reader);
                    break;
                case "cancel":
                    cancelRoom(reader);
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.err.println("Please enter \"book\" to book a room\n" +
                            "\"cancel\" to cancel a booked room\n" +
                            "\"check\" to check the availability of a room\n" +
                            "\"exit\" to exit\n");
                    break;
            }
        }
    }

    private void createRoom(BufferedReader reader) throws IOException{
        boolean complete = false;
        while(!complete){
            //choose date
            Calendar calendar = inputDate(reader);
            //choose room
            println("Enter the room number");
            String roomNumber = reader.readLine();

            //input time
            boolean addAnother = true;
            List<TimeSlot> list = new LinkedList<>();
            while(addAnother){
                println("Enter time slot in the format of hh:mm-hh:mm ");
                addAnother = setTimeSlot(reader, list, calendar);
            }
            //ask completion
            String input = null;
            do{
                if(input != null) printlnErr("Invalid input : " + input);
                println("Create room " + roomNumber + " on " + Format.formatDate(calendar));

                for(TimeSlot slot : list)
                    println("From : " + Format.formatTime(slot.getStartTime()) +
                            "To : " + Format.formatTime(slot.getEndTime()));

                println("Complete input, input \"y\" to process or \"n\" to start over\n");
                input = reader.readLine().toLowerCase();

                if(input.equals("y")){
                    println("Data send to " + campusName.name + " server...");
                    complete = true;
                    /**
                     * Connect to server
                     */
                    client.createRoom(roomNumber, calendar, list);
                }else println("Data not sent, restart ...");
            }while(!input.equals("y") && !input.equals("n"));
        }

    }

    private boolean setTimeSlot(BufferedReader reader, List<TimeSlot> list, Calendar calendar) throws IOException{
        String input;
        boolean valid = false;
        while(!valid){
            println("Enter time slot in format of HH:MM-HH:MM");
            input = reader.readLine();
            valid = validTimeSlotInput(input, list, calendar);
        }

        input = null;
        do{
            if(input != null) printlnErr("Invalid input");
            println("Add another time slot? (y/n)");
            input = reader.readLine().toLowerCase();
            if(input.equals("y")) return true;
            if(input.equals("n")) return false;
        }while(!input.equals("y") || !input.equals("n"));

        return true;
    }

    private boolean validTimeSlotInput(String input, List<TimeSlot> list, Calendar calendar){
        String[] periodDelim = input.split("-");
        if(periodDelim.length != 2){
            printlnErr("Invalid time slot input " + input);
            return false;
        }

        String[] time1 = periodDelim[0].split(":");
        String[] time2 = periodDelim[1].split(":");
        if(time1.length != 2 || time2.length != 2){
            printlnErr("Invalid time format : " + time1[0] + ":" + time1[1] + "-" + time2[0] + ":" + time2[1]);
            return false;
        }
        int h1, h2, m1, m2;
        try{
            h1 = Integer.parseInt(time1[0]);
            m1 = Integer.parseInt(time1[1]);
            h2 = Integer.parseInt(time2[0]);
            m2 = Integer.parseInt(time2[1]);

            if(h1 < 0 || h1 > 23 || h2 < 0 || h2 > 23 || m1 < 0 || m1 > 59 || m2 < 0 || m2 > 59){
                printlnErr("invalid time range");
                return false;
            }else{
                Calendar calendar1 = (Calendar) calendar.clone();
                calendar1.set(Calendar.HOUR_OF_DAY, h1);
                calendar1.set(Calendar.MINUTE, m1);

                Calendar calendar2 = (Calendar) calendar.clone();
                calendar2.set(Calendar.HOUR_OF_DAY, h2);
                calendar2.set(Calendar.MINUTE, m2);

                println("Input time :\n" + calendar1.getTime().toString() + "\n" + calendar2.getTime().toString());

                list.add(new TimeSlot(calendar1, calendar2));
                return true;
            }
        }catch(NumberFormatException e){
            printlnErr("Invalid time format : " + time1[0] + ":" + time1[1] + "-" + time2[0] + ":" + time2[1]);
            return false;
        }

    }

    private void deleteRoom(BufferedReader reader) throws IOException{
        Calendar calendar = inputDate(reader);
    }


    private void cancelRoom(BufferedReader reader) throws IOException{
        Calendar calendar = inputDate(reader);
    }

    private void checkAvailability(BufferedReader reader) throws IOException{
        Calendar calendar = inputDate(reader);

    }

    private void bookRoom(BufferedReader reader) throws IOException{
        Calendar calendar = inputDate(reader);

    }

    private boolean connectToServer(String username){
        if(username.length() != 8) return false;
        String type = username.substring(3, 4).toUpperCase();
        String campus = username.substring(0, 3).toUpperCase();
        try{
            id = Integer.parseInt(username.substring(4));
        }catch(NumberFormatException e){
            return false;
        }
        campusName = CampusName.getCampusName(campus);
        if(campusName == null) return false;

        if(type.equals("A")){
            client = new AdminClient(campusName, id);
            admin = true;
            return true;
        }else if(type.equals("S")){
            client = new StudentClient(campusName, id);
            admin = false;
            return true;
        }
        return false;
    }

    private Calendar inputDate(BufferedReader reader) throws IOException{
        String dateString;
        String[] delim;
        boolean validDate;
        Calendar calendar = Calendar.getInstance();


        do{
            println("Please enter the date (yyyy/mm/dd) :");
            dateString = reader.readLine();
            delim = dateString.split("/");
            validDate = parseDate(delim, calendar);
        }while(!validDate);
        return calendar;
    }

    private boolean parseDate(String[] dateDelim, Calendar calendar){
        if(dateDelim.length != 3){
            printlnErr("Invalid date format");
            return false;
        }
        try{
            int year, month, day;
            year = Integer.parseInt(dateDelim[0]);
            month = Integer.parseInt(dateDelim[1]) - 1;
            day = Integer.parseInt(dateDelim[2]);
            if(month < 0 || month > 12 || day > 31 || day < 0){
                printlnErr("Invalid date input : " + month + " " + day);
                return false;
            }else{
                if(month == 4 || month == 6 || month == 9 || month == 11){
                    if(day > 30){
                        printlnErr("Invalid day input " + day);
                        return false;
                    }
                }else if(month == 2){
                    if(year % 4 == 0 && day > 29){
                        printlnErr("Invalid day input for even February " + day);
                        return false;
                    }else if(year % 4 != 0 && day > 28){
                        printlnErr("Invalid day input for odd February " + day);
                        return false;
                    }
                }
            }
            calendar.set(year, month, day, 0, 0, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            return true;
        }catch(NumberFormatException e){
            printlnErr("Invalid date format");
            return false;
        }
    }


    private void println(String s){
        System.out.println(s);
    }

    private void printlnErr(String s){
        System.err.println(s);
    }

    private void print(String s){
        System.out.print(s);
    }

    private void printErr(String s){
        System.err.print(s);
    }
}

