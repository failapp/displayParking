package cl.architeq.displayParking.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "display")
public class Display {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String quota;
    private String free;
    private String reserve;

    @Column(name = "ip_addr")
    private String ipAddr;

    @Column(name = "tcp_port")
    private String tcpPort;


    public Display() {
        //
    }

    public Display(String name, String quota, String free, String reserve, String ipAddress, String tcpPort) {
        this.name = name;
        this.quota = quota;
        this.free = free;
        this.reserve = reserve;
        this.ipAddr = ipAddress;
        this.tcpPort = tcpPort;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuota() {
        return quota;
    }

    public void setQuota(String quota) {
        this.quota = quota;
    }

    public String getFree() {
        return free;
    }

    public void setFree(String free) {
        this.free = free;
    }

    public String getReserve() {
        return reserve;
    }

    public void setReserve(String reserve) {
        this.reserve = reserve;
    }

    public String getIpAddr() {
        return ipAddr;
    }

    public void setIpAddr(String ipAddress) {
        this.ipAddr = ipAddress;
    }

    public String getTcpPort() {
        return tcpPort;
    }

    public void setTcpPort(String tcpPort) {
        this.tcpPort = tcpPort;
    }


    @Override
    public String toString() {
        return "Display{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", quota='" + quota + '\'' +
                ", free='" + free + '\'' +
                ", reserve='" + reserve + '\'' +
                ", ipAddress='" + ipAddr + '\'' +
                ", tcpPort='" + tcpPort + '\'' +
                '}';
    }
}
