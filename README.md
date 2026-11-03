# Лаборатори №8: Mockito — Mock, Stub, Verify

**Хичээл:** F.CSA313 — Программ хангамжийн чанарын баталгаа ба тест (2026)  
**Нэр:** М.Алтан-Од  
**Оюутны код:** B222270023  

## Ажлын орчин

```bash
$ java -version
openjdk version "17.0.20.1" 2026-08-18
OpenJDK Runtime Environment (build 17.0.20.1+1-1-26.04-Ubuntu)
OpenJDK 64-Bit Server VM (build 17.0.20.1+1-1-26.04-Ubuntu, mixed mode, sharing)

$ mvn -version
Apache Maven 3.9.12
Maven home: /usr/share/maven
Java version: 17.0.20.1, vendor: Ubuntu, runtime: /usr/lib/jvm/java-17-openjdk-amd64
OS name: "linux", version: "6.18.54.1-microsoft-standard-wsl2", arch: "amd64", family: "unix"
```

## Төслийн бүтэц

- `src/main/java/mn/edu/must/sqat/CourseRegistrationService.java` — тестлэгдэх класс
- `src/test/java/mn/edu/must/sqat/CourseRegistrationServiceTest.java` — Mockito тестүүд
- `results/mvn-test.txt` — бүх тест амжилттай давсан үр дүн
- `results/mvn-test-mutant-1.txt`, `-2.txt`, `-3.txt` — мутаци бүрийн алдаа заасан гаралт

## Тестийн тоо

Нийт **10 тест**: 7 ширхэг `@Test` (A1–A7) + 1 ширхэг `@ParameterizedTest` (A8), түүний `@CsvSource` дахь 3 мөр тус бүр тусдаа тест болж тоологддог.

> **7 + 3 = 10**

Энэ нь `results/mvn-test.txt`-ийн `Tests run: 10, Failures: 0, Errors: 0, Skipped: 0` мөртэй яг таарч байна.

## `lenient()` ашигласан шалтгаан

A8 параметртэй тестэд мөр бүр `findStudent` болон `courseExists` хоёуланг stub хийдэг боловч эхний хоёр мөрөнд `register()` метод `courseExists`-г дуудахаас өмнө алдааны утга буцаадаг. Mockito-ийн strict stubs горим нь ашиглагдаагүй stub байгаа тохиолдолд `UnnecessaryStubbingException` шиддэг тул `lenient()` ашиглан энэ шалгалтыг зөвхөн тухайн тест дээр түр хассан болно.

## Мутацийн шалгалт

Кодыг санаатайгаар 3 өөр байдлаар эвдэж (мутаци үүсгэж), тест бүрийг ажиллуулж шалгасан. Мутаци бүрийг илрүүлсний дараа кодыг эх хэвээр нь сэргээсэн болно.

| № | Мутаци | Унасан тест | Шалтгаан |
|---|---|---|---|
| 1 | `if (!student.active())` шалгалтыг коммент болгосон | `register_inactiveStudent_returnsErrorInactiveAndDoesNotTouchEmail`, `register_errorCases_returnExpectedError[2]` | `ERROR_INACTIVE_STUDENT` хүлээсэн боловч `ERROR_NO_COURSE` буцсан |
| 2 | `repository.saveRegistration(...)` дуудлагыг коммент болгосон | `register_activeStudent_returnsOkSavesAndSendsEmail`, `register_emailSenderThrows_stillReturnsOkAndSaves` | `Wanted but not invoked: repository.saveRegistration(...)` |
| 3 | `courseId == null` шалгалтыг устгасан | `register_nullStudentId_throwsIllegalArgumentException` | `IllegalArgumentException` шидэгдэх ёстой байсан ч шидэгдээгүй (`ERROR_NO_STUDENT` буцсан) |

3 мутаци бүгд амжилттай илэрсэн (Failures ≥ 1, `BUILD FAILURE`), тиймээс тестүүдийг нэмж өөрчлөх шаардлагагүй байсан.

## Дүгнэлт

Mock нь жинхэнэ хамаарлуудыг (`repository`, `email sender` гэх мэт) орлож, тухайн метод дуудагдсан эсэх болон ямар аргументтай дуудагдсаныг шалгах боломж олгодог бол fake нь жинхэнэ логиктой бүрэн ажилладаг хөнгөн хэрэгжилт (жишээ нь, санах ойд өгөгдөл хадгалдаг repository) юм. 

Энэ лабораторийн хувьд 2-р мутацийн үед `saveRegistration`-г огт дуудахгүй байсан ч `"OK"` буцааж байсныг зөвхөн буцаах утгыг шалгадаг энгийн тестүүд илрүүлж чадахгүй байсан ба харин `verify` л энэ алдааг бодитоор барьж чадсан юм. Нөгөө талаас, `verify`-г хэтрүүлэн ашиглавал (бүх дуудлага, дарааллыг нь хүртэл хатуу шалгавал) тест нь кодын дотоод хэрэгжилттэй (implementation details) хэт нягт холбогдож, цаашид кодыг зөв рефакторинг хийхэд ч тест унах эрсдэлтэй болдог. Тиймээс зөвхөн бизнесийн утга учиртай гадаад үр дүн/нөлөөг (`repository`-д хадгалсан эсэх, имэйл илгээсэн эсэх) `verify` хийх нь зүйтэй. 

Mock хийж болохгүй зүйлсэд энгийн утгын объект (`Student` record), `List` зэрэг стандарт классууд болон тестэлж буй классын өөрийнх нь дотоод логик орно. Учир нь эдгээрийг mock хийвэл бид бодит кодыг биш, өөрсдийн таамаглалыг шалгах болж хувирна. Мөн stub-ийг хэтрүүлэн ашиглавал тест нь бодит ажиллагааг биш, харин бидний бэлдсэн хуурамч ертөнцийг шалгах эрсдэлтэй. 