/*L
 * Copyright Ekagra Software Technologies Ltd, SAIC-F
 *
 * Distributed under the OSI-approved BSD 3-Clause License.
 * See http://ncip.github.com/cadsr-objectcart/LICENSE.txt for details.
 */

package gov.nih.nci.objectCart.util;

/**
 * This exception is thrown by all the methods of the {@link Validator}
 * This exception contains the actual error or the error message of 
 * the business error that occurred during processing the request.
 * 
 * @author Ekagra Software Technologies Ltd.
 */
public class ValidatorException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * Default constructor. Constructs the (@link ValidatorException) object 
	 */
	public ValidatorException()
	{
		super();
	}
	/**
	 * Constructs the {@link ValidatorException} object with the passed message 
	 * @param message The message which is describes the exception caused
	 */
	public ValidatorException(String message)
	{
		super(message);
	}
	/**
	 * Constructs the {@link ValidatorException} object with the passed message.
	 * It also stores the actual exception that occurred 
	 * @param message The message which is describes the exception caused
	 * @param cause The actual exception that occurred
	 */
	public ValidatorException(String message, Throwable cause)
	{
		super(message, cause);
	}
	/**
	 * Constructs the {@link ValidatorException} object storing the actual 
	 * exception that occurred 
	 * @param cause The actual exception that occurred
	 */
	public ValidatorException(Throwable cause)
	{
		super(cause);
	}
}
