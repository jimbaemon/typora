package typora.image.server.entity;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

@Entity
@Getter @Setter @EqualsAndHashCode(of = "id")
public class ImageInfo {

    @Id @GeneratedValue
    private Long id;

    private String fileOrgName;
    private String fileSaveName;
    private String fileExt;
    private String filePath;
}
