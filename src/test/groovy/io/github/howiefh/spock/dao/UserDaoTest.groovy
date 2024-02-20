package io.github.howiefh.spock.dao

import io.github.howiefh.spock.SpockSpringTest
import io.github.howiefh.spock.domain.UserStatistics
import org.springframework.beans.factory.annotation.Autowired
import spock.lang.*

/**
 *
 * @author fenghao
 * @version 1.0
 * @since 2024/2/19
 */
@SpockSpringTest
class UserDaoTest extends Specification {
    @Autowired
    UserDao userDao

    def "test statistics use databaseIdProvider"() {
        when:
        UserStatistics userStatistics = userDao.statistics()

        then:
        userStatistics.male == 1
        userStatistics.female == 0
    }
}
