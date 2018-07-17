package org.webcurator.spring;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.jndi.JndiObjectFactoryBean;
import org.springframework.jndi.JndiTemplate;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;
import org.springframework.transaction.support.TransactionTemplate;
import org.webcurator.core.permissionmapping.Mapping;
import org.webcurator.core.permissionmapping.MappingView;
import org.webcurator.domain.model.auth.Agency;
import org.webcurator.domain.model.auth.RolePrivilege;
import org.webcurator.domain.model.auth.User;
import org.webcurator.domain.model.core.*;

import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.transaction.TransactionManager;
import java.util.Properties;
import static org.hibernate.cfg.Environment.*;

@Configuration
@PropertySource("classpath:hibernate.properties")
@EnableTransactionManagement
@ComponentScans(value = {@ComponentScan("org.webcurator.domain")})
public class AppConfig implements TransactionManagementConfigurer {
    private Log log = LogFactory.getLog(AppConfig.class);

    @Autowired
    private Environment env;

    @Bean
    public DataSource getDataSource() throws NamingException {
        return (DataSource) new JndiTemplate().lookup("java:comp/env/jdbc/wctDatasource");
    }

    @Bean
    public LocalSessionFactoryBean getSessionFactory() {
        LocalSessionFactoryBean factoryBean = new LocalSessionFactoryBean();
        try {
            factoryBean.setDataSource(getDataSource());
        } catch (NamingException ne) {
            log.error(ne);
        }

        Properties props = new Properties();

        // Setting Hibernate properties
        props.put(SHOW_SQL, env.getProperty("hibernate.show_sql"));
        props.put(HBM2DDL_AUTO, env.getProperty("hibernate.hbm2ddl.auto"));
        props.put(USE_NEW_ID_GENERATOR_MAPPINGS, env.getProperty("hibernate.id.new_generator_mappings"));

        // Setting C3P0 properties
        props.put(C3P0_MIN_SIZE, env.getProperty("hibernate.c3p0.min_size"));
        props.put(C3P0_MAX_SIZE, env.getProperty("hibernate.c3p0.max_size"));
        props.put(C3P0_ACQUIRE_INCREMENT, env.getProperty("hibernate.c3p0.acquire_increment"));
        props.put(C3P0_TIMEOUT, env.getProperty("hibernate.c3p0.timeout"));
        props.put(C3P0_MAX_STATEMENTS, env.getProperty("hibernate.c3p0.max_statements"));

        factoryBean.setHibernateProperties(props);
        factoryBean.setAnnotatedClasses(org.webcurator.domain.model.auth.Role.class, AuthorisingAgent.class,
                MappingView.class, RolePrivilege.class, User.class, PermissionExclusion.class, Site.class,
                HibernateTest.class, Mapping.class, Permission.class, Agency.class, UrlPattern.class);

        return factoryBean;
    }

    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(getSessionFactory().getObject());
        return transactionManager;
    }

    @Bean
    public HibernateTemplate getTransactionTemplate() {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(getSessionFactory().getObject());
        return hibernateTemplate;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return getTransactionManager();
    }
}
