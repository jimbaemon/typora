package typora.image.server.image;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import typora.image.server.entity.ImageInfo;
import typora.image.server.service.ImageService;

import java.net.URI;
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
    public ResponseEntity uploadImage(@RequestParam("file")MultipartFile file){
        List<ImageResource> imageResources = new ArrayList<>();

        //파일 업로드 진행
        Long id = imageService.uploadImage(file);

        var selfLinkBuilder = linkTo(ImageController.class).slash(id);
        URI createdUri = selfLinkBuilder.toUri();

        return ResponseEntity.created(createdUri).body(createdUri);
    }

}
