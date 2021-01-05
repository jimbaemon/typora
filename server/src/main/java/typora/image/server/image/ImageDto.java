package typora.image.server.image;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.core.io.Resource;

@Getter @Setter @Builder
public class ImageDto {
    private Resource imageResource;
    private String fileName;
    private String mimeType;
}
