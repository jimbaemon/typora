package typora.image.server.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
@Builder @NoArgsConstructor @AllArgsConstructor
public class ImageInfo {

    @Id @GeneratedValue
    private Long id;
    private String fileOrgName;
    private String fileSaveName;
    private String filePath;
}
