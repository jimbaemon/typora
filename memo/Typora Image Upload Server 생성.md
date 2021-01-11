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



