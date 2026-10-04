# File Field Diff

> 구분자로 구성된 두 텍스트 파일의 특정 필드를 비교하여 일치하지 않는 데이터를 추출하는 Java CLI 도구입니다.

> Source 파일의 특정 필드 값을 Target 파일의 특정 필드와 비교하고, Target에서 찾을 수 없는 Source 행을 결과 파일로 출력합니다.

## 주요 기능

- 두 텍스트 파일의 특정 필드 비교
- Source / Target 비교 필드 위치 설정
- 부분 문자열(`contains`) 기반 비교
- 일치하지 않는 Source 원본 행 추출
- `config.properties`를 통한 파일 경로 설정
- 실행 가능한 JAR 형태로 사용

## 요구 사항

- Java
- Gradle 또는 Gradle Wrapper

## 동작 방식

Source 필드 값이 다음과 같고:

```text
PAY_JOB
```

Target 필드 값이 다음과 같다면:

```text
NEW_PAY_JOB_BATCH
NEW_CANCEL_JOB_BATCH
```

각 Target 필드에 Source 값이 포함되어 있는지 확인합니다.

```text
NEW_PAY_JOB_BATCH.contains("PAY_JOB") → true
```

어떤 Target 필드에도 Source 값이 포함되어 있지 않으면 해당 Source의 전체 원본 행을 결과 파일에 기록합니다.

> 비교는 완전 일치가 아닌 부분 문자열 포함 여부를 기준으로 합니다.

## 설정

실행할 JAR 파일과 동일한 디렉터리에 `config.properties`를 생성합니다.

```properties
source.path=/path/to/source.txt
target.path=/path/to/target.txt
output.path=/path/to/result.txt

source.field.index=1
target.field.index=2
```

`config.properties`의 필드 위치는 **1부터 시작합니다.**

예를 들어 다음과 같은 데이터가 있다면:

```text
A001|PAY_JOB|ACTIVE
```

| 위치 | 값 |
|---|---|
| 1 | `A001` |
| 2 | `PAY_JOB` |
| 3 | `ACTIVE` |

따라서:

```properties
source.field.index=2
```

로 설정하면 `PAY_JOB` 필드를 비교 대상으로 사용합니다.

## 사용 예시

### Source 파일

```text
100001|PAY_JOB|ACTIVE
100002|CANCEL_JOB|ACTIVE
100003|UNKNOWN_JOB|ACTIVE
```

### Target 파일

```text
200001|NEW_PAY_JOB_BATCH|ACTIVE
200002|NEW_CANCEL_JOB_BATCH|ACTIVE
```

다음과 같이 설정하면:

```properties
source.field.index=2
target.field.index=2
```

`PAY_JOB`은 `NEW_PAY_JOB_BATCH`에 포함되어 있으므로 일치합니다.

`CANCEL_JOB` 역시 `NEW_CANCEL_JOB_BATCH`에 포함되어 있으므로 일치합니다.

반면 `UNKNOWN_JOB`은 어떤 Target 값에도 포함되어 있지 않습니다.

### 결과 파일

```text
100003|UNKNOWN_JOB|ACTIVE
```

## 빌드

Gradle Wrapper를 사용하여 빌드할 수 있습니다.

### Linux / macOS

```bash
./gradlew clean build
```

### Windows

```powershell
.\gradlew clean build
```

빌드된 JAR 파일은 다음 디렉터리에 생성됩니다.

```text
build/libs/
```

## 실행 방법

JAR 파일과 `config.properties`를 동일한 디렉터리에 배치합니다.

```text
file-field-diff/
├── file-field-diff-1.0-SNAPSHOT.jar
└── config.properties
```

이후 다음과 같이 실행합니다.

```bash
java -jar file-field-diff-1.0-SNAPSHOT.jar
```

`config.properties`에서 지정한 Source 파일과 Target 파일은 실행 전에 존재해야 합니다.

## 파일 형식

현재 구현에서는 파이프(`|`)를 구분자로 사용하는 텍스트 파일을 대상으로 합니다.

```text
필드1|필드2|필드3|필드4
```

행 마지막에 빈 필드가 존재하는 경우에도 해당 필드를 유지하여 처리합니다.

## 참고 사항

현재 비교 방식은 다음과 같습니다.

```text
targetField.contains(sourceField)
```

따라서 Source 값이 Target 값의 일부로 포함되어 있기만 해도 일치한 것으로 판단합니다.

예를 들어:

```text
Source: PAY_JOB
Target: NEW_PAY_JOB_01_BATCH
결과: 일치
```

부분 문자열 비교 특성상 의도하지 않은 값까지 일치할 수 있으므로, 비교할 Source 필드는 충분히 구체적인 값을 사용하는 것이 좋습니다.

## 라이선스

이 프로젝트는 MIT License를 따릅니다.