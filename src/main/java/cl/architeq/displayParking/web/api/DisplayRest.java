package cl.architeq.displayParking.web.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;


@RestController
@RequestMapping("api")
public class DisplayRest {

    @GetMapping("/hi/{name}")
    public String hi(@PathVariable String name) {
        return "Hi " + name + " xD !!!! ";
    }

    public static GpioPinDigitalOutput pinRelay;

    @GetMapping("/relay")
    public String led() {

        try {

            GpioController gpio = GpioFactory.getInstance();
            pinRelay = gpio.provisionDigitalOutputPin(RaspiPin.GPIO_04, "Rele", PinState.HIGH); // down ..

            //pinRelay.toggle();
            //pinRelay.low();

        } catch (Exception ex) {
            System.out.println("error en relay !!!!!");
            System.out.println(ex.getMessage());
        }

        if (pinRelay != null) {

            pinRelay.toggle();

        }


        return "control relay !!";

    }


}
