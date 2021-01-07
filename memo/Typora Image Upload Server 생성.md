# Typora Image Upload Server 생성

**MarkDown** 언어를 배운뒤로 나의 모든 정리는 `.md` 를 통해 진행하고 있다.

![정리는 md로](C:\Users\jimfo\AppData\Roaming\Typora\typora-user-images\image-20210102164439962.png "점점 늘어만 가는 .md")



다른것은 만족하면서 사용하고 있지만...... 
이미지가 PC의 상대경로로 들어가서 다른곳에서 pull 받으면 정상적인 이용이 불가능하다...

![깨지는 이미지들](C:\Users\jimfo\AppData\Roaming\Typora\typora-user-images\image-20210102164814942.png "무수히 깨지는 이미지")



이미지 업로드시 URL 로 다운받을수 있는 이미지 서버를 개발해 보자.



## Typora Image Upload Server 개발을 위한 조사 시작

Typora 에서는 image uploader 라는 기능을 지원한다. 

![image-20210102171237338](C:\prj\typora\memo\image-20210102171237338.png)

https://support.typora.io/Upload-Image/ 에 상세 정보가 나온다.

상세 정보에서 Custom 탭을 보면 아래와 같다.

![image-20210102171357640](C:\prj\typora\memo\image-20210102171357640.png)

대충 특정 이미지를 업로드 진행시, 업로드 갯수만큼의 URL 이 나오면 정상적으로 Upload 가 되었다고 인식하는듯 하다.



JAVA 에서 대충 테스트를 진행해 보도록 한다.



### 테스트용 class 생성

![image-20210102172155137](C:\prj\typora\memo\image-20210102172155137.png)

가볍게 실행시 https://test.jpg 라는 URL 을 출력하는 class 를 만들어서 typora 에서 실행해 보도록 한다.

`Image Uploader` 를 Custom Command 로 변경 하고 생성한 jar 파일을 실행하는 명령어를 `Command`부분에 입력한다.

![image-20210102171633249](C:\prj\typora\memo\image-20210102171633249.png)

![image-20210102172223842](C:\prj\typora\memo\image-20210102172223842.png)



결과는 실패.... 명령어를 자세히 보니, 2개의 이미지가 들어간다... 출력문도 2개로 바꿔본다.

![image-20210102172337219](C:\prj\typora\memo\image-20210102172337219.png)

테스트 결과는...

![image-20210102172613546](C:\prj\typora\memo\image-20210102172613546.png)

성공이다!!!!! 이걸로 기본적인 조사는 완료.



## Typora Image Upload Server 개발을 위한 설계 시작

## Client

>Typora 이미지 업로드시 Terminal 을 통한 명령어 실행으로 이미지 업로드가 진행된다.
>
>패턴은 실행명령어 "이미지경로"  이다.
>
>이미지 경로를 받아서 OutputStream 으로 파일을 전송하는 Client 개발이 필요하다.

#### 기능

* 이미지를 갯수별로 입력받아 Buffer 로 변경 RESTAPI Request 양식에 맞춰 이미지 전송후 반환되는 json 에서 URI 을 Console Output 으로 반환



## Server

> REST API 방식으로 파일을 전송 받고 
> 이미지를 압축하여 다운받을수 있는 경로를 전송하는 Server 개발 필요

#### 기능

* 전송 받은 이미지를 압축하여 저장, 이미지 정보를 데이터 베이스에서 저장후, 다운로드 받을수 있는 경로 생성 및 반환.



## SERVER 개발

![image-20210107213134204](C:\prj\typora\memo\image-20210107213134204.png)

패키지 구조는 아래와 같이 잡았다.

* common : 함께 사용하는 기능을 넣어두었다
  * ExceptionController : Exception를 공통처리해 주는 클래스이다. 
* entity : 엔티티용 패키지
  * ImageInfo : 이미지 정보를 담고 있는 Entity
* image : 이미지 업로드 다운로드시 필요한 클래스를 담고 있는 패키지
  * ImageController : 주요 컨트롤러
  * imageDto : Resource, 파일명등을 담고있는 DTO
  * ImageRepository : Spring JPA 용 Repository Interface
  * NotImageException : 이미지가 아닐경우 Exception 처리
* service : 서비스용 패키지
  * ImageService : 이미지 업로드, 다운로드용 서비스
  * ImageServiceImpl : 구현체



#### 주요소스

```
@PostMapping
public ResponseEntity uploadImage(@RequestParam("file")MultipartFile file) throws IOException {

//파일 업로드 진행
Long id = imageService.uploadImage(file);

var selfLinkBuilder = linkTo(ImageController.class).slash(id);
URI createdUri = selfLinkBuilder.toUri();

return ResponseEntity.created(createdUri).body(createdUri+".dummy");
}
```

이미지 업로드시 해당 이미지의 다운로드 링크를 반환해주는 URI



```
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
```

반환받은 이미지로 접근시 이미지를 다운로드 하는 메소드.



```
@Override
public Long uploadImage(MultipartFile file) throws IOException{

    String fileName = StringUtils.cleanPath(file.getOriginalFilename());
    System.out.println(file.getContentType());
    if(!file.getContentType().startsWith("image/")){
        throw new NotImageException("이미지 타입만 업로드 가능합니다.");
    }
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
```

이미지 업로드 진행 구현체, 이미지가 아니면 NotImageException 처리.



## Client 개발

![image-20210107213858231](C:\prj\typora\memo\image-20210107213858231.png)

MulitipartRequest 파일 전송을 위해 Apache 에서 제공해주는 `MultipartEntityBuilder` 를 이용하기 위해 라이브러리가 필요했고,

라이브러리 관리를 위해 Gradle 프로젝트 생성 이후 Gradle에 관련 라이브러리 dependency 추가..

```
compile 'org.apache.httpcomponents:httpclient:4.5.13'
compile group: 'org.apache.httpcomponents', name: 'httpmime', version: '4.3.1'
```

#### 주요소스

```
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
```

메인에서 함꼐 입력받는 args 를 이용해서 파일 전송 진행, 이후 다운로드 링크를 console 로 출력



```
public String imageUpload(String filePath) throws IOException {
    CloseableHttpClient client = HttpClients.createDefault();

    HttpPost post = new HttpPost("URL");
    File file = new File(filePath);
    MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
    builder.addBinaryBody("file", file, ContentType.create(Files.probeContentType(file.toPath())), file.getName());

    HttpEntity entity = builder.build();
    post.setEntity(entity);
    HttpResponse response = client.execute(post);
    return EntityUtils.toString(response.getEntity());
}
```

main 에서 전달받은 filepath(args)의 파일을 multipart 방식으로 전송.



## 실수 발견과 수정 시작.

> 결론부터 말하자면 망헀다...

위에서 제작한 Client 를 jar 로 build 하여 typora 에서 실행해 보았다.

```
java -jar C:\prj\typora\client\build\libs\client-1.0-SNAPSHOT.jar
```

![image-20210107214453181](C:\prj\typora\memo\image-20210107214453181.png)

아..... 실패다....... 확인을 해보니 RESTAPI 식 URL 에는 확장자가 없기 때문에 Typora 에서 이미지로 인식하지 않는듯하다....
그냥 파일서버로 만들면 됬는데 JPA 까지 사용해볼려던 욕심이 참사를 빚은듯 하다....

일단 지금까지 만든게 아까우니 branch 를 나눠서 기존 소스는 보존하고 신규를 개발을 시작한다.