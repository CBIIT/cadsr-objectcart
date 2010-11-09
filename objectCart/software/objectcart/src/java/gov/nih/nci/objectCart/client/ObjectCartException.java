package gov.nih.nci.objectCart.client;

/**
 * This exception is thrown by all the methods of the {@link ObjectCartClient}
 * This exception contains the actual error or the error message of 
 * the business error that occurred during processing the request.
 * 
 * @author Ekagra Software Technologies Ltd.
 */
public class ObjectCartException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Default constructor. Constructs the (@link ObjectCartException) object 
	 */
	public ObjectCartException()
	{
		super();
	}
	/**
	 * Constructs the {@link ApplicationException} object with the passed message 
	 * @param message The message which is describes the exception caused
	 */
	public ObjectCartException(String message)
	{
		super(message);
	}
	/**
	 * Constructs the {@link ObjectCartException} object with the passed message.
	 * It also stores the actual exception that occurred 
	 * @param message The message which is describes the exception caused
	 * @param cause The actual exception that occurred
	 */
	public ObjectCartException(String message, Throwable cause)
	{
		super(message, cause);
	}
	/**
	 * Constructs the {@link ObjectCartException} object storing the actual 
	 * exception that occurred 
	 * @param cause The actual exception that occurred
	 */
	public ObjectCartException(Throwable cause)
	{
		super(cause);
	}
}
