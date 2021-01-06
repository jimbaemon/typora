package typora.image.cilent;

import typora.image.cilent.service.ImageUploadService;

public class Main {
    public static void main(String[] args) {
        ImageUploadService imageUploadService = new ImageUploadService();
        try {
            for (String arg : args) {
                System.out.println(imageUploadService.imageUpload(arg));
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
