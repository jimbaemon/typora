package typora.image.server.service.impl;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;
import typora.image.server.service.ImageService;

@Service
public class ImageServiceImpl implements ImageService {
    @Override
    public Long uploadImage(MultipartFile file) {
        return 0l;
    }
}
