package typora.image.server.image;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import typora.image.server.entity.ImageInfo;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

public class ImageResource extends EntityModel<ImageInfo> {

    public ImageResource(ImageInfo imageInfo, Link... links){
        super(imageInfo, links);
        add(linkTo(ImageController.class).slash(imageInfo.getId()).withSelfRel());
    }
}
