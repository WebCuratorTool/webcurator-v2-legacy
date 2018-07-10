package org.webcurator.domain.model;

/**
 * Hibernate Test
 * @hibernate.class table="HIBERNATE_TEST" lazy="false"
 * @hibernate.query name="org.webcurator.domain.model.HibernateTest.getAll" query="SELECT ht FROM HibernateTest ht"
 */
public class HibernateTest {

    /** Query to retrieve all hibernate test data. */
    public static final String QRY_GET_ALL = "org.webcurator.domain.model.HibernateTest.getAll";

    private Long id;
    private String column1;
    private String column2;

    /**
     * gets the id, this is its primary key
     * @return the id
     * @hibernate.id column="ID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="HibernateTest"
     */
    public Long getId() {
        return id;
    }

    /**
     * Set the database ID.
     * @param id The database OID.
     */
    public void setId(Long id) {
        this.id = id;
    }

    /**
     * gets column 1
     * @return column 1
     * @hibernate.property column="COLUMN1" not-null="false" length="100"
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
     * @hibernate.property column="COLUMN2" not-null="false" length="100"
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
