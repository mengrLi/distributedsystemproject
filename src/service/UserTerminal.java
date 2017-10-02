package service;

import domain.CampusName;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class UserTerminal{
    public static Client client;

    public UserTerminal(){
        init();
    }

    private void init(){
        System.out.println("Enter your username:");
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(System.in))){
            String username = null;
            do{
                if(username != null) System.err.println("Invalid username");
                username = bufferedReader.readLine();
            }while(!connectToServer(username));

            String userType;
            userType = username.substring(3, 4).toUpperCase().equals("A")
                    ? "administrator"
                    : "student";

            System.out.println("Hello, " + userType + " " + username.substring(4));

            String todo;
            do{
                System.out.println("Please enter your command:");
                String input = bufferedReader.readLine();
                todo = parseInput(input);

                System.out.println(todo);

            }
            while(!todo.equals("exit"));
            System.out.println("You are logged out");

        }catch(IOException e){
            System.err.println(e.getMessage());
        }
    }

    private String parseInput(String input){

        return input;
    }

    private boolean connectToServer(String username){
        if(username.length() != 8) return false;
        String type = username.substring(3, 4).toUpperCase();
        String campus = username.substring(0, 3).toUpperCase();
        int id;
        try{
            id = Integer.parseInt(username.substring(4));
        }catch(NumberFormatException e){
            return false;
        }
        CampusName campusName = CampusName.getCampusName(campus);
        if(campusName == null) return false;

        if(type.equals("A")){
            client = new AdminClient(campusName, id);
            return true;
        }else if(type.equals("S")){
            client = new StudentClient(campusName, id);
            return true;
        }
        return false;
    }

}
