package domain;

public enum CampusName{
    DORVAL("Droval Campus", "DVL", 1101),
    KIRKLAND("Kirkland Campus", "KKL", 1101),
    WESTMOUNT("Westmount Campus", "WST", 1102);

    public String name;
    public String abrev;
    public int port;

    CampusName(String name, String abrev, int port){
        this.name = name;
        this.abrev = abrev;
        this.port = port;
    }

    public static CampusName getCampusName(String abrev){
        for(CampusName campusName : CampusName.values()){
            if(campusName.abrev.equals(abrev)) return campusName;
        }
        return null;
    }
}
