package typora.image.server.image;

public class NotImageException extends RuntimeException{
    public NotImageException(String message){
        super(message);
    }
}
