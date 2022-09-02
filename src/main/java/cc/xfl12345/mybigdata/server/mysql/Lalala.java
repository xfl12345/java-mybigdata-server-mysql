package cc.xfl12345.mybigdata.server.mysql;

import lombok.Getter;
import lombok.Setter;

public class Lalala {
    @Getter
    @Setter
    protected String helloMessage = "Hello, world!";

    public void sayHello() {
        System.out.println(helloMessage);
    }
}
