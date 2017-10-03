package service.user;

import domain.CampusName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;

public class UserTerminal{
    private Client client;

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
                    + "You are logging to " + campusName.name + "campus");

            exit = false;
            do{
                System.out.println("Please enter your command:");
                String input = bufferedReader.readLine();
                parseInput(input.toLowerCase());

            }while(!exit);
            System.out.println("You are logged out");

        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    private void parseInput(String input){
        if(admin){
            switch(input){
                case "create":
                    createRoom();
                    break;
                case "delete":
                    deleteRoom();
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.err.println("Please enter \n\"create\" to create a room\n" +
                            "\"delete\" to delete a room\n");
                    break;
            }
        }else{
            switch(input){
                case "book":
                    bookRoom();
                    break;
                case "check":
                    checkAvailability();
                    break;
                case "cancel":
                    cancelRoom();
                    break;
                case "exit":
                    exit = true;
                    break;
                default:
                    System.err.println("Please enter \"book\" to book a room\n" +
                            "\"cancel\" to cancel a booked room\n" +
                            "\"check\" to check the availability of a room\n");
                    break;
            }
        }
    }


    private void deleteRoom(){

    }

    private void createRoom(){

    }

    private void cancelRoom(){
    }

    private void checkAvailability(){

    }

    private void bookRoom(){


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

    private Date inputDate(String dateString){
        String[] delim = dateString.split("/");
        Date date = new Date();

        return date;
    }


}
