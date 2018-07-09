/*
 *  Copyright 2006 The National Library of New Zealand
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.webcurator.domain.model.core;

/**
 * The DublinCore class contains the basic meta data description fields to be associated with an
 * object.
 * The Dublin Core metadata element set is a standard for cross-domain information resource description. 
 * Here an information resource is defined to be "anything that has identity". 
 * This is the definition used in Internet RFC 2396, "Uniform Resource Identifiers (URI): Generic Syntax", 
 * by Tim Berners-Lee et al. There are no fundamental restrictions to the types of resources to which 
 * Dublin Core metadata can be assigned.
 * 
 * @author nwaight
 * @hibernate.class table="DUBLIN_CORE" lazy="false"
 */
public class DublinCore {
	/** max length of the title field. */
	public static final int MAX_LEN_TITLE = 255;
	/** max length of the creator field. */
	public static final int MAX_LEN_CREATOR = 255;
	/** max length of the subject field. */
	public static final int MAX_LEN_SUBJECT = 2000;
	/** max length of the description field. */
	public static final int MAX_LEN_DESCRIPTION = 2000;
	/** max length of the publisher field. */
	public static final int MAX_LEN_PUBLISHER = 255;
	/** max length of the contributor field. */
	public static final int MAX_LEN_CONTRIBUTOR = 255;
	/** max length of the type field. */
	public static final int MAX_LEN_TYPE = 50;
	/** max length of the format field. */
	public static final int MAX_LEN_FORMAT = 255;
	/** max length of the identifier field. */
	public static final int MAX_LEN_IDENTIFIER = 255;
	/** max length of the source field. */
	public static final int MAX_LEN_SOURCE = 255;
	/** max length of the language field. */
	public static final int MAX_LEN_LANGAGE = 255;
	/** max length of the relation field. */
	public static final int MAX_LEN_RELATION = 255;
	/** max length of the coverage field. */
	public static final int MAX_LEN_COVERAGE = 255;
	/** max length of the ISSN field. */
	public static final int MAX_LEN_ISSN = 9;
	/** max length of the ISBN field. */
	public static final int MAX_LEN_ISBN = 13;
	
	/** the unique id of this meta data set. */
	private Long oid;
	/** A name given to the resource. */
	private String title;
	/** An entity primarily responsible for making the content of the resource. */
	private String creator;
	/** A topic of the content of the resource. */
	private String subject;
	/** An account of the content of the resource. */
	private String description;
	/** An entity responsible for making the resource available. */
	private String publisher;
	/** An entity responsible for making contributions to the content of the resource. */
	private String contributor;
	/** The nature or genre of the content of the resource. */
	private String type;
	/** The physical or digital manifestation of the resource. */
	private String format;
	/** An unambiguous reference to the resource within a given context. */
	private String identifier;
	/** A Reference to a resource from which the present resource is derived. */
	private String source;
	/** A language of the intellectual content of the resource. */
	private String language;
	/** A reference to a related resource. */
	private String relation;
	/** The extent or scope of the content of the resource. */
	private String coverage;
	/** International Standard Serial Number. */
	private String issn;
	/** International Standard Book Number. */
	private String isbn;
	
	/**
	 * @return the contributor
	 * @hibernate.property column="DC_CONTRIBUTOR" length="255"
	 */
	public String getContributor() {
		return contributor;
	}
	/**
	 * @param contributor the contributor to set
	 */
	public void setContributor(String contributor) {
		this.contributor = contributor;
	}
	/**
	 * @return the coverage
	 * @hibernate.property column="DC_COVERAGE" length="255"
	 */
	public String getCoverage() {
		return coverage;
	}
	/**
	 * @param coverage the coverage to set
	 */
	public void setCoverage(String coverage) {
		this.coverage = coverage;
	}
	/**
	 * @return the creator
	 * @hibernate.property column="DC_CREATOR" length="255"
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * @param creator the creator to set
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * @return the description
	 * @hibernate.property column="DC_DESCRIPTION" length="2000"
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the format
	 * @hibernate.property column="DC_FORMAT" length="255"
	 */
	public String getFormat() {
		return format;
	}
	/**
	 * @param format the format to set
	 */
	public void setFormat(String format) {
		this.format = format;
	}
	/**
	 * @return the identifier
	 * @hibernate.property column="DC_IDENTIFIER" length="255"
	 */
	public String getIdentifier() {
		return identifier;
	}
	/**
	 * @param identifier the identifier to set
	 */
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	/**
	 * @return the isbn
	 * @hibernate.property column="DC_IDENTIFIER_ISBN" length="13"
	 */
	public String getIsbn() {
		return isbn;
	}
	/**
	 * @param isbn the isbn to set
	 */
	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}
	/**
	 * @return the issn
	 * @hibernate.property column="DC_IDENTIFIER_ISSN" length="9"
	 */
	public String getIssn() {
		return issn;
	}
	/**
	 * @param issn the issn to set
	 */
	public void setIssn(String issn) {
		this.issn = issn;
	}
	/**
	 * @return the language
	 * @hibernate.property column="DC_LANGUAGE" length="255"
	 */
	public String getLanguage() {
		return language;
	}
	/**
	 * @param language the language to set
	 */
	public void setLanguage(String language) {
		this.language = language;
	}
	/**
	 * @return the oid
	 * @hibernate.id column="DC_OID" generator-class="org.hibernate.id.MultipleHiLoPerTableGenerator"
     * @hibernate.generator-param name="table" value="ID_GENERATOR"
     * @hibernate.generator-param name="primary_key_column" value="IG_TYPE"
     * @hibernate.generator-param name="value_column" value="IG_VALUE"
     * @hibernate.generator-param name="primary_key_value" value="DublinCore" 
	 */
	public Long getOid() {
		return oid;
	}
	/**
	 * @param oid the oid to set
	 */
	public void setOid(Long oid) {
		this.oid = oid;
	}
	/**
	 * @return the publisher
	 * @hibernate.property column="DC_PUBLISHER" length="255"
	 */
	public String getPublisher() {
		return publisher;
	}
	/**
	 * @param publisher the publisher to set
	 */
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	/**
	 * @return the relation
	 * @hibernate.property column="DC_RELATION" length="255"
	 */
	public String getRelation() {
		return relation;
	}
	/**
	 * @param relation the relation to set
	 */
	public void setRelation(String relation) {
		this.relation = relation;
	}
	/**
	 * @return the source
	 * @hibernate.property column="DC_SOURCE" length="255"
	 */
	public String getSource() {
		return source;
	}
	/**
	 * @param source the source to set
	 */
	public void setSource(String source) {
		this.source = source;
	}
	/**
	 * @return the subject
	 * @hibernate.property column="DC_SUBJECT" length="2000"
	 */
	public String getSubject() {
		return subject;
	}
	/**
	 * @param subject the subject to set
	 */
	public void setSubject(String subject) {
		this.subject = subject;
	}
	/**
	 * @return the title
	 * @hibernate.property column="DC_TITLE" length="255"
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the type
	 * @hibernate.property column="DC_TYPE" length="50"
	 */
	public String getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}	
}
