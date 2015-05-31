package tcpassresetplugin;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import static org.apache.commons.lang.StringUtils.isEmpty;

public class SmtpConfig {

    private Integer port;
    private String host;
    private String username;
    private String password;
    private String fromAddress;
    private Boolean tls;

    public Integer getPort() {
        return port;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFromAddress() {
        return fromAddress;
    }

    public void setFromAddress(String fromAddress) {
        this.fromAddress = fromAddress;
    }

    public Boolean isTls() {
        return tls;
    }

    public void setTls(Boolean tls) {
        this.tls = tls;
    }

    @Override
    public String toString() {
        return "SmtpConfig{" +
                "port=" + port +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", tls=" + tls +
                '}';
    }


    public String toSafeString() {
        return "SmtpConfig{" +
                "port=" + port +
                ", host='" + host + '\'' +
                ", username='" + username + '\'' +
                ", password='" + (isEmpty(password) ? "" : "**********") + '\'' +
                ", fromAddress='" + fromAddress + '\'' +
                ", tls=" + tls +
                '}';
    }

    public Collection<String> getErrors() {
        Set<String> errors = Sets.newHashSet();
        if (isEmpty(host)) {
            errors.add("Host is not set");
        }
        if (port == null || port == 0) {
            errors.add("Port is not set");
        }
        return errors;
    }

    public Collection<String> getWarnings() {
        Set<String> warnings = Sets.newHashSet();
        if (isEmpty(username)) {
            warnings.add("Username is not set");
        }
        if (isEmpty(password)) {
            warnings.add("Password is not set");
        }
        if (isEmpty(fromAddress)) {
            warnings.add("From address is not set");
        }
        return warnings;
    }


}
