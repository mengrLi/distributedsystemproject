package service.corba;

import org.omg.CORBA.ORB;

public class Corba{
    public Corba(){
        ORB orb = ORB.init();
        orb.run();

    }

    public static void main(String[] args){
        Corba corba = new Corba();
    }
}
