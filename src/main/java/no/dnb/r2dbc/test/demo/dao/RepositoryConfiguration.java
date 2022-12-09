package no.dnb.r2dbc.test.demo.dao;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttribute;
import org.springframework.transaction.interceptor.TransactionAttributeSource;
import org.springframework.transaction.interceptor.TransactionInterceptor;

@Configuration
@ComponentScan
@RequiredArgsConstructor
public class RepositoryConfiguration {

    // The code below is a hack to get the transaction name to be less than 32 chars for MS SQL Server
    private final TransactionInterceptor interceptor;

    @PostConstruct
    public void setNameForMsSqlServer() {
        interceptor.setTransactionAttributeSource(transactionAttributeSource());
    }

    TransactionAttributeSource transactionAttributeSource() {
        return (method, targetClass) -> {
            DefaultTransactionAttribute attribute = new DefaultTransactionAttribute();
            attribute.setName(method.getName()); // Just method name, not fully-qualified class.method name.
            return attribute;
        };
    }

}
