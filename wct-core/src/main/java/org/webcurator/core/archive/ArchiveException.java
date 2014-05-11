package org.webcurator.core.archive;

public class ArchiveException extends Exception {

	/** For serialisation. */
	private static final long serialVersionUID = 1L;

	public ArchiveException() {
		super();
	}

	public ArchiveException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public ArchiveException(String arg0) {
		super(arg0);
	}

	public ArchiveException(Throwable arg0) {
		super(arg0);
	}

}
