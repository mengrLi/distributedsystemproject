package CampusServerCorba;


/**
 * CampusServerCorba/CampusServerInterfacePOA.java .
 * Generated by the IDL-to-Java compiler (portable), version "3.2"
 * from CampusServerCorba.idl
 * Saturday, November 4, 2017 12:14:30 o'clock PM EDT
 */

public abstract class CampusServerInterfacePOA extends org.omg.PortableServer.Servant
        implements CampusServerCorba.CampusServerInterfaceOperations, org.omg.CORBA.portable.InvokeHandler {

    // Constructors

    private static java.util.Hashtable _methods = new java.util.Hashtable();

    static {
        _methods.put("getUdpPort", new java.lang.Integer(0));
        _methods.put("createRoom", new java.lang.Integer(1));
        _methods.put("deleteRoom", new java.lang.Integer(2));
        _methods.put("bookRoom", new java.lang.Integer(3));
        _methods.put("switchRoom", new java.lang.Integer(4));
        _methods.put("getAvailableTimeSlotCount", new java.lang.Integer(5));
        _methods.put("getAvailableTimeSlotByRoom", new java.lang.Integer(6));
        _methods.put("cancelBooking", new java.lang.Integer(7));
        _methods.put("checkAdminId", new java.lang.Integer(8));
    }

    public org.omg.CORBA.portable.OutputStream _invoke(String $method,
                                                       org.omg.CORBA.portable.InputStream in,
                                                       org.omg.CORBA.portable.ResponseHandler $rh) {
        org.omg.CORBA.portable.OutputStream out = null;
        java.lang.Integer __method = (java.lang.Integer) _methods.get($method);
        if (__method == null)
            throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);

        switch (__method.intValue()) {
            case 0:  // CampusServerCorba/CampusServerInterface/getUdpPort
            {
                int $result = 0;
                $result = this.getUdpPort();
                out = $rh.createReply();
                out.write_long($result);
                break;
            }

            case 1:  // CampusServerCorba/CampusServerInterface/createRoom
            {
                String json = in.read_string();
                String $result = null;
                $result = this.createRoom(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 2:  // CampusServerCorba/CampusServerInterface/deleteRoom
            {
                String json = in.read_string();
                String $result = null;
                $result = this.deleteRoom(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 3:  // CampusServerCorba/CampusServerInterface/bookRoom
            {
                String json = in.read_string();
                String $result = null;
                $result = this.bookRoom(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 4:  // CampusServerCorba/CampusServerInterface/switchRoom
            {
                String json = in.read_string();
                String $result = null;
                $result = this.switchRoom(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 5:  // CampusServerCorba/CampusServerInterface/getAvailableTimeSlotCount
            {
                String json = in.read_string();
                String $result = null;
                $result = this.getAvailableTimeSlotCount(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 6:  // CampusServerCorba/CampusServerInterface/getAvailableTimeSlotByRoom
            {
                String json = in.read_string();
                String $result = null;
                $result = this.getAvailableTimeSlotByRoom(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 7:  // CampusServerCorba/CampusServerInterface/cancelBooking
            {
                String json = in.read_string();
                String $result = null;
                $result = this.cancelBooking(json);
                out = $rh.createReply();
                out.write_string($result);
                break;
            }

            case 8:  // CampusServerCorba/CampusServerInterface/checkAdminId
            {
                String json = in.read_string();
                boolean $result = false;
                $result = this.checkAdminId(json);
                out = $rh.createReply();
                out.write_boolean($result);
                break;
            }

            default:
                throw new org.omg.CORBA.BAD_OPERATION(0, org.omg.CORBA.CompletionStatus.COMPLETED_MAYBE);
        }

        return out;
    } // _invoke

    // Type-specific CORBA::Object operations
    private static String[] __ids = {
            "IDL:CampusServerCorba/CampusServerInterface:1.0"};

    public String[] _all_interfaces(org.omg.PortableServer.POA poa, byte[] objectId) {
        return __ids.clone();
    }

    public CampusServerInterface _this() {
        return CampusServerInterfaceHelper.narrow(
                super._this_object());
    }

    public CampusServerInterface _this(org.omg.CORBA.ORB orb) {
        return CampusServerInterfaceHelper.narrow(
                super._this_object(orb));
    }


} // class CampusServerInterfacePOA