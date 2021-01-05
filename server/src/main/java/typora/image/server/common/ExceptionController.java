package typora.image.server.common;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import typora.image.server.image.NotImageException;

@ControllerAdvice
public class ExceptionController {

    @ExceptionHandler({
            NotImageException.class
    })
    public ResponseEntity<Object> BadRequestException(final RuntimeException ex){
        //https://velog.io/@aidenshin/Spring-Boot-Exception-Controller 참조

        return ResponseEntity.badRequest().body(ex.getMessage());
    }
}
