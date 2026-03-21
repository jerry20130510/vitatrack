package core.exception;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import web.member.exception.BusinessException;


@ControllerAdvice
public class GlobalExceptionHandler {

	private static final Logger logger = LogManager.getLogger(GlobalExceptionHandler.class);

	@ExceptionHandler(BusinessException.class)
	public ResponseEntity<Map<String, Object>> handleBusinessException(BusinessException  b) {
		Map<String, Object> response = new HashMap<>();
	    response.put("success", false);
	    response.put("message", b.getMessage());
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}
	//當客户端發送的請求參數無法被控制器方法正確解析時，就會抛出異常!
	@ExceptionHandler(MethodArgumentTypeMismatchException.class)
	public ResponseEntity<Map<String, Object>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
	    Map<String, Object> response = new HashMap<>();
	    response.put("success", false);
	    response.put("message", "參數格式錯誤");
	    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
	}


	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> handleSystemException(Exception e) {
		logger.error("Unexpected exception occurred", e);
		Map<String, Object> response = new HashMap<>();
		response.put("success", false);
		response.put("message", "系統錯誤,請稍後再試");
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
	}
	
	
	

}
