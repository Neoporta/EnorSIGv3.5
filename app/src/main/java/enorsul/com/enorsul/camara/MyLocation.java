package enorsul.com.enorsul.camara;

/**
 * Created by Eric on 14/03/18.
 */

public class MyLocation {
    private String lat;
    private String lon;

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
//        if(lat.contains("-")){
//            this.lat = lat.replace("-","S");
//        } else {
//            this.lat = "N" + lat;
//        }
        this.lat = lat;

    }

    public String getLon() {
        return lon;
    }

    public void setLon(String lon) {
//        if(lon.contains("-")){
//            this.lon = lon.replace("-", "");
//        } else {
//            this.lon = lon;
//        }

        this.lon = lon;
    }
}
