package cl.architeq.displayParking.service;


import cl.architeq.displayParking.entity.Display;
import cl.architeq.displayParking.repository.DisplayRepository;
import cl.architeq.displayParking.util.Util;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.DataOutputStream;
import java.net.Socket;
import java.util.List;


@Service
public class DisplayService {

    @Autowired
    DisplayRepository displayRepo;

    @Autowired
    private Environment environment;

    private static final Logger _logger = LoggerFactory.getLogger(GpioService.class);

    private Display _display;

    public void setDisplay(Display display) {
        this._display = display;
    }

    public Display getDisplay() {
        return this._display;
    }


    public Display fetchDisplay(String ipAddr, String tcpPort) {

        Display display = null;
        try {
            //Optional<Display> display = displayRepo.findById(1);
            List<Display> list = (List<Display>) displayRepo.findAll();
            if (list != null && list.size() > 0) {
                display = displayRepo.findByIpAddr(ipAddr);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        if (display == null) {


            // ip y puerto predeterminado ..
            ipAddr = environment.getProperty("direccionIP");
            tcpPort = environment.getProperty("puerto");

            // valores predeterminados ..
            String quota = environment.getProperty("estacionamientos");
            String free = environment.getProperty("estacionamientos");

            display = new Display("display01", quota, free, "0", ipAddr, tcpPort);
            displayRepo.save(display);
            display = displayRepo.findByIpAddr(ipAddr);
        }

        return display;

    }


    // restar ..
    public Display sub() {

        try {

            // restar unidad ..
            String free = this._display.getFree();
            Integer subt = Integer.parseInt(free);
            subt--;

            if (subt < 0 ) return this._display;

            free = subt.toString();
            this._display.setFree(free);

            Boolean bln = this.sendStream(free);

            if (bln) {
                this.displayRepo.save(this._display);
            }

            return this._display;

        } catch (Exception ex ){
            System.out.println(ex.getMessage());
            return this._display;
        }
    }


    // sumar ..
    public Display add() {

        try {

            // sumar unidad ..
            String free = this._display.getFree();
            Integer sum = Integer.parseInt(free);
            sum++;

            //controlar cantidad de cupos maxima .. !!
            Integer maximo = Integer.parseInt(this._display.getQuota());
            if (sum > maximo ) return this._display;

            free = sum.toString();
            this._display.setFree(free);

            Boolean bln = this.sendStream(free);

            if (bln) {
                this.displayRepo.save(this._display);
            }

            return this._display;

        } catch (Exception ex ){
            System.out.println(ex.getMessage());
            return this._display;
        }
    }


    public boolean setLCD() {

        try {

            Boolean bln = this.sendStream(this._display.getFree());
            if (bln){
                System.out.println("MARCADOR LCD -> " + this._display.getFree());
            } else {
                System.out.println("ERROR -> Falla conexion .. ");
            }

            return true;

        } catch (Exception ex ){
            System.out.println(ex.getMessage());
            return false;
        }

    }


    public Boolean sendStream(String str) {

        Boolean bln = false;

        try {

            if (!Util.isNumeric(str)) return bln;

            //boolean ping = Util.pingIpAddr(this._display.getIpAddr().trim() );
            //if (!ping) System.out.println("NO PASA PING !!" + this._display.getIpAddr().trim());
            //if (!ping) return bln;

            String ipAddress = this._display.getIpAddr().trim();
            Integer port = Integer.parseInt(this._display.getTcpPort().trim());

            str = String.format("%03d", Integer.parseInt(str));

            char[] array = str.toCharArray();

            byte[] b = new byte[4];

            //b[0] = 0x30; //0
            //b[1] = 0x31; //1
            //b[2] = 0x32; //2
            //b[3] = 0x0a;

            b[0] = (byte)array[0];  //48
            b[1] = (byte)array[1];  //49
            b[2] = (byte)array[2];  //50
            b[3] = 0x0a;            //10

            Socket socket = new Socket(ipAddress, port);
            DataOutputStream dataOutput = new DataOutputStream(socket.getOutputStream());

            dataOutput.write(b);
            Thread.sleep(50);

            dataOutput.close();
            socket.close();
            socket = null;

            bln = true;

        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

        return bln;

    }



    public void taskResetDisplay() {

        try {

            String quota = environment.getProperty("estacionamientos");
            this._display.setFree(quota);
            this.displayRepo.save(this._display);
            this.setLCD();
            _logger.info("tarea de reinicio de servicios, estacionamientos disponibles : " + this._display.getFree());

        } catch (Exception ex) {

            ex.printStackTrace();
            _logger.error("error al ejecutar tarea programada");

        }

    }

    /// /////////////////////////////////////////////////////////////////////////////////
}
