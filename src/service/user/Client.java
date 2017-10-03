package service.user;

import domain.CampusName;

public abstract class Client{
    protected CampusName campusName;
    protected int id;

    Client(CampusName campusName, int id){
        this.campusName = campusName;
        this.id = id;
    }

}
