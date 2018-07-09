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
package org.webcurator.ui.target.command;

import org.webcurator.domain.model.core.DublinCore;

/**
 * The command for the target description view 
 * containing the Dublin Core meta data fields.
 * @author nwaight
 */
public class DescriptionCommand {
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

	public static DescriptionCommand fromModel(DublinCore metaData) {
		DescriptionCommand cmd = new DescriptionCommand();
		
		if (metaData != null) {
			cmd.setContributor(metaData.getContributor());
			cmd.setCoverage(metaData.getCoverage());
			cmd.setCreator(metaData.getCreator());
			cmd.setDescription(metaData.getDescription());
			cmd.setFormat(metaData.getFormat());
			cmd.setIdentifier(metaData.getIdentifier());
			cmd.setIsbn(metaData.getIsbn());
			cmd.setIssn(metaData.getIssn());
			cmd.setLanguage(metaData.getLanguage());
			cmd.setPublisher(metaData.getPublisher());
			cmd.setRelation(metaData.getRelation());
			cmd.setSource(metaData.getSource());
			cmd.setSubject(metaData.getSubject());
			cmd.setTitle(metaData.getTitle());
			cmd.setType(metaData.getType());
		}
		
		return cmd;
	}
	
	public DublinCore toModelObject() {
		DublinCore dc = new DublinCore();
		
		dc.setContributor(contributor);
		dc.setCoverage(coverage);
		dc.setCreator(creator);
		dc.setDescription(description);
		dc.setFormat(format);
		dc.setIdentifier(identifier);
		dc.setIsbn(isbn);
		dc.setIssn(issn);
		dc.setLanguage(language);
		dc.setPublisher(publisher);
		dc.setRelation(relation);
		dc.setSource(source);
		dc.setSubject(subject);
		dc.setTitle(title);
		dc.setType(type);
		
		return dc;
	}	
	
	/**
	 * @return the contributor
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
	 * @return the publisher
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
