package org.webcurator.domain.model.core;

import javax.persistence.*;

/**
 * Hibernate Test
 */
@Entity
@Table(name = "HIBERNATE_TEST")
@NamedQueries({@NamedQuery(name = "org.webcurator.domain.model.core.HibernateTest.getAll", query = "SELECT ht FROM HibernateTest ht")})
public class HibernateTest {

    /** Query to retrieve all hibernate test data. */
    public static final String QRY_GET_ALL = "org.webcurator.domain.model.core.HibernateTest.getAll";

    @Id
    @Column(name = "ID")
    private Integer id;
    @Column(name = "COLUMN1", length = 100)
    private String column1;
    @Column(name = "COLUMN2", length = 100)
    private String column2;

    /**
     * gets the id, this is its primary key
     * @return the id
     */
    public Integer getId() {
        return id;
    }

    /**
     * Set the database ID.
     * @param id The database OID.
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * gets column 1
     * @return column 1
     */
    public String getColumn1() {
        return column1;
    }

    /**
     * Sets column 1
     * @param column1 column 1.
     */
    public void setColumn1(String column1) {
        this.column1 = column1;
    }

    /**
     * gets column 2
     * @return column 2
     */
    public String getColumn2() {
        return column2;
    }

    /**
     * Sets column 2
     * @param column2 column 2.
     */
    public void setColumn2(String column2) {
        this.column2 = column2;
    }
}
