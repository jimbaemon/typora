package typora.image.server.image;

import org.springframework.data.jpa.repository.JpaRepository;
import typora.image.server.entity.ImageInfo;

public interface ImageRepository extends JpaRepository<ImageInfo, Long> {
}
