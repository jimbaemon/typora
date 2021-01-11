package typora.image.server.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;
import typora.image.server.service.ImageService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

@Controller
@RequestMapping(value = "/image")
public class ImageController {

    private final ImageService imageService;

    @Autowired
    public ImageController(ImageService imageService){
        this.imageService = imageService;
    }

    @PostMapping
    public ResponseEntity uploadImage(@RequestParam("file")MultipartFile file) throws IOException {

        //파일 업로드 진행
        Long id = imageService.uploadImage(file);

        var selfLinkBuilder = linkTo(ImageController.class).slash(id);
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(createdUri.toString());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadImage(@PathVariable Long id, HttpServletRequest request) throws IOException, NotImageException {

        ImageDto resource = imageService.downloadImage(id);

        String fileName = resource.getFileName();
        String userAgent = request.getHeader("User-Agent");

        String saveName = setEndoing(userAgent, fileName);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(resource.getMimeType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+saveName+"\"")
                .body(resource.getImageResource());
    }

    private String setEndoing(String userAgent, String fileName) throws UnsupportedEncodingException {
        if (userAgent.contains("MSIE") || userAgent.contains("Trident")) {
            fileName = URLEncoder.encode(fileName,"UTF-8").replaceAll("\\+", "%20");
        } else {
            fileName = new String(fileName.getBytes("UTF-8"), "ISO-8859-1");
        }

        return fileName;
    }

}
