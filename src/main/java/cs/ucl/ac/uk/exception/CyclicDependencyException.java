package cs.ucl.ac.uk.exception;

@SuppressWarnings("serial")
public class CyclicDependencyException extends Exception {

	public CyclicDependencyException() {
		// TODO Auto-generated constructor stub
	}

	public CyclicDependencyException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}

	public CyclicDependencyException(Throwable cause) {
		super(cause);
		// TODO Auto-generated constructor stub
	}

	public CyclicDependencyException(String message, Throwable cause) {
		super(message, cause);
		// TODO Auto-generated constructor stub
	}

	public CyclicDependencyException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
		// TODO Auto-generated constructor stub
	}

}
