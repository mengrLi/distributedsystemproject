package domain;

public enum CampusName{
    DORVAL("Droval Campus", "DVL", "dorval", 5858),
    KIRKLAND("Kirkland Campus", "KKL", "kirkland", 5859),
    WESTMOUNT("Westmount Campus", "WST", "westmount", 5860);

    public String name;
    public String abrev;
    public int port;
    public String serverName;

    CampusName(String name, String abrev, String serverName, int port){
        this.name = name;
        this.abrev = abrev;
        this.port = port;
        this.serverName = serverName;
    }

    public static CampusName getCampusName(String abrev){
        for(CampusName campusName : CampusName.values()){
            if(campusName.abrev.equals(abrev)) return campusName;
        }
        return null;
    }
}
