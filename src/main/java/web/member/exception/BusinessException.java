package web.member.exception;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class BusinessException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	private static final Logger logger = LogManager.getLogger(BusinessException.class);

	public BusinessException(String message) {
		super(message);
		logger.error("BusinessException: {}", message);
	}

	public BusinessException(String message, Throwable e) {
		super(message,e);
		logger.error("BusinessException: {}", message, e);
	}
}
