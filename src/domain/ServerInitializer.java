package domain;

public class ServerInitializer{

    public ServerInitializer(){
        init();
    }

    private void init(){
        for(CampusName name : CampusName.values()){
            Campus campus = new Campus(name);
            campus.start();
        }
    }
}
