package domain;

public enum Campus{
    DORVAL("Droval Campus", "DVL", "dorval", 1099, 5858),
    KIRKLAND("Kirkland Campus", "KKL", "kirkland", 1099, 5859),
    WESTMOUNT("Westmount Campus", "WST", "westmount", 1099, 5860);

    public String name;
    public String abrev;
    public int port;
    public String serverName;
    public int udpPort;

    Campus(String name, String abrev, String serverName, int port, int udpPort){
        this.name = name;
        this.abrev = abrev;
        this.port = port;
        this.serverName = serverName;
        this.udpPort = udpPort;
    }

    public static Campus getCampusName(String abrev){
        for(Campus campus : Campus.values()){
            if(campus.abrev.equals(abrev)) return campus;
        }
        return null;
    }
    public static int determinePort(String campusOfInterestAbrev) {
        Campus ret = getCampusName(campusOfInterestAbrev);
        if (ret != null) return ret.udpPort;
        System.err.println("Campus invalid");
        return -1;
    }
}
