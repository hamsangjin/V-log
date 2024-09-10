<div align="center">
<h1> V-log </h1>
인기 블로깅 플랫폼인 Velog를 Spring Boot, HTML, CSS, JS를 사용하여 클론코딩한 프로젝트
</div>

<br>

# 목차
  - [개요](#개요)
  - [기술 스택](#기술-스택)
  - [도메인 설계](#도메인-설계)
  - [패키지 구조](#패키지-구조)
  - [Preview](#preview)
  - [시연 영상](#시연-영상)
  - [개발 기능](#개발-기능)

<br>
<br>

# 개요
- 프로젝트 이름: V-log
- 프로젝트 지속기간: 2024. 06. 18 ~ 2024. 07. 19

<br>
<br>

# 기술 스택
| 분류 | 기술 스택 |
|:---:|:---:|
| 개발 언어 | ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white) ![JavaScript](https://img.shields.io/badge/javascript-%23323330.svg?style=for-the-badge&logo=javascript&logoColor=%23F7DF1E) ![HTML5](https://img.shields.io/badge/html5-%23E34F26.svg?style=for-the-badge&logo=html5&logoColor=white) ![CSS3](https://img.shields.io/badge/css3-%231572B6.svg?style=for-the-badge&logo=css3&logoColor=white) |
| 프레임워크/라이브러리 | ![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Spring Boot](https://img.shields.io/badge/springboot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white) ![Sprint Secutiry](https://img.shields.io/badge/springsecurity-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white) <!-- ![JWT](https://img.shields.io/badge/JWT-black?style=for-the-badge&logo=JSON%20web%20tokens)--> ![Thymeleaf](https://img.shields.io/badge/Thymeleaf-%23005C0F.svg?style=for-the-badge&logo=Thymeleaf&logoColor=white) | 
| 데이터베이스 | ![Mysql](https://img.shields.io/badge/mysql-4479A1?style=for-the-badge&logo=mysql&logoColor=white) |
| etc | ![GitHub](https://img.shields.io/badge/github-%23121011.svg?style=for-the-badge&logo=github&logoColor=white) ![Notion](https://img.shields.io/badge/Notion-%23000000.svg?style=for-the-badge&logo=notion&logoColor=white)

<br>
<br>

# 도메인 설계
<img width="826" alt="스크린샷 2024-07-23 23 02 42" src="https://github.com/user-attachments/assets/3b50340f-7018-494f-bc74-b003437f492a">

<br>
<br>

# 패키지 구조
```
src
├── main
│   ├── java
│   │   └── hello
│   │       └── velog
│   │           ├── config
│   │           ├── controller
│   │           ├── domain
│   │           ├── dto
│   │           ├── exception
│   │           ├── global
│   │           ├── repository
│   │           └── service
│   └── resources
│       ├── static
│       │   ├── css
│       │   ├── images
│       │   │   ├── post
│       │   │   └── user
│       │   └── js
│       └── templates
│           └── fragments
└── test
```

<br>
<br>


# Preview
## 1. Home
|                   트렌딩                   |                   최신                    |                         피드(로그인 O)                          |
| :---------------------------------------: | :--------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/0c15fc66-e012-4054-940a-0c350ba94489" alt="트렌딩" width=80%> | <img src="https://github.com/user-attachments/assets/8803aa0d-86e4-4801-b557-da768e10813a" alt="최신" width=80%> | <img src="https://github.com/user-attachments/assets/7283431d-8482-4615-af8b-13622a36c263" alt="피드(로그인O)" width=80%> |

## 2. 블로그
|                  내 블로그                  |            내 블로그(게시글 표시)             |                        다른 유저 블로그                          |
| :---------------------------------------: | :--------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/11381f17-e084-483b-bf07-4acf8844acdc" alt="내-글보기" width=80%> | <img src="https://github.com/user-attachments/assets/2de4da68-33b3-4f32-b739-42c7a3eff1ef" alt="내-글보기(게시글-표시)" width=80%> | <img src="https://github.com/user-attachments/assets/bfd9c79e-6d8f-478a-9541-86b2313e67b3" alt="다른-유저-블로그" width=80%> |
|                   시리즈                    |                 소개 작성 X                 |                         소개 작성 O                            |
| <img src="https://github.com/user-attachments/assets/9d25ac13-d5d8-4800-b7f5-4fc976223b6d" alt="시리즈" width=80%> | <img src="https://github.com/user-attachments/assets/ff5f18c9-dfc5-4073-a32e-63b8f0edc14a" alt="소개 작성 X" width=80%> | <img src="https://github.com/user-attachments/assets/0c3ac86a-6c1c-4792-9293-f3c141c25e3c" alt="소개 작성 O" width=80%> |

## 3. 게시글 상세 및 작성
|               게시글 상세(상단)               |              게시글 상세(하단)              |                           게시글 작성                           |
| :---------------------------------------: | :--------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/dfc89d70-e593-490c-8079-274923f02d1e" alt="게시글-상세(상단)" width=80%> | <img src="https://github.com/user-attachments/assets/d14e3e99-4ea2-485e-99db-66183e56fcad" alt="게시글-상세(하단)" width=80%> | <img src="https://github.com/user-attachments/assets/a9ce7f16-9ae4-40c1-8b01-f35b333d4273" alt="게시글-작성" width=80%> | 

## 4. 임시글/읽기목록/설정
|                   임시글                   |                  읽기 목록                  |                             설정                              |
| :---------------------------------------: | :--------------------------------------: | :----------------------------------------------------------: |
| <img src="https://github.com/user-attachments/assets/7eab81f3-6cb1-48ff-831a-cb12cca46545" alt="임시글" width=80%> | <img src="https://github.com/user-attachments/assets/a00e7431-fac7-4cec-8dd6-d5b8075ff8ef" alt="읽기-목록" width=80%> | <img src="https://github.com/user-attachments/assets/aeff5cde-e185-4a8e-83fe-af73ca842693" alt="설정" width=80%> |

<br>
<br>

# 시연 영상
아래 그림을 클릭하면 유튜브로 연결됩니다.

<a href="https://youtu.be/s40xUHc08TI"> 
  <img width="1498" alt="velog 클론 코딩 썸네일" src="https://github.com/user-attachments/assets/b0175278-7ab5-4fa3-8141-78e9c9685606">
</a>

<br>
<br>

# 개발 기능
- `사용자 인증`: Spring Security를 이용하여 회원가입, 로그인, 로그아웃 기능이 구현되었습니다.
- `콘텐츠 표시`: 사용자들이 작성한 최신 글과 인기 글을 정렬하여 볼 수 있습니다.
- `프로필 및 태그`: 개별 사용자 페이지에서는 사용자의 프로필, 태그 목록과 태그별 글의 수가 표시됩니다.
- `블로그 글 관리`: 사용자는 글을 작성, 수정, 삭제할 수 있으며, 즉시 출간하거나 임시 글을 공개적으로 또는 비공개로 출간할 수 있습니다.
- `블로그 글 보기`: 공개된 블로그 글 보기 및 비공개 글 표시 기능을 제공하며, 사용자는 다른 사람의 글에 좋아요를 할 수 있고, 필요에 따라 팔로우할 수 있습니다.
- `댓글 기능`: 블로그 글에 댓글을 작성하고, 답글을 추가하거나 삭제할 수 있습니다.
