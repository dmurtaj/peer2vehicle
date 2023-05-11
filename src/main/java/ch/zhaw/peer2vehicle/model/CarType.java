package ch.zhaw.peer2vehicle.model;

public enum CarType {
    ELECTRIC, HYBRID, DIESEL, GAS;

    /*
    public static boolean isValidCarType(String type) {
        for (CarType carType : CarType.values()) {
            if (carType.name().equalsIgnoreCase(type)) {
                return true;
            }
        }
        return false;
    }
    */
}
