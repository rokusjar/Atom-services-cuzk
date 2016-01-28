package cz.cuzk.services.atomfeed.config;
//----------------------------------------------------------------------------------------------------------------------
/**
 * Slouží jako modelová třída pro čtení konfiguračního souboru pomocí knihovny Gson.
 * @author Jaromír Rokusek
 * @version 1.0
 * @since 2015-08-23
 */
//----------------------------------------------------------------------------------------------------------------------
public class DbConnection {
    private String service_name;
    private String port;
    private String host;
    private String username;
    private String password;

    public void setService_name(String service_name) {
        this.service_name = service_name;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getService_name() {
        return service_name;
    }

    public String getPort() {
        return port;
    }

    public String getHost() {
        return host;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getConnectionString(){
        String connString = "jdbc:oracle:thin:%s/%s@%s:%s/%s";
        return String.format(connString, username, password, host, port, service_name);
    }
}
