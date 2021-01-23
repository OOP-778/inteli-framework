import com.oop.inteliframework.config.configuration.PlainConfig;

import java.io.File;

public class Test {
    public static void main(String[] args) {
        File file = new File("/run/media/brian/Misc/Work/inteli-framework/config/src/main/resources/testComments.yml");
        PlainConfig nodes = new PlainConfig(file);
    }
}
