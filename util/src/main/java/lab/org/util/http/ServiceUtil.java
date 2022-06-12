package lab.org.util.http;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;

import lab.org.api.exceptions.NotFoundException;

@Component
public class ServiceUtil {

    private final String port;
    private String serviceAddress = null;

    @Autowired
    public ServiceUtil(@Value("${server.port}") String port) {
        this.port = port;
    }

    public String getServiceAddress() {
        if (serviceAddress == null) {
            serviceAddress = findMyHostname() + "/" + findMyIpAddress() + ":" + port;
        }
        return serviceAddress;
    }

    private String findMyIpAddress() {
        try {
            return InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            return "known IP address";
        }
    }

    private String findMyHostname() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (UnknownHostException e) {
            return "known host name";
        }
    }
}
