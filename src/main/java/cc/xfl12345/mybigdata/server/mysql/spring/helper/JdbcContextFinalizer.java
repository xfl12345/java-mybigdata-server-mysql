package cc.xfl12345.mybigdata.server.mysql.spring.helper;

import com.mysql.cj.jdbc.AbandonedConnectionCleanupThread;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationFailedEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * 这是一个用来执行正常退出任务的类
 */
@Slf4j
public class JdbcContextFinalizer implements ApplicationListener<ApplicationFailedEvent> {
    @Override
    public void onApplicationEvent(ApplicationFailedEvent event) {
        deregisterFromSpringFramework(event);
    }

    public static void deregisterDriver(Driver d) throws SQLException {
        String driverInstanceName = d.toString();
        DriverManager.deregisterDriver(d);
        log.info(String.format("Driver [%s] deregistered", driverInstanceName));
        // 触发 GC
        try {
            Thread.sleep(0);
        } catch (InterruptedException ignored) {
        }
    }

    /**
     * 这是一个用来结束JDBC驱动的函数，防止redeploy时被警告存在内存泄露风险
     */
    public static void deregister(ApplicationContext springAppContext) {
        ClassLoader contextClassLoader = springAppContext == null ?
            Thread.currentThread().getContextClassLoader() :
            springAppContext.getClassLoader();

        Enumeration<Driver> drivers = DriverManager.getDrivers();
        HashMap<Driver, Integer> retryCounts = new HashMap<>();
        if (drivers.hasMoreElements()) {
            Driver d = null;
            // 优先卸载 当前Context ClassLoader 加载的 JDBC 驱动
            // 跟 SpringBoot Servlet 学的一招
            while (drivers.hasMoreElements()) {
                try {
                    d = drivers.nextElement();
                    if (d.getClass().getClassLoader().equals(contextClassLoader)) {
                        deregisterDriver(d);
                    }
                } catch (SQLException ignored) {
                }
            }

            drivers = DriverManager.getDrivers();
            while (drivers.hasMoreElements()) {
                try {
                    d = drivers.nextElement();
                    deregisterDriver(d);
                } catch (SQLException ex) {
                    Integer count = retryCounts.get(d);
                    if (count == null) {
                        count = 0;
                    } else {
                        count += 1;
                    }

                    retryCounts.put(d, count);
                    log.error(String.format("Error deregistering driver [%s]. Retry %s.", d, count) + " Error msg: " + ex);

                    if (count >= 3) {
                        log.warn("Max retry reached. Stop deregistering.");
                        break;
                    }
                }
            }
        }

        if (drivers.hasMoreElements()) {
            log.warn("JDBC driver not clean.");
        } else {
            log.info("JDBC driver clean.");
        }

    }

    @EventListener
    public void onSpringContextClosed(ContextClosedEvent event) {
        deregisterFromSpringFramework(event);
    }

    protected void deregisterFromSpringFramework(ApplicationEvent applicationEvent) {
        AbandonedConnectionCleanupThread.checkedShutdown();
        ApplicationContext applicationContext = null;
        if (applicationEvent instanceof ContextClosedEvent event) {
            applicationContext = event.getApplicationContext();
        } else if (applicationEvent instanceof ApplicationFailedEvent event) {
            applicationContext = event.getApplicationContext();
        }

        if (applicationContext != null && applicationContext.getParent() == null) {
            deregister(applicationContext);
        }
    }
}
