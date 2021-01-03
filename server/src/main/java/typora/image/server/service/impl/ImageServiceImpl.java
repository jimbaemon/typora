package typora.image.server.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileUrlResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;
import typora.image.server.image.ImageDto;
import typora.image.server.image.ImageRepository;
import typora.image.server.properties.FileUploadProperties;
import typora.image.server.service.ImageService;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.UUID;
import java.util.regex.Matcher;

@Service
public class ImageServiceImpl implements ImageService {

    private final Path filePath;
    private final ImageRepository imageRepository;

    @Autowired
    public ImageServiceImpl(FileUploadProperties prop, ImageRepository imageRepository) throws IOException {
        String uri = prop.getUploadDir();
        String uriChng = uri.replaceAll("/", Matcher.quoteReplacement(File.separator)).trim();

        this.filePath = Paths.get(uriChng).toAbsolutePath().normalize();

        this.imageRepository = imageRepository;
    }

    @Override
    public Long uploadImage(MultipartFile file) throws IOException{

        String fileName = StringUtils.cleanPath(file.getOriginalFilename());
        String randomFileName = UUID.randomUUID() + "_" + fileName;

        Path targetLocation = this.filePath.resolve(randomFileName);
        Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

        ImageInfo imageInfo = ImageInfo.builder()
                                .fileSaveName(randomFileName)
                                .fileOrgName(fileName)
                                .filePath(filePath.toString())
                                .build();

        ImageInfo save = imageRepository.save(imageInfo);

        return save.getId();
    }

    @Override
    public ImageDto downloadImage(Long id) throws IOException {
        Optional<ImageInfo> imageById = imageRepository.findById(id);

        if(imageById.isEmpty()){
            return null;
        }

        ImageInfo imageInfo = imageById.get();
        Path filePath = Paths.get(imageInfo.getFilePath()).toAbsolutePath().normalize().resolve(imageInfo.getFileSaveName());

        Resource resource = new UrlResource(filePath.toUri());
        String mimeType = Files.probeContentType(filePath);
        String fileName = imageInfo.getFileOrgName();

        ImageDto imageDto = ImageDto.builder().fileName(fileName).mimeType(mimeType).imageResource(resource).build();

        return imageDto;
    }
}
