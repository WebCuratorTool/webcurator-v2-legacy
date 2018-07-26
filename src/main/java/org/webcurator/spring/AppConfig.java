package org.webcurator.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScans;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.hibernate5.HibernateTemplate;
import org.springframework.orm.hibernate5.HibernateTransactionManager;
import org.springframework.orm.hibernate5.LocalSessionFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import javax.sql.DataSource;

@Configuration
@EnableTransactionManagement
@ComponentScans(value = {@ComponentScan("org.webcurator.domain"),
                         @ComponentScan("org.webcurator.core.permissionmapping")})
public class AppConfig implements TransactionManagementConfigurer {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private LocalSessionFactoryBean sessionFactory;

    @Bean
    public HibernateTransactionManager getTransactionManager() {
        HibernateTransactionManager transactionManager = new HibernateTransactionManager();
        transactionManager.setSessionFactory(sessionFactory.getObject());
        return transactionManager;
    }

    @Bean
    public HibernateTemplate getTransactionTemplate() {
        HibernateTemplate hibernateTemplate = new HibernateTemplate(sessionFactory.getObject());
        return hibernateTemplate;
    }

    @Override
    public PlatformTransactionManager annotationDrivenTransactionManager() {
        return getTransactionManager();
    }
}
