package domain;

public enum CampusName{
    DORVAL("Droval Campus", "DVL", "dorval", 1099, 5858),
    KIRKLAND("Kirkland Campus", "KKL", "kirkland", 1099, 5859),
    WESTMOUNT("Westmount Campus", "WST", "westmount", 1099, 5860);

    public String name;
    public String abrev;
    public int port;
    public String serverName;
    public int inPort;

    CampusName(String name, String abrev, String serverName, int port, int inPort){
        this.name = name;
        this.abrev = abrev;
        this.port = port;
        this.serverName = serverName;
        this.inPort = inPort;
    }

    public static CampusName getCampusName(String abrev){
        for(CampusName campusName : CampusName.values()){
            if(campusName.abrev.equals(abrev)) return campusName;
        }
        return null;
    }
}
