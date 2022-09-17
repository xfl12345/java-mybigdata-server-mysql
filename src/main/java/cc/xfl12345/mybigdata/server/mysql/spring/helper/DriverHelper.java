package cc.xfl12345.mybigdata.server.mysql.spring.helper;

import com.alibaba.druid.pool.DruidDataSource;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.datasource.pooled.PooledDataSource;
import org.apache.ibatis.datasource.unpooled.UnpooledDataSource;
import org.springframework.context.ApplicationContext;

import javax.sql.DataSource;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.List;
import java.util.TreeSet;

@Slf4j
public class DriverHelper {
    /**
     * 这是一个用来结束JDBC驱动的函数，防止redeploy时被警告存在内存泄露风险
     */
    public static void deregister(ApplicationContext springAppContext) {
        Enumeration<Driver> drivers = DriverManager.getDrivers();
        TreeSet<String> dataSourceBeanNames = new TreeSet<>();
        if (drivers.hasMoreElements()) {
            if (springAppContext != null) {
                dataSourceBeanNames = new TreeSet<>(List.of(springAppContext.getBeanNamesForType(DataSource.class)));
            }
            Driver d = null;
            DataSource dataSource;
            String driverInstanceName;
            int dataSourceBeanNamesCount = dataSourceBeanNames.size();
            boolean isOk2DeregisterDriverNormally = springAppContext == null || dataSourceBeanNamesCount == 0;
            while (drivers.hasMoreElements()) {
                try {
                    d = drivers.nextElement();
                    //优先卸载JDBC驱动
                    if (isOk2DeregisterDriverNormally) {
                        driverInstanceName = d.toString();
                        DriverManager.deregisterDriver(d);
                        log.info(String.format("Driver %s deregistered", driverInstanceName));
                    } else {
                        // 防止同一个驱动被反复卸载 （一个驱动可能被多个 bean 引用）
                        boolean isHitAsLeaseOnce = false;
                        @SuppressWarnings("unchecked")
                        TreeSet<String> tmpCopy = (TreeSet<String>) dataSourceBeanNames.clone();
                        for (String beanName : tmpCopy) {
                            dataSource = springAppContext.getBean(beanName, DataSource.class);
                            String dataSourceDriverName = "";
                            if (dataSource instanceof PooledDataSource mybatisDataSource) {
                                dataSourceDriverName = mybatisDataSource.getDriver();
                            } else if (dataSource instanceof UnpooledDataSource mybatisDataSource) {
                                dataSourceDriverName = mybatisDataSource.getDriver();
                            } else if (dataSource instanceof DruidDataSource druidDataSource) {
                                dataSourceDriverName = druidDataSource.getDriverClassName();
                            }
                            if (d.getClass().getCanonicalName().equals(dataSourceDriverName)) {
                                driverInstanceName = d.toString();
                                // ((BeanDefinitionRegistry) springAppContext.getAutowireCapableBeanFactory()).removeBeanDefinition(beanName);
                                // log.info(String.format("Bean[%s] has been removed definition from Spring context.", beanName));
                                if (!isHitAsLeaseOnce) {
                                    isHitAsLeaseOnce = true;
                                    DriverManager.deregisterDriver(d);
                                    log.info(String.format("Driver %s deregistered", driverInstanceName));
                                }
                                dataSourceBeanNames.remove(beanName);
                                dataSourceBeanNamesCount--;
                            }
                        }
                        isOk2DeregisterDriverNormally = dataSourceBeanNamesCount == 0;
                        try {
                            //noinspection BusyWait
                            Thread.sleep(0);
                        } catch (InterruptedException ignored) {
                        }
                        // Runtime.getRuntime().gc();
                    }
                } catch (SQLException ex) {
                    log.error(String.format("Error deregistering driver %s", d) + ":" + ex);
                }
                //像队列一样遍历列表。循环到队列为空的时候才退出
                if (!drivers.hasMoreElements()) {
                    // 如果出现了不认识的 bean ，将会导致 bean 数量无法清零。
                    // 为了防止陷入死循环，允许直接使用常规（暴力）手段卸载
                    isOk2DeregisterDriverNormally = true;
                    drivers = DriverManager.getDrivers();
                }
            }
            log.info("JDBC driver clean.");
        }
    }
}
