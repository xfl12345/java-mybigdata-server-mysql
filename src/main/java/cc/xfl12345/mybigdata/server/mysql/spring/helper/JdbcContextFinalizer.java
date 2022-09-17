package cc.xfl12345.mybigdata.server.mysql.spring.helper;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;

/**
 * 这是一个用来执行正常退出任务的类
 */
@Slf4j
public class JdbcContextFinalizer implements ApplicationListener<ContextClosedEvent> {
    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        AbandonedConnectionCleanupThread.checkedShutdown();
        ApplicationContext applicationContext = event.getApplicationContext();
        if (applicationContext.getParent() == null) {
            DriverHelper.deregister(applicationContext);
        }
    }
}
