package typora.image.server.service;

import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;

public interface ImageService {

    public Long uploadImage(MultipartFile file);
}
