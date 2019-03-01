package cl.architeq.displayParking.gpio;

import cl.architeq.displayParking.entity.Display;
import cl.architeq.displayParking.service.DisplayService;
import cl.architeq.displayParking.service.GpioService;
import cl.architeq.displayParking.util.Util;
import com.pi4j.io.gpio.*;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.LocalTime;


@Component
//@Scope("prototype")
public class GpioControl implements Runnable {

    private static final Logger _logger = LoggerFactory.getLogger(GpioControl.class);
    private volatile boolean _cancel;

    @Autowired
    private Environment environment;

    @Autowired
    private DisplayService _displayService;

    private Display _display;

    @Override
    public void run() {

        try {

            // ip y puerto predeterminados ..
            String ipAddr = environment.getProperty("direccionIP");
            String tcpPort = environment.getProperty("puerto");

            String timeReset = environment.getProperty("programacionReinicio");

            String initDelay = environment.getProperty("retardoInicio");
            String operationDelay = environment.getProperty("retardoSumaResta");
            int debouncePin = (Integer.parseInt(operationDelay));

            //int resetHour = LocalTime.now().getHour();
            //int resetMin = LocalTime.now().getMinute();

            String[] arr = timeReset.split(":");
            int resetHour = Integer.parseInt(arr[0]);
            int resetMin  = Integer.parseInt(arr[1]);


            System.out.println("resetHour -> " + resetHour + " - LocalTime.now().getHour() " + LocalTime.now().getHour());
            System.out.println("resetMin -> " + resetMin + " - LocalTime.now().getMinute() " + LocalTime.now().getMinute());


            _display = _displayService.fetchDisplay(ipAddr, tcpPort);
            //System.out.println("INIT SERVICE .. DISPLAY LCD -> " + _display);

            System.out.println("SERVICIO CONTROL ESTACIONAMIENTO - RETARDO INICIO -> " + initDelay + " ms .." );
            Util.sleep( Integer.parseInt(initDelay) / 1000 );

            _displayService.setDisplay(_display);
            boolean bln = false;
            while (!bln) {
                bln = _displayService.setLCD();
                if (!bln) {
                    String msg = "Falla conexion a tablero LCD - IP -> " + _display.getIpAddr();
                    System.out.println(msg);
                    _logger.error(msg);
                    Util.sleep(5);
                }
            }

            System.out.println("SERVICIO CONTROL ESTACIONAMIENTO - DISPLAY LCD ");
            System.out.println("DIRECCION IP TABLERO :" + _display.getIpAddr());
            System.out.println("PUERTO TCP TABLERO :" + _display.getTcpPort());
            System.out.println("TOTAL CUPOS ESTACIONAMIENTO :" + _display.getQuota());
            System.out.println("CUPOS DISPONIBLES :" + _display.getFree());


            String msg = "Servicio control estacionamiento, cantidad de cupos: " + _display.getQuota()
                       + ", cupos disponibles: " + _display.getFree();

            final GpioController gpio = GpioFactory.getInstance();

            final GpioPinDigitalInput pinIN = gpio.provisionDigitalInputPin(RaspiPin.GPIO_28, PinPullResistance.PULL_UP);
            final GpioPinDigitalInput pinOUT = gpio.provisionDigitalInputPin(RaspiPin.GPIO_29, PinPullResistance.PULL_UP);
            final GpioPinDigitalOutput pinRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "pinRelay", PinState.HIGH);  //OFF

            pinIN.setDebounce(debouncePin);
            pinOUT.setDebounce(debouncePin);

            pinIN.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {
                    if (event.getState().isHigh()) {

                        _display = _displayService.add();
                        System.out.println(LocalDateTime.now().format(Util.formatDateTime) + " PIN IN .. DISPLAY LCD -> " + _display.getFree());

                        if (Integer.parseInt(_display.getFree()) > 0) {
                            pinRelay.high(); // OFF ..
                        }

                    }
                }
            });

            pinOUT.addListener(new GpioPinListenerDigital() {
                @Override
                public void handleGpioPinDigitalStateChangeEvent( GpioPinDigitalStateChangeEvent event ) {

                    if (event.getState().isHigh()) {

                        _display = _displayService.sub();
                        System.out.println(LocalDateTime.now().format(Util.formatDateTime) + " PIN OUT .. DISPLAY LCD -> " + _display.getFree());

                        if (Integer.parseInt(_display.getFree()) <= 0) {
                            pinRelay.low();  // ON ..
                        }

                    }
                }
            });



            while (!this._cancel) {

                Util.sleep(1);

                if (resetHour == LocalTime.now().getHour() && resetMin == LocalTime.now().getMinute()) {
                    System.out.println("EJECUTAR RESET !! ");
                    _displayService.taskResetDisplay();
                    Util.sleep(60);
                }

            }

        } catch (Exception ex) {

            ex.printStackTrace();
            System.out.println("ERROR GPIO ..");

        }

    }
}
