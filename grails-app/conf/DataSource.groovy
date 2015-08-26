dataSource {
    pooled = true
    driverClassName = "org.postgresql.Driver"
    dialect = org.hibernate.dialect.PostgreSQLDialect
}
hibernate {
    cache.use_second_level_cache = false
    cache.use_query_cache = false
    cache.provider_class = 'net.sf.ehcache.hibernate.EhCacheProvider'
}
// environment specific settings
environments {
    development {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://10.0.0.2:5432/sadweb_brre3"
//            url = "jdbc:postgresql://10.0.0.2:5432/sadweb_prdc"
//            url = "jdbc:postgresql://127.0.0.1:5432/sadweb_prdc"
//            url = "jdbc:postgresql://10.0.0.2:5432/happy10"
            username = "postgres"
            password = "postgres"

/*
            properties {
                jmxEnabled = true
                initialSize = 10
                maxActive = 100
                minIdle = 10
                maxIdle = 100
                maxWait = 30000         // 10 segundos
//                maxAge = 30 * 60000     // 0 por defecto: No se chequea las conexiones
                timeBetweenEvictionRunsMillis = 30000   //1800000
                minEvictableIdleTimeMillis = 60000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 30000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
*/
        }
    }
    test {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://10.0.0.3:5432/happy"
            username = "postgres"
            password = "postgres"
        }
    }
    production {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://127.0.0.1:5432/happy"
            username = "postgres"
            password = "janus"
        }

/*
        properties {
            jmxEnabled = true
            initialSize = 10
            maxActive = 100
            minIdle = 10
            maxIdle = 100
            maxWait = 30000         // 10 segundos
//                maxAge = 30 * 60000     // 0 por defecto: No se chequea las conexiones
            timeBetweenEvictionRunsMillis = 30000   //1800000
            minEvictableIdleTimeMillis = 60000
            validationQuery = "SELECT 1"
            validationQueryTimeout = 3
            validationInterval = 30000
            testOnBorrow = true
            testWhileIdle = true
            testOnReturn = false
            jdbcInterceptors = "ConnectionState;StatementCache(max=200)"
            defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
        }
*/
    }


    pruebasvt {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://127.0.0.1:5432/pruebasvt2"
            username = "postgres"
            password = "janus"
        }
    }
    produccionSAD {
        dataSource {
            dbCreate = "update"
            url = "jdbc:postgresql://127.0.0.1:5432/sadweb"
            username = "postgres"
            password = "janus"

/*
            properties {
                jmxEnabled = true
                initialSize = 10
                maxActive = 100
                minIdle = 10
                maxIdle = 25
                maxWait = 60000         // 10 segundos
                maxAge = 30 * 60000     // 0 por defecto: No se chequea las conexiones
                timeBetweenEvictionRunsMillis = 60000   //1800000
                minEvictableIdleTimeMillis = 120000
                validationQuery = "SELECT 1"
                validationQueryTimeout = 3
                validationInterval = 60000
                testOnBorrow = true
                testWhileIdle = true
                testOnReturn = false
                jdbcInterceptors = ConnectionState
                defaultTransactionIsolation = java.sql.Connection.TRANSACTION_READ_COMMITTED
            }
*/
        }
    }

}


                                                                                      i