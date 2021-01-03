package typora.image.server.service;

import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;
import typora.image.server.image.ImageDto;

import java.io.IOException;
import java.net.MalformedURLException;

public interface ImageService {

    Long uploadImage(MultipartFile file) throws IOException;

    ImageDto downloadImage(Long id) throws IOException;
}
