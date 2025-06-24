# backend\.gitattributes

```
/mvnw text eol=lf
*.cmd text eol=crlf
```

# backend\.gitignore

```
HELP.md
target/
!.mvn/wrapper/maven-wrapper.jar
!**/src/main/**/target/
!**/src/test/**/target/

### STS ###
.apt_generated
.classpath
.factorypath
.project
.settings
.springBeans
.sts4-cache

### IntelliJ IDEA ###
.idea
*.iws
*.iml
*.ipr

### NetBeans ###
/nbproject/private/
/nbbuild/
/dist/
/nbdist/
/.nb-gradle/
build/
!**/src/main/**/build/
!**/src/test/**/build/

### VS Code ###
.vscode/
```

# backend\movie_db.sql

```
-- Active: 1744333988204@@127.0.0.1@3306@movie_db
CREATE DATABASE movie_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

# backend\mvnw.cmd

```
<# : batch portion
@REM ----------------------------------------------------------------------------
@REM Licensed to the Apache Software Foundation (ASF) under one
@REM or more contributor license agreements.  See the NOTICE file
@REM distributed with this work for additional information
@REM regarding copyright ownership.  The ASF licenses this file
@REM to you under the Apache License, Version 2.0 (the
@REM "License"); you may not use this file except in compliance
@REM with the License.  You may obtain a copy of the License at
@REM
@REM    http://www.apache.org/licenses/LICENSE-2.0
@REM
@REM Unless required by applicable law or agreed to in writing,
@REM software distributed under the License is distributed on an
@REM "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
@REM KIND, either express or implied.  See the License for the
@REM specific language governing permissions and limitations
@REM under the License.
@REM ----------------------------------------------------------------------------

@REM ----------------------------------------------------------------------------
@REM Apache Maven Wrapper startup batch script, version 3.3.2
@REM
@REM Optional ENV vars
@REM   MVNW_REPOURL - repo url base for downloading maven distribution
@REM   MVNW_USERNAME/MVNW_PASSWORD - user and password for downloading maven
@REM   MVNW_VERBOSE - true: enable verbose log; others: silence the output
@REM ----------------------------------------------------------------------------

@IF "%__MVNW_ARG0_NAME__%"=="" (SET __MVNW_ARG0_NAME__=%~nx0)
@SET __MVNW_CMD__=
@SET __MVNW_ERROR__=
@SET __MVNW_PSMODULEP_SAVE=%PSModulePath%
@SET PSModulePath=
@FOR /F "usebackq tokens=1* delims==" %%A IN (`powershell -noprofile "& {$scriptDir='%~dp0'; $script='%__MVNW_ARG0_NAME__%'; icm -ScriptBlock ([Scriptblock]::Create((Get-Content -Raw '%~f0'))) -NoNewScope}"`) DO @(
  IF "%%A"=="MVN_CMD" (set __MVNW_CMD__=%%B) ELSE IF "%%B"=="" (echo %%A) ELSE (echo %%A=%%B)
)
@SET PSModulePath=%__MVNW_PSMODULEP_SAVE%
@SET __MVNW_PSMODULEP_SAVE=
@SET __MVNW_ARG0_NAME__=
@SET MVNW_USERNAME=
@SET MVNW_PASSWORD=
@IF NOT "%__MVNW_CMD__%"=="" (%__MVNW_CMD__% %*)
@echo Cannot start maven from wrapper >&2 && exit /b 1
@GOTO :EOF
: end batch / begin powershell #>

$ErrorActionPreference = "Stop"
if ($env:MVNW_VERBOSE -eq "true") {
  $VerbosePreference = "Continue"
}

# calculate distributionUrl, requires .mvn/wrapper/maven-wrapper.properties
$distributionUrl = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionUrl
if (!$distributionUrl) {
  Write-Error "cannot read distributionUrl property in $scriptDir/.mvn/wrapper/maven-wrapper.properties"
}

switch -wildcard -casesensitive ( $($distributionUrl -replace '^.*/','') ) {
  "maven-mvnd-*" {
    $USE_MVND = $true
    $distributionUrl = $distributionUrl -replace '-bin\.[^.]*$',"-windows-amd64.zip"
    $MVN_CMD = "mvnd.cmd"
    break
  }
  default {
    $USE_MVND = $false
    $MVN_CMD = $script -replace '^mvnw','mvn'
    break
  }
}

# apply MVNW_REPOURL and calculate MAVEN_HOME
# maven home pattern: ~/.m2/wrapper/dists/{apache-maven-<version>,maven-mvnd-<version>-<platform>}/<hash>
if ($env:MVNW_REPOURL) {
  $MVNW_REPO_PATTERN = if ($USE_MVND) { "/org/apache/maven/" } else { "/maven/mvnd/" }
  $distributionUrl = "$env:MVNW_REPOURL$MVNW_REPO_PATTERN$($distributionUrl -replace '^.*'+$MVNW_REPO_PATTERN,'')"
}
$distributionUrlName = $distributionUrl -replace '^.*/',''
$distributionUrlNameMain = $distributionUrlName -replace '\.[^.]*$','' -replace '-bin$',''
$MAVEN_HOME_PARENT = "$HOME/.m2/wrapper/dists/$distributionUrlNameMain"
if ($env:MAVEN_USER_HOME) {
  $MAVEN_HOME_PARENT = "$env:MAVEN_USER_HOME/wrapper/dists/$distributionUrlNameMain"
}
$MAVEN_HOME_NAME = ([System.Security.Cryptography.MD5]::Create().ComputeHash([byte[]][char[]]$distributionUrl) | ForEach-Object {$_.ToString("x2")}) -join ''
$MAVEN_HOME = "$MAVEN_HOME_PARENT/$MAVEN_HOME_NAME"

if (Test-Path -Path "$MAVEN_HOME" -PathType Container) {
  Write-Verbose "found existing MAVEN_HOME at $MAVEN_HOME"
  Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
  exit $?
}

if (! $distributionUrlNameMain -or ($distributionUrlName -eq $distributionUrlNameMain)) {
  Write-Error "distributionUrl is not valid, must end with *-bin.zip, but found $distributionUrl"
}

# prepare tmp dir
$TMP_DOWNLOAD_DIR_HOLDER = New-TemporaryFile
$TMP_DOWNLOAD_DIR = New-Item -Itemtype Directory -Path "$TMP_DOWNLOAD_DIR_HOLDER.dir"
$TMP_DOWNLOAD_DIR_HOLDER.Delete() | Out-Null
trap {
  if ($TMP_DOWNLOAD_DIR.Exists) {
    try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
    catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
  }
}

New-Item -Itemtype Directory -Path "$MAVEN_HOME_PARENT" -Force | Out-Null

# Download and Install Apache Maven
Write-Verbose "Couldn't find MAVEN_HOME, downloading and installing it ..."
Write-Verbose "Downloading from: $distributionUrl"
Write-Verbose "Downloading to: $TMP_DOWNLOAD_DIR/$distributionUrlName"

$webclient = New-Object System.Net.WebClient
if ($env:MVNW_USERNAME -and $env:MVNW_PASSWORD) {
  $webclient.Credentials = New-Object System.Net.NetworkCredential($env:MVNW_USERNAME, $env:MVNW_PASSWORD)
}
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12
$webclient.DownloadFile($distributionUrl, "$TMP_DOWNLOAD_DIR/$distributionUrlName") | Out-Null

# If specified, validate the SHA-256 sum of the Maven distribution zip file
$distributionSha256Sum = (Get-Content -Raw "$scriptDir/.mvn/wrapper/maven-wrapper.properties" | ConvertFrom-StringData).distributionSha256Sum
if ($distributionSha256Sum) {
  if ($USE_MVND) {
    Write-Error "Checksum validation is not supported for maven-mvnd. `nPlease disable validation by removing 'distributionSha256Sum' from your maven-wrapper.properties."
  }
  Import-Module $PSHOME\Modules\Microsoft.PowerShell.Utility -Function Get-FileHash
  if ((Get-FileHash "$TMP_DOWNLOAD_DIR/$distributionUrlName" -Algorithm SHA256).Hash.ToLower() -ne $distributionSha256Sum) {
    Write-Error "Error: Failed to validate Maven distribution SHA-256, your Maven distribution might be compromised. If you updated your Maven version, you need to update the specified distributionSha256Sum property."
  }
}

# unzip and move
Expand-Archive "$TMP_DOWNLOAD_DIR/$distributionUrlName" -DestinationPath "$TMP_DOWNLOAD_DIR" | Out-Null
Rename-Item -Path "$TMP_DOWNLOAD_DIR/$distributionUrlNameMain" -NewName $MAVEN_HOME_NAME | Out-Null
try {
  Move-Item -Path "$TMP_DOWNLOAD_DIR/$MAVEN_HOME_NAME" -Destination $MAVEN_HOME_PARENT | Out-Null
} catch {
  if (! (Test-Path -Path "$MAVEN_HOME" -PathType Container)) {
    Write-Error "fail to move MAVEN_HOME"
  }
} finally {
  try { Remove-Item $TMP_DOWNLOAD_DIR -Recurse -Force | Out-Null }
  catch { Write-Warning "Cannot remove $TMP_DOWNLOAD_DIR" }
}

Write-Output "MVN_CMD=$MAVEN_HOME/bin/$MVN_CMD"
```

# backend\pom.xml

```
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
	xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd">
	<modelVersion>4.0.0</modelVersion>
	<parent>
		<groupId>org.springframework.boot</groupId>
		<artifactId>spring-boot-starter-parent</artifactId>
		<version>3.5.0</version>
		<relativePath/> <!-- lookup parent from repository -->
	</parent>
	<groupId>com.movie_back</groupId>
	<artifactId>backend</artifactId>
	<version>0.0.1-SNAPSHOT</version>
	<name>backend</name>
	<description>Demo project for Spring Boot</description>
	<url/>
	<licenses>
		<license/>
	</licenses>
	<developers>
		<developer/>
	</developers>
	<scm>
		<connection/>
		<developerConnection/>
		<tag/>
		<url/>
	</scm>
	<properties>
		<java.version>17</java.version>
	</properties>
	<dependencies>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-actuator</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-web</artifactId>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-starter-test</artifactId>
			<scope>test</scope>
		</dependency>

		<dependency>
			<groupId>org.springframework.boot</groupId>
			<artifactId>spring-boot-devtools</artifactId>
			<optional>true</optional>
		</dependency>
		<dependency>
			<groupId>org.springdoc</groupId>
			<artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
			<version>2.5.0</version>
		</dependency>
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
		<dependency>
			<groupId>com.mysql</groupId>
			<artifactId>mysql-connector-j</artifactId>
			<scope>runtime</scope>
		</dependency>
		<dependency>
			<groupId>org.projectlombok</groupId>
			<artifactId>lombok</artifactId>
			<version>1.18.32</version>
			<optional>true</optional>
		</dependency>
		<dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
		<dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.11.5</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.11.5</version>
            <scope>runtime</scope>
        </dependency>
	</dependencies>

	<build>
		<plugins>
			<plugin>
				<groupId>org.springframework.boot</groupId>
				<artifactId>spring-boot-maven-plugin</artifactId>
			</plugin>
		</plugins>
	</build>

</project>
```

# backend\README.md

```
实验要求

# 1.需求分析
模仿某些电影影评系统，如豆瓣，设计一个电影影评系统。
该系统应该包含电影、演员、导演的如下基本信息：
## （1）电影 (Movies)
o电影ID (MovieID, PK)
o电影名称 (Title)
o发行年份 (ReleaseYear)
o电影时长 (Duration)
o类型/流派 (Genre)
o语言 (Language)
o国家/地区 (Country)
o简介 (Synopsis)
o评分
## (2)演员 (Actors)
o演员ID (ActorID, PK)
o姓名 (Name)
o性别(Gender)
o出生日期 (BirthDate)
o国籍 (Nationality)
## （3）导演 (Directors)
o导演ID (DirectorID, PK)
o姓名 (Name)
o性别(Gender)
o出生日期 (BirthDate)
o国籍 (Nationality)
该系统还应包含：①注册用户的基本信息；②演员参演电影的信息；③导演电影的信息（一部电影可能不止一个导演，一个导演会导演很多电影）；④用户可以对电影发表评论、给电影打分，所以还应该有评论信息、打分信息。

# 2.系统功能要求
（1）可以维护电影、导演、演员、用户的基本信息；
（2）每部电影都应该有演员和导演；
（3）用户可以对电影打分、发表评论；
（4）用户可以维护自己的打分和评论信息；
（5）用户评分后，对应电影的总评分要自动发生变化；
（6）可以按照电影的发行年月、类型/流派、评分等信息进行组合查询和排序；
（7）可以按照演员、导演的相关信息进行查询；
（8）给出热门电影推荐信息（同学们可以自行对“热门”进行定义）；
（9）届面应该有电影、演员的图形化显示。
说明：同学们还可以自行设计系统的功能。

# 3、数据库对象及完整性设计
（1）基本表要有主健，表和表之间的关系要设置外键；
（2）设置约束， 比如性别的默认值；
（3）设定缺省约束，如电影评分的默认值为0 。
（4）设置非空约束，如电影名、演员姓名等。
（5）设置触发器，当某电影增加了一个评分，该电影的评分自动发生变化；
（6）设计存储过程，以电影名为输入参数，查询该电影的所有评论信息；
（7）设计存储过程，以演员姓名为输入参数，查询该演员参演的所有电影；
（8）查询时自行设计视图。
# 4、请按照数据库设计过程完成数据库设计。
# 5、应用程序设计
 （1）本课程设计，数据库部分和应用程序部分各占50%。
  (2)应用程序开发工具任选，DBMS用MySQL。应用系统的用户交互要尽可能友好，功能尽可能完善。
```

# backend\.mvn\wrapper\maven-wrapper.properties

```
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.
wrapperVersion=3.3.2
distributionType=only-script
distributionUrl=https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.9/apache-maven-3.9.9-bin.zip
```

# backend\src\main\java\com\movie_back\backend\BackendApplication.java

```
package com.movie_back.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(BackendApplication.class, args);
	}

}
```

# backend\src\main\java\com\movie_back\backend\config\DataInitializer.java

```
package com.movie_back.backend.config;

import com.movie_back.backend.entity.Role;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {

        // ↓↓↓ 新增逻辑：如果 admin 用户已存在，则删除它 ↓↓↓
        userRepository.findByUsername("admin").ifPresent(userRepository::delete);

        // 创建一个管理员用户（如果不存在）
        if (userRepository.findByUsername("admin").isEmpty()) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@movie-time.com");
            admin.setRole(Role.ROLE_ADMIN);
            admin.setProfileImageUrl("https://i.ibb.co/0jFjF5bB/catcat.jpg");
            userRepository.save(admin);
            System.out.println("Created ADMIN user: admin");
        }

        // ↓↓↓ 新增逻辑：如果 user 用户已存在，则删除它 ↓↓↓
        userRepository.findByUsername("user").ifPresent(userRepository::delete);

        // 创建一个普通用户（如果不存在）
        if (userRepository.findByUsername("user").isEmpty()) {
            User regularUser = new User();
            regularUser.setUsername("user");
            regularUser.setPassword(passwordEncoder.encode("user123"));
            regularUser.setEmail("user@movie-time.com");
            regularUser.setRole(Role.ROLE_USER);
            regularUser.setProfileImageUrl("https://i.ibb.co/9kR8CgCw/catcat.jpg");
            userRepository.save(regularUser);
            System.out.println("Created USER user: user");
        }
    }
}
```

# backend\src\main\java\com\movie_back\backend\config\OpenApiConfig.java

```
package com.movie_back.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;

@Configuration // 配置类
public class OpenApiConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("影评项目 API") // 对应你之前的 title
						.description("所有 API 接口") // 对应你之前的 description
						.version("v1.0.0") // 建议添加一个版本号
						.license(new License().name("Apache 2.0").url("http://springdoc.org")));
	}
}
```

# backend\src\main\java\com\movie_back\backend\config\SecurityConfig.java

```
// backend/src/main/java/com/movie_back/backend/config/SecurityConfig.java

package com.movie_back.backend.config;

import com.movie_back.backend.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // 确保导入 HttpMethod
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 确保这里的端口号和您前端应用的端口号一致
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:8080", "http://localhost:8081"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // ↓↓↓ 新增：明确允许所有OPTIONS预检请求 ↓↓↓
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        // 公开访问的端点
                        .requestMatchers("/api/auth/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // .requestMatchers("/images/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/movies/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/actors/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/directors/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/movies", "/api/actors", "/api/directors")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/movies/**", "/api/actors/**", "/api/directors/**")
                        .hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/movies/**", "/api/actors/**", "/api/directors/**")
                        .hasRole("ADMIN")
                        // 其他所有请求都需要认证
                        .anyRequest().authenticated())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\ActorController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.actor.ActorDTO;
import com.movie_back.backend.service.ActorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/actors")
@RequiredArgsConstructor
public class ActorController {

    private final ActorService actorService;

    // 创建演员 - 仅限管理员
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDTO> createActor(@Valid @RequestBody ActorDTO actorDTO) {
        return new ResponseEntity<>(actorService.createActor(actorDTO), HttpStatus.CREATED);
    }

    // 获取所有演员列表 - 公开
    @GetMapping
    public ResponseEntity<List<ActorDTO>> getAllActors() {
        return ResponseEntity.ok(actorService.getAllActors());
    }

    // 获取单个演员信息 - 公开
    @GetMapping("/{id}")
    public ResponseEntity<ActorDTO> getActorById(@PathVariable Long id) {
        return ResponseEntity.ok(actorService.getActorById(id));
    }

    // 更新演员信息 - 仅限管理员
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ActorDTO> updateActor(@PathVariable Long id, @Valid @RequestBody ActorDTO actorDTO) {
        return ResponseEntity.ok(actorService.updateActor(id, actorDTO));
    }

    // 删除演员 - 仅限管理员
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteActor(@PathVariable Long id) {
        actorService.deleteActor(id);
        return ResponseEntity.noContent().build();
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\AuthController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.auth.AuthRequest;
import com.movie_back.backend.dto.auth.AuthResponse;
import com.movie_back.backend.dto.user.UserDTO;
import com.movie_back.backend.dto.user.UserRegistrationRequest;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.security.JwtUtil;
import com.movie_back.backend.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    // 用户登录接口
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody AuthRequest request) {
        // 使用 AuthenticationManager 进行用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
        // 认证成功后，获取用户信息
        User user = (User) authentication.getPrincipal();
        // 生成JWT
        String token = jwtUtil.generateToken(user);

        // 返回JWT和一些用户信息
        AuthResponse response = new AuthResponse(
                token,
                user.getId(),
                user.getUsername(),
                user.getRole().name(),
                user.getProfileImageUrl(),
                user.getEmail(),
                user.getPersonalWebsite(),
                user.getBirthDate(),
                user.getBio());
        return ResponseEntity.ok(response);
    }

    // 用户注册接口
    @PostMapping("/register")
    public ResponseEntity<UserDTO> register(@Valid @RequestBody UserRegistrationRequest request) {
        UserDTO newUser = userService.registerUser(request);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\DirectorController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.director.DirectorDTO;
import com.movie_back.backend.service.DirectorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/directors")
@RequiredArgsConstructor
public class DirectorController {

    private final DirectorService directorService;

    // 创建导演 - 仅限管理员
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDTO> createDirector(@Valid @RequestBody DirectorDTO directorDTO) {
        return new ResponseEntity<>(directorService.createDirector(directorDTO), HttpStatus.CREATED);
    }

    // 获取所有导演列表 - 公开
    @GetMapping
    public ResponseEntity<List<DirectorDTO>> getAllDirectors() {
        return ResponseEntity.ok(directorService.getAllDirectors());
    }

    // 获取单个导演信息 - 公开
    @GetMapping("/{id}")
    public ResponseEntity<DirectorDTO> getDirectorById(@PathVariable Long id) {
        return ResponseEntity.ok(directorService.getDirectorById(id));
    }

    // 更新导演信息 - 仅限管理员
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<DirectorDTO> updateDirector(@PathVariable Long id,
            @Valid @RequestBody DirectorDTO directorDTO) {
        return ResponseEntity.ok(directorService.updateDirector(id, directorDTO));
    }

    // 删除导演 - 仅限管理员
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteDirector(@PathVariable Long id) {
        directorService.deleteDirector(id);
        return ResponseEntity.noContent().build();
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\MovieController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.movie.CreateMovieRequest;
import com.movie_back.backend.dto.movie.MovieDTO;
import com.movie_back.backend.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/movies")
@RequiredArgsConstructor
public class MovieController {
    private final MovieService movieService;

    // 创建电影 - 仅限管理员
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> createMovie(@Valid @RequestBody CreateMovieRequest movieRequest) {
        return new ResponseEntity<>(movieService.createMovie(movieRequest), HttpStatus.CREATED);
    }

    // 更新电影 - 仅限管理员
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDTO> updateMovie(@PathVariable Long id,
            @Valid @RequestBody CreateMovieRequest movieRequest) {
        return ResponseEntity.ok(movieService.updateMovie(id, movieRequest));
    }

    // 删除电影 - 仅限管理员
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteMovie(@PathVariable Long id) {
        movieService.deleteMovie(id);
        return ResponseEntity.noContent().build();
    }

    // 获取单个电影详情 - 公开访问
    @GetMapping("/{id}")
    public ResponseEntity<MovieDTO> getMovieById(@PathVariable Long id) {
        MovieDTO movie = movieService.getMovieById(id);
        return ResponseEntity.ok(movie);
    }

    // 组合查询电影 - 公开访问
    @GetMapping("/search")
    public ResponseEntity<Page<MovieDTO>> searchMovies(
            @RequestParam(required = false) Integer releaseYear,
            @RequestParam(required = false) String genre,
            @RequestParam(required = false) String country,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "title") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<MovieDTO> movies = movieService.searchMovies(releaseYear, genre, country, minRating, sortBy, sortDir, page,
                size);
        return ResponseEntity.ok(movies);
    }

    // 获取热门电影 - 公开访问
    @GetMapping("/hot")
    public ResponseEntity<List<MovieDTO>> getHotMovies(@RequestParam(defaultValue = "10") int limit) {
        List<MovieDTO> hotMovies = movieService.getHotMovies(limit);
        return ResponseEntity.ok(hotMovies);
    }

    // 根据演员姓名查找电影 - 公开访问
    @GetMapping("/by-actor")
    public ResponseEntity<List<MovieDTO>> getMoviesByActorName(@RequestParam String name) {
        List<MovieDTO> movies = movieService.getMoviesByActorName(name);
        return ResponseEntity.ok(movies);
    }

    // 根据导演姓名查找电影 - 公开访问
    @GetMapping("/by-director")
    public ResponseEntity<List<MovieDTO>> getMoviesByDirectorName(@RequestParam String name) {
        List<MovieDTO> movies = movieService.getMoviesByDirectorName(name);
        return ResponseEntity.ok(movies);
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\ReviewController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.service.ReviewService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    /**
     * 为电影添加评论的端点。
     * 为简化起见，userId 作为请求参数传递。在真实世界的应用中，
     * 这通常应从安全上下文（例如，已登录用户）中获取。
     *
     * @param movieId       要评论的电影 ID。
     * @param userId        提交评论的用户 ID。
     * @param reviewRequest 评论的内容。
     * @return 创建的评论。
     */
    @PostMapping("/movies/{movieId}/reviews")
    public ResponseEntity<ReviewDTO> addReviewToMovie(
            @PathVariable Long movieId,
            @RequestParam Long userId, // 简化处理：在真实应用中，从安全上下文中获取
            @Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewDTO createdReview = reviewService.addReview(movieId, userId, reviewRequest);
        return new ResponseEntity<>(createdReview, HttpStatus.CREATED);
    }

    /**
     * 获取特定电影所有评论的端点。
     * 
     * @param movieId 电影的 ID。
     * @return 评论列表。
     */
    @GetMapping("/movies/{movieId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsForMovie(@PathVariable Long movieId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForMovie(movieId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 获取特定用户所有评论的端点。
     * 
     * @param userId 用户的 ID。
     * @return 评论列表。
     */
    @GetMapping("/users/{userId}/reviews")
    public ResponseEntity<List<ReviewDTO>> getReviewsByUser(@PathVariable Long userId) {
        List<ReviewDTO> reviews = reviewService.getReviewsForUser(userId);
        return ResponseEntity.ok(reviews);
    }

    /**
     * 删除评论的端点。
     * 
     * @param reviewId 要删除的评论 ID。
     * @return 成功删除后返回无内容的响应。
     */
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 更新评论的端点。
     *
     * @param reviewId      要更新的评论 ID。
     * @param reviewRequest 评论的新内容。
     * @return 成功更新后返回更新的评论。
     */
    @PutMapping("/reviews/{reviewId}")
    public ResponseEntity<ReviewDTO> updateReview(
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest reviewRequest) {
        ReviewDTO updatedReview = reviewService.updateReview(reviewId, reviewRequest);
        return ResponseEntity.ok(updatedReview);
    }

}
```

# backend\src\main\java\com\movie_back\backend\controller\UserController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.user.UserDTO;
import com.movie_back.backend.dto.user.UserProfileUpdateDTO;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 获取当前登录用户的信息
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()") // 确保用户已登录
    public ResponseEntity<UserDTO> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        // @AuthenticationPrincipal可以直接注入当前登录的User对象
        UserDTO userDTO = userService.findUserById(currentUser.getId());
        return ResponseEntity.ok(userDTO);
    }

    // 根据ID获取用户信息 - 仅限管理员
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserDTO userDTO = userService.findUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    // 新增：更新当前登录用户的个人资料
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDTO> updateCurrentUserProfile(
            @AuthenticationPrincipal User currentUser,
            @RequestBody UserProfileUpdateDTO profileData) {
        UserDTO updatedUser = userService.updateUserProfile(currentUser.getId(), profileData);
        return ResponseEntity.ok(updatedUser);
    }
}
```

# backend\src\main\java\com\movie_back\backend\controller\UserRatingController.java

```
package com.movie_back.backend.controller;

import com.movie_back.backend.dto.userRating.UserRatingDTO;
import com.movie_back.backend.dto.userRating.UserRatingRequest;
import com.movie_back.backend.service.UserRatingService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/movies/{movieId}/ratings")
public class UserRatingController {

    private final UserRatingService userRatingService;

    public UserRatingController(UserRatingService userRatingService) {
        this.userRatingService = userRatingService;
    }

    /**
     * 用户为电影添加或更新评分的端点。
     * 为简化起见，userId 作为请求参数传递。在真实应用中，
     * 这应该从已认证用户的安全主体中获取。
     *
     * @param movieId 待评分的电影 ID。
     * @param userId  评分用户的 ID。
     * @param request 评分分数。
     * @return 创建或更新后评分的 DTO。
     */
    @PostMapping
    public ResponseEntity<UserRatingDTO> addOrUpdateRating(
            @PathVariable Long movieId,
            @RequestParam Long userId, // 暂时简化处理
            @Valid @RequestBody UserRatingRequest request) {
        UserRatingDTO ratingDTO = userRatingService.addOrUpdateRating(movieId, userId, request);
        return ResponseEntity.ok(ratingDTO);
    }
}
```

# backend\src\main\java\com\movie_back\backend\dto\actor\ActorDTO.java

```
package com.movie_back.backend.dto.actor;

import lombok.Data;
import java.time.LocalDate;

@Data
public class ActorDTO {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String nationality;
    private String profileImageUrl;
}
```

# backend\src\main\java\com\movie_back\backend\dto\auth\AuthRequest.java

```
package com.movie_back.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

// 登录请求的数据传输对象
@Data
public class AuthRequest {
    @NotBlank
    private String username;

    @NotBlank
    private String password;
}
```

# backend\src\main\java\com\movie_back\backend\dto\auth\AuthResponse.java

```
package com.movie_back.backend.dto.auth;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;

// 登录成功后返回的数据传输对象
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private Long userId;
    private String username;
    private String role;
    private String profileImageUrl;
    private String email; // 新增
    private String personalWebsite; // 新增
    private LocalDate birthDate; // 新增
    private String bio; // 新增
}
```

# backend\src\main\java\com\movie_back\backend\dto\director\DirectorDTO.java

```
package com.movie_back.backend.dto.director;

import lombok.Data;
import java.time.LocalDate;

@Data
public class DirectorDTO {
    private Long id;
    private String name;
    private String gender;
    private LocalDate birthDate;
    private String nationality;
    private String profileImageUrl;
}
```

# backend\src\main\java\com\movie_back\backend\dto\movie\CreateMovieRequest.java

```
package com.movie_back.backend.dto.movie;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.Set;

@Data
public class CreateMovieRequest {
    @NotBlank
    private String title;
    @NotNull
    private Integer releaseYear;
    private Integer duration;
    private String genre;
    private String language;
    private String country;
    private String synopsis;
    private String posterUrl;
    private Set<Long> actorIds;
    private Set<Long> directorIds;
}
```

# backend\src\main\java\com\movie_back\backend\dto\movie\MovieDTO.java

```
package com.movie_back.backend.dto.movie;

import lombok.Data;
import java.util.Set;

@Data
public class MovieDTO {
    private Long id;
    private String title;
    private Integer releaseYear;
    private Integer duration;
    private String genre;
    private String language;
    private String country;
    private String synopsis;
    private Double averageRating;
    private String posterUrl;
    private Set<Long> actorIds; // 用于创建/更新时关联演员
    private Set<String> actorNames; // 用于展示
    private Set<Long> directorIds; // 用于创建/更新时关联导演
    private Set<String> directorNames; // 用于展示
}
```

# backend\src\main\java\com\movie_back\backend\dto\review\ReviewDTO.java

```
package com.movie_back.backend.dto.review;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ReviewDTO { // 用于展示
    private Long id;
    private Long movieId;
    private String movieTitle;
    private Long userId;
    private String username;
    private String commentText;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

# backend\src\main\java\com\movie_back\backend\dto\review\ReviewRequest.java

```
package com.movie_back.backend.dto.review;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class ReviewRequest { // 用于创建和更新
    @NotBlank
    private String commentText;
}
```

# backend\src\main\java\com\movie_back\backend\dto\user\UserDTO.java

```
package com.movie_back.backend.dto.user;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String username;
    private String email;
}
```

# backend\src\main\java\com\movie_back\backend\dto\user\UserProfileUpdateDTO.java

```
package com.movie_back.backend.dto.user;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileUpdateDTO {
    private String username;
    private String personalWebsite;
    private LocalDate birthDate;
    private String bio;
    // 注意：密码和头像将通过单独的接口处理
    private String profileImageUrl; // 用于接收前端传来的图床URL
}
```

# backend\src\main\java\com\movie_back\backend\dto\user\UserRegistrationRequest.java

```
package com.movie_back.backend.dto.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserRegistrationRequest {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;
    @NotBlank
    @Size(min = 6, max = 100)
    private String password;
    @NotBlank
    @Email
    private String email;
}
```

# backend\src\main\java\com\movie_back\backend\dto\userRating\UserRatingDTO.java

```
package com.movie_back.backend.dto.userRating;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class UserRatingDTO { // 用于展示
    private Long id;
    private Long movieId;
    private Long userId;
    private Integer score;
    private LocalDateTime ratedAt;
}
```

# backend\src\main\java\com\movie_back\backend\dto\userRating\UserRatingRequest.java

```
package com.movie_back.backend.dto.userRating;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class UserRatingRequest { // 用于创建和更新
    @NotNull
    @Min(1)
    @Max(10) // 假设1-10分制
    private Integer score;
}
```

# backend\src\main\java\com\movie_back\backend\entity\Actor.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "actors")
@Data
@EqualsAndHashCode(exclude = "moviesActedIn")
@ToString(exclude = "moviesActedIn")
public class Actor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // ActorID

    @Column(nullable = false)
    private String name; // 姓名

    private String gender; // 性别 (如: "Male", "Female", "Other")

    private LocalDate birthDate; // 出生日期

    private String nationality; // 国籍

    private String profileImageUrl; // 演员图片URL

    @ManyToMany(mappedBy = "cast", fetch = FetchType.LAZY)
    private Set<Movie> moviesActedIn = new HashSet<>();
}
```

# backend\src\main\java\com\movie_back\backend\entity\Director.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "directors")
@Data
@EqualsAndHashCode(exclude = "moviesDirected")
@ToString(exclude = "moviesDirected")
public class Director {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // DirectorID

    @Column(nullable = false)
    private String name; // 姓名

    private String gender; // 性别

    private LocalDate birthDate; // 出生日期

    private String nationality; // 国籍

    private String profileImageUrl; // 导演图片URL

    @ManyToMany(mappedBy = "directors", fetch = FetchType.LAZY)
    private Set<Movie> moviesDirected = new HashSet<>();
}
```

# backend\src\main\java\com\movie_back\backend\entity\Movie.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "movies")
@Data
@EqualsAndHashCode(exclude = { "cast", "directors", "reviews", "userRatings" }) // 避免循环引用导致的hashCode和equals问题
@ToString(exclude = { "cast", "directors", "reviews", "userRatings" }) // 避免循环引用导致的toString问题
public class Movie {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // MovieID

    @Column(nullable = false)
    private String title; // 电影名称

    private Integer releaseYear; // 发行年份

    private Integer duration; // 电影时长(min)

    private String genre; // 类型/流派

    private String language; // 语言

    private String country; // 国家/地区

    @Lob
    private String synopsis; // 简介

    @Column
    private Double averageRating = 0.0; // 评分 (由UserRating计算得出)

    private String posterUrl; // 海报图片URL (用于图形化显示)

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "movie_actors", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "actor_id"))
    private Set<Actor> cast = new HashSet<>(); // 参演演员

    @ManyToMany(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "movie_directors", joinColumns = @JoinColumn(name = "movie_id"), inverseJoinColumns = @JoinColumn(name = "director_id"))
    private Set<Director> directors = new HashSet<>(); // 导演

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "movie", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRating> userRatings = new HashSet<>();
}
```

# backend\src\main\java\com\movie_back\backend\entity\Review.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
@Data
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Lob
    @Column(nullable = false)
    private String commentText;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
```

# backend\src\main\java\com\movie_back\backend\entity\Role.java

```
package com.movie_back.backend.entity;

// 定义角色枚举
public enum Role {
    ROLE_USER, // 普通用户
    ROLE_ADMIN // 管理员
}
```

# backend\src\main\java\com\movie_back\backend\entity\User.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "users") // 'user' is often a reserved keyword
@Data
@EqualsAndHashCode(exclude = { "reviews", "userRatings" })
@ToString(exclude = { "reviews", "userRatings" })
// 实现 UserDetails 接口，以便 Spring Security 进行集成
public class User implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password; // 实际项目中应加密存储

    @Column(nullable = false, unique = true)
    private String email;

    // 新增：用户头像URL
    private String profileImageUrl;

    // 新增：用户角色字段
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    private LocalDateTime createdAt;

    @Column // 个人网站
    private String personalWebsite;

    @Column // 生日
    private LocalDate birthDate;

    @Lob // 自我介绍/签名
    private String bio;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<UserRating> userRatings = new HashSet<>();

    // 以下为实现 UserDetails 接口所需要的方法
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // 返回用户的角色权限集合
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        // 账户是否未过期
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        // 账户是否未锁定
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        // 凭证是否未过期
        return true;
    }

    @Override
    public boolean isEnabled() {
        // 账户是否启用
        return true;
    }
}
```

# backend\src\main\java\com\movie_back\backend\entity\UserRating.java

```
package com.movie_back.backend.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_ratings", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "user_id", "movie_id" }) // 用户对同一电影只能评分一次
})
@Data
public class UserRating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Integer score; // 例如 1-10 或 1-5

    private LocalDateTime ratedAt;

    @PrePersist
    protected void onRate() {
        ratedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdateRate() {
        ratedAt = LocalDateTime.now();
    }
}
```

# backend\src\main\java\com\movie_back\backend\exception\ResourceNotFoundException.java

```
package com.movie_back.backend.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// @ResponseStatus 注解会让 Spring 在抛出此异常时自动返回 404 NOT_FOUND 状态码
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException { // 通常继承自 RuntimeException

    public ResourceNotFoundException(String message) {
        super(message);
    }

    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
```

# backend\src\main\java\com\movie_back\backend\repository\ActorRepository.java

```
package com.movie_back.backend.repository;

// import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie_back.backend.entity.Actor;

public interface ActorRepository extends JpaRepository<Actor, Long> {
    // List<Actor> findById(String id);
}
```

# backend\src\main\java\com\movie_back\backend\repository\DirectorRepository.java

```
package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.movie_back.backend.entity.Director;

public interface DirectorRepository extends JpaRepository<Director, Long> {
}
```

# backend\src\main\java\com\movie_back\backend\repository\MovieRepository.java

```
package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor; // 用于复杂查询
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.movie_back.backend.entity.Movie;

import java.util.List;

public interface MovieRepository extends JpaRepository<Movie, Long>, JpaSpecificationExecutor<Movie> {
    // 可以按照电影的发行年月、类型/流派、评分等信息进行组合查询和排序 (使用Specification API)

    // 示例：按类型查找
    List<Movie> findByGenreContainingIgnoreCase(String genre);

    // 示例：查找某演员参演的电影
    @Query("SELECT m FROM Movie m JOIN m.cast a WHERE a.id = :actorId")
    List<Movie> findMoviesByActorId(@Param("actorId") Long actorId);

    // 示例：查找某导演执导的电影
    @Query("SELECT m FROM Movie m JOIN m.directors d WHERE d.id = :directorId")
    List<Movie> findMoviesByDirectorId(@Param("directorId") Long directorId);

    // 热门电影推荐 (示例：按平均分降序，取前N个)
    @Query("SELECT m FROM Movie m ORDER BY m.averageRating DESC")
    List<Movie> findTopRatedMovies(org.springframework.data.domain.Pageable pageable);

    // 示例：根据演员姓名模糊查找电影
    @Query("SELECT m FROM Movie m JOIN m.cast a WHERE a.name LIKE %:actorName%")
    List<Movie> findMoviesByActorNameContaining(@Param("actorName") String actorName);

    // 示例：根据导演姓名模糊查找电影
    @Query("SELECT m FROM Movie m JOIN m.directors d WHERE d.name LIKE %:directorName%")
    List<Movie> findMoviesByDirectorNameContaining(@Param("directorName") String directorName);
}
```

# backend\src\main\java\com\movie_back\backend\repository\ReviewRepository.java

```
package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.movie_back.backend.entity.Review;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByMovieId(Long movieId);
    List<Review> findByUserId(Long userId);
}
```

# backend\src\main\java\com\movie_back\backend\repository\UserRatingRepository.java

```
package com.movie_back.backend.repository;

import com.movie_back.backend.entity.UserRating;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRatingRepository extends JpaRepository<UserRating, Long> {

    /**
     * 查找特定用户对特定电影的评分。
     * 这对于检查用户是否已对某电影评分很有用。
     * 
     * @param movieId 电影的 ID。
     * @param userId  用户的 ID。
     * @return 一个包含 UserRating 的 Optional 对象（如果存在）。
     */
    Optional<UserRating> findByMovieIdAndUserId(Long movieId, Long userId);

    /**
     * 计算给定电影的平均分。
     * 
     * @param movieId 电影的 ID。
     * @return 平均分；如果没有评分，则返回 null。
     */
    @Query("SELECT AVG(ur.score) FROM UserRating ur WHERE ur.movie.id = :movieId")
    Double getAverageRatingForMovie(Long movieId);
}
```

# backend\src\main\java\com\movie_back\backend\repository\UserRepository.java

```
package com.movie_back.backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.movie_back.backend.entity.User;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);
}
```

# backend\src\main\java\com\movie_back\backend\security\JwtAuthenticationFilter.java

```
package com.movie_back.backend.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor // 使用Lombok自动注入final字段
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain) throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;

        // 如果请求头中没有 "Authorization" 或不以 "Bearer " 开头，则直接放行
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 提取JWT
        jwt = authHeader.substring(7);
        try {
            username = jwtUtil.extractUsername(jwt);
        } catch (Exception e) {
            // 如果JWT解析失败，则直接放行，后续的Security链会处理未认证状态
            filterChain.doFilter(request, response);
            return;
        }

        // 如果用户名不为空，并且当前安全上下文中没有认证信息
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // 从数据库加载用户信息
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 验证token是否有效
            if (jwtUtil.validateToken(jwt, (com.movie_back.backend.entity.User) userDetails)) {
                // 创建一个认证令牌
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 将认证令牌设置到安全上下文中
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        // 继续执行过滤器链
        filterChain.doFilter(request, response);
    }
}
```

# backend\src\main\java\com\movie_back\backend\security\JwtUtil.java

```
package com.movie_back.backend.security;

import com.movie_back.backend.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.function.Function;

@Component
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expiration;

    // 使用secret字符串生成一个安全的密钥
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // 从token中提取用户名
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    // 从token中提取过期时间
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // 泛型方法，用于从token中提取任何声明
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    // 解析token，获取所有声明
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // 检查token是否已过期
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // 为用户生成token
    public String generateToken(User user) {
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("role", user.getRole().name()) // 添加角色信息
                .claim("userId", user.getId())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // 验证token是否有效
    public Boolean validateToken(String token, User user) {
        final String username = extractUsername(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\ActorService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.actor.ActorDTO;
import com.movie_back.backend.entity.Actor;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.ActorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ActorService {

    private final ActorRepository actorRepository;

    @Transactional
    public ActorDTO createActor(ActorDTO actorDTO) {
        Actor actor = new Actor();
        actor.setName(actorDTO.getName());
        actor.setGender(actorDTO.getGender());
        actor.setBirthDate(actorDTO.getBirthDate());
        actor.setNationality(actorDTO.getNationality());
        actor.setProfileImageUrl(actorDTO.getProfileImageUrl());
        Actor savedActor = actorRepository.save(actor);
        return convertToDTO(savedActor);
    }

    @Transactional(readOnly = true)
    public ActorDTO getActorById(Long id) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));
        return convertToDTO(actor);
    }

    @Transactional(readOnly = true)
    public List<ActorDTO> getAllActors() {
        return actorRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public ActorDTO updateActor(Long id, ActorDTO actorDetails) {
        Actor actor = actorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Actor not found with id: " + id));

        actor.setName(actorDetails.getName());
        actor.setGender(actorDetails.getGender());
        actor.setBirthDate(actorDetails.getBirthDate());
        actor.setNationality(actorDetails.getNationality());
        actor.setProfileImageUrl(actorDetails.getProfileImageUrl());

        Actor updatedActor = actorRepository.save(actor);
        return convertToDTO(updatedActor);
    }

    @Transactional
    public void deleteActor(Long id) {
        if (!actorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Actor not found with id: " + id);
        }
        actorRepository.deleteById(id);
    }

    private ActorDTO convertToDTO(Actor actor) {
        ActorDTO dto = new ActorDTO();
        dto.setId(actor.getId());
        dto.setName(actor.getName());
        dto.setGender(actor.getGender());
        dto.setBirthDate(actor.getBirthDate());
        dto.setNationality(actor.getNationality());
        dto.setProfileImageUrl(actor.getProfileImageUrl());
        return dto;
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\DirectorService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.director.DirectorDTO;
import com.movie_back.backend.entity.Director;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.DirectorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DirectorService {

    private final DirectorRepository directorRepository;

    @Transactional
    public DirectorDTO createDirector(DirectorDTO directorDTO) {
        Director director = new Director();
        director.setName(directorDTO.getName());
        director.setGender(directorDTO.getGender());
        director.setBirthDate(directorDTO.getBirthDate());
        director.setNationality(directorDTO.getNationality());
        director.setProfileImageUrl(directorDTO.getProfileImageUrl());
        Director savedDirector = directorRepository.save(director);
        return convertToDTO(savedDirector);
    }

    @Transactional(readOnly = true)
    public DirectorDTO getDirectorById(Long id) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + id));
        return convertToDTO(director);
    }

    @Transactional(readOnly = true)
    public List<DirectorDTO> getAllDirectors() {
        return directorRepository.findAll().stream().map(this::convertToDTO).collect(Collectors.toList());
    }

    @Transactional
    public DirectorDTO updateDirector(Long id, DirectorDTO directorDetails) {
        Director director = directorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Director not found with id: " + id));
        director.setName(directorDetails.getName());
        director.setGender(directorDetails.getGender());
        director.setBirthDate(directorDetails.getBirthDate());
        director.setNationality(directorDetails.getNationality());
        director.setProfileImageUrl(directorDetails.getProfileImageUrl());
        Director updatedDirector = directorRepository.save(director);
        return convertToDTO(updatedDirector);
    }

    @Transactional
    public void deleteDirector(Long id) {
        if (!directorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Director not found with id: " + id);
        }
        directorRepository.deleteById(id);
    }

    private DirectorDTO convertToDTO(Director director) {
        DirectorDTO dto = new DirectorDTO();
        dto.setId(director.getId());
        dto.setName(director.getName());
        dto.setGender(director.getGender());
        dto.setBirthDate(director.getBirthDate());
        dto.setNationality(director.getNationality());
        dto.setProfileImageUrl(director.getProfileImageUrl());
        return dto;
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\MovieService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.movie.CreateMovieRequest;
import com.movie_back.backend.dto.movie.MovieDTO;
import com.movie_back.backend.entity.Actor;
import com.movie_back.backend.entity.Director;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.UserRating;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.ActorRepository;
import com.movie_back.backend.repository.DirectorRepository;
import com.movie_back.backend.repository.MovieRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // 使用 Lombok 自动注入 final 字段
public class MovieService {
    private final MovieRepository movieRepository;
    private final ActorRepository actorRepository;
    private final DirectorRepository directorRepository;

    @Transactional
    public MovieDTO createMovie(CreateMovieRequest request) {
        Movie movie = new Movie();
        // 设置基本属性
        setMoviePropertiesFromRequest(movie, request);
        Movie savedMovie = movieRepository.save(movie);
        return convertToMovieDTO(savedMovie);
    }

    // 根据ID获取电影
    @Transactional(readOnly = true)
    public MovieDTO getMovieById(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));
        return convertToMovieDTO(movie);
    }

    // 更新电影信息
    @Transactional
    public MovieDTO updateMovie(Long movieId, CreateMovieRequest request) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        // 更新属性
        setMoviePropertiesFromRequest(movie, request);
        Movie updatedMovie = movieRepository.save(movie);
        return convertToMovieDTO(updatedMovie);
    }

    // 删除电影
    @Transactional
    public void deleteMovie(Long movieId) {
        if (!movieRepository.existsById(movieId)) {
            throw new ResourceNotFoundException("Movie not found with id: " + movieId);
        }
        movieRepository.deleteById(movieId);
    }

    @Transactional
    public void updateMovieAverageRating(Long movieId) {
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("Movie not found with id: " + movieId));

        // 从 UserRating 集合中计算平均分
        double average = movie.getUserRatings().stream()
                .mapToInt(UserRating::getScore)
                .average()
                .orElse(0.0);

        // 保留一位小数
        movie.setAverageRating(Math.round(average * 10.0) / 10.0);
        movieRepository.save(movie);
    }

    // page为当前页号，size为每页包含的电影数量
    public Page<MovieDTO> searchMovies(Integer releaseYear,
            String genre, String country, Double minRating,
            String sortBy,
            String sortDir, int page, int size) {
        Sort.Direction direction = sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        // Specification接口在 JPA 中是用来构建动态查询条件的工具
        Specification<Movie> spec = Specification.where(null);

        if (releaseYear != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("releaseYear"), releaseYear));
        }

        if (genre != null && !genre.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.like(cb.lower(root.get("genre")), "%" + genre.toLowerCase() + "%"));
        }

        if (country != null && !country.isEmpty()) {
            spec = spec.and((root, query, cb) -> cb.equal(cb.lower(root.get("country")), country.toLowerCase()));
        }
        if (minRating != null) {
            spec = spec.and((root, query, cb) -> cb.greaterThanOrEqualTo(root.get("averageRating"), minRating));
        }

        Page<Movie> moviePage = movieRepository.findAll(spec, pageable);
        return moviePage.map(this::convertToMovieDTO);
    }

    public List<MovieDTO> getHotMovies(int limit) {
        // 按平均分降序，取前N个
        Pageable pageable = PageRequest.of(0, limit, Sort.by(Sort.Direction.DESC, "averageRating"));
        return movieRepository.findAll(pageable).stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByActorName(String actorName) {
        return movieRepository.findMoviesByActorNameContaining(actorName).stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    public List<MovieDTO> getMoviesByDirectorName(String directorName) {
        return movieRepository.findMoviesByDirectorNameContaining(directorName).stream()
                .map(this::convertToMovieDTO)
                .collect(Collectors.toList());
    }

    // 抽取的公共方法，用于从请求设置电影属性
    private void setMoviePropertiesFromRequest(Movie movie, CreateMovieRequest request) {
        movie.setTitle(request.getTitle());
        movie.setReleaseYear(request.getReleaseYear());
        movie.setDuration(request.getDuration());
        movie.setGenre(request.getGenre());
        movie.setLanguage(request.getLanguage());
        movie.setCountry(request.getCountry());
        movie.setSynopsis(request.getSynopsis());
        movie.setPosterUrl(request.getPosterUrl());

        if (request.getActorIds() != null && !request.getActorIds().isEmpty()) {
            List<Actor> actors = actorRepository.findAllById(request.getActorIds());
            movie.setCast(new HashSet<>(actors));
        } else {
            movie.getCast().clear();
        }

        if (request.getDirectorIds() != null && !request.getDirectorIds().isEmpty()) {
            List<Director> directors = directorRepository.findAllById(request.getDirectorIds());
            movie.setDirectors(new HashSet<>(directors));
        } else {
            movie.getDirectors().clear();
        }
    }

    private MovieDTO convertToMovieDTO(Movie movie) {
        MovieDTO dto = new MovieDTO();
        dto.setId(movie.getId());
        dto.setTitle(movie.getTitle());
        dto.setReleaseYear(movie.getReleaseYear());
        dto.setDuration(movie.getDuration());
        dto.setGenre(movie.getGenre());
        dto.setLanguage(movie.getLanguage());
        dto.setCountry(movie.getCountry());
        dto.setSynopsis(movie.getSynopsis());
        dto.setAverageRating(movie.getAverageRating());
        dto.setPosterUrl(movie.getPosterUrl());
        if (movie.getCast() != null) {
            dto.setActorNames(movie.getCast().stream().map(Actor::getName).collect(Collectors.toSet()));
            dto.setActorIds(movie.getCast().stream().map(Actor::getId).collect(Collectors.toSet()));
        }
        if (movie.getDirectors() != null) {
            dto.setDirectorNames(movie.getDirectors().stream().map(Director::getName).collect(Collectors.toSet()));
            dto.setDirectorIds(movie.getDirectors().stream().map(Director::getId).collect(Collectors.toSet()));
        }
        return dto;
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\ReviewService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.review.ReviewDTO;
import com.movie_back.backend.dto.review.ReviewRequest;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.Review;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.ReviewRepository;
import com.movie_back.backend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;

    public ReviewService(ReviewRepository reviewRepository, MovieRepository movieRepository,
            UserRepository userRepository) {
        this.reviewRepository = reviewRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
    }

    /**
     * 获取特定电影的所有评论。
     * 
     * @param movieId 电影的 ID。
     * @return 评论 DTO 的列表。
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForMovie(Long movieId) {
        List<Review> reviews = reviewRepository.findByMovieId(movieId);
        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * 获取特定用户的所有评论。
     * 
     * @param userId 用户的 ID。
     * @return 评论 DTO 的列表。
     */
    @Transactional(readOnly = true)
    public List<ReviewDTO> getReviewsForUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户");
        }
        List<Review> reviews = reviewRepository.findByUserId(userId);
        return reviews.stream()
                .map(this::convertToReviewDTO)
                .collect(Collectors.toList());
    }

    /**
     * 为电影添加一条新评论。
     * 
     * @param movieId       被评论的电影 ID。
     * @param userId        发表评论的用户 ID。
     * @param reviewRequest 评论内容。
     * @return 已创建评论的 DTO。
     */
    @Transactional
    public ReviewDTO addReview(Long movieId, Long userId, ReviewRequest reviewRequest) {
        // 从数据库中查找电影和用户实体。
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + movieId + " 的电影"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户"));

        Review review = new Review();
        review.setMovie(movie);
        review.setUser(user);
        review.setCommentText(reviewRequest.getCommentText());

        Review savedReview = reviewRepository.save(review);
        return convertToReviewDTO(savedReview);
    }

    /**
     * 更新一条现有评论。
     *
     * @param reviewId      要更新的评论 ID。
     * @param reviewRequest 包含新评论内容的数据。
     * @return 更新后的评论 DTO。
     */
    @Transactional
    public ReviewDTO updateReview(Long reviewId, ReviewRequest reviewRequest) {
        // 从数据库找到现有评论
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论"));

        // 更新评论文本
        review.setCommentText(reviewRequest.getCommentText());

        // 保存更新后的评论
        Review updatedReview = reviewRepository.save(review);
        return convertToReviewDTO(updatedReview);
    }

    /**
     * 根据 ID 删除一条评论。
     * 
     * @param reviewId 要删除的评论 ID。
     */
    @Transactional
    public void deleteReview(Long reviewId) {
        if (!reviewRepository.existsById(reviewId)) {
            throw new ResourceNotFoundException("未找到 ID 为 " + reviewId + " 的评论");
        }
        reviewRepository.deleteById(reviewId);
    }

    /**
     * 将 Review 实体转换为其 DTO 表现形式。
     * 
     * @param review Review 实体。
     * @return 对应的 ReviewDTO。
     */
    private ReviewDTO convertToReviewDTO(Review review) {
        ReviewDTO dto = new ReviewDTO();
        dto.setId(review.getId());
        dto.setCommentText(review.getCommentText());
        dto.setCreatedAt(review.getCreatedAt());
        dto.setUpdatedAt(review.getUpdatedAt());
        dto.setMovieId(review.getMovie().getId());
        dto.setMovieTitle(review.getMovie().getTitle());
        dto.setUserId(review.getUser().getId());
        dto.setUsername(review.getUser().getUsername());
        return dto;
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\UserRatingService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.userRating.UserRatingDTO;
import com.movie_back.backend.dto.userRating.UserRatingRequest;
import com.movie_back.backend.entity.Movie;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.entity.UserRating;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.MovieRepository;
import com.movie_back.backend.repository.UserRepository;
import com.movie_back.backend.repository.UserRatingRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserRatingService {

    private final UserRatingRepository userRatingRepository;
    private final MovieRepository movieRepository;
    private final UserRepository userRepository;
    private final MovieService movieService; // 用于触发平均分更新

    public UserRatingService(UserRatingRepository userRatingRepository, MovieRepository movieRepository,
            UserRepository userRepository, MovieService movieService) {
        this.userRatingRepository = userRatingRepository;
        this.movieRepository = movieRepository;
        this.userRepository = userRepository;
        this.movieService = movieService;
    }

    /**
     * 添加或更新用户对电影的评分。
     * 如果用户已经对该电影评分，则更新现有评分。
     * 否则，创建一条新评分。
     *
     * @param movieId 电影的 ID。
     * @param userId  用户的 ID。
     * @param request 评分数据。
     * @return 创建或更新后评分的 DTO。
     */
    @Transactional
    public UserRatingDTO addOrUpdateRating(Long movieId, Long userId, UserRatingRequest request) {
        // 确保电影和用户存在
        Movie movie = movieRepository.findById(movieId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + movieId + " 的电影"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("未找到 ID 为 " + userId + " 的用户"));

        // 检查该用户是否已对该电影评分
        Optional<UserRating> existingRatingOpt = userRatingRepository.findByMovieIdAndUserId(movieId, userId);

        UserRating userRating;
        if (existingRatingOpt.isPresent()) {
            // 更新现有评分
            userRating = existingRatingOpt.get();
            userRating.setScore(request.getScore());
        } else {
            // 创建新评分
            userRating = new UserRating();
            userRating.setMovie(movie);
            userRating.setUser(user);
            userRating.setScore(request.getScore());
        }

        UserRating savedRating = userRatingRepository.save(userRating);

        // 保存评分后，触发电影平均分的更新。
        movieService.updateMovieAverageRating(movieId);

        return convertToUserRatingDTO(savedRating);
    }

    /**
     * 将 UserRating 实体转换为其 DTO 表现形式。
     * 
     * @param rating UserRating 实体。
     * @return 对应的 UserRatingDTO。
     */
    private UserRatingDTO convertToUserRatingDTO(UserRating rating) {
        UserRatingDTO dto = new UserRatingDTO();
        dto.setId(rating.getId());
        dto.setScore(rating.getScore());
        dto.setRatedAt(rating.getRatedAt());
        dto.setMovieId(rating.getMovie().getId());
        dto.setUserId(rating.getUser().getId());
        return dto;
    }
}
```

# backend\src\main\java\com\movie_back\backend\service\UserService.java

```
package com.movie_back.backend.service;

import com.movie_back.backend.dto.user.UserDTO;
import com.movie_back.backend.dto.user.UserProfileUpdateDTO;
import com.movie_back.backend.dto.user.UserRegistrationRequest;
import com.movie_back.backend.entity.Role;
import com.movie_back.backend.entity.User;
import com.movie_back.backend.exception.ResourceNotFoundException;
import com.movie_back.backend.repository.UserRepository;
// import lombok.RequiredArgsConstructor; // 移除这行
import org.springframework.context.annotation.Lazy; // 导入 @Lazy
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
// @RequiredArgsConstructor // 移除此注解，因为我们将手动创建构造函数
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 手动创建构造函数，并对PasswordEncoder使用@Lazy注解
    public UserService(UserRepository userRepository, @Lazy PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // Spring Security 会调用此方法来加载用户信息
    @Override
    @Transactional(readOnly = true)
    public User loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
    }

    // 用户注册
    @Transactional
    public UserDTO registerUser(UserRegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already in use");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        // 默认注册为普通用户
        user.setRole(Role.ROLE_USER);
        // 为新用户设置一个默认头像 (使用 pravatar 服务生成一个唯一的随机头像)
        user.setProfileImageUrl("https://i.pravatar.cc/150?u=" + request.getUsername());

        User savedUser = userRepository.save(user);
        return convertToDTO(savedUser);
    }

    // 根据用户名查找用户并转换为DTO
    @Transactional(readOnly = true)
    public UserDTO findUserByUsername(String username) {
        User user = loadUserByUsername(username);
        return convertToDTO(user);
    }

    // 根据ID查找用户并转换为DTO
    @Transactional(readOnly = true)
    public UserDTO findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        return convertToDTO(user);
    }

    // 添加一个更新用户个人资料的方法
    @Transactional
    public UserDTO updateUserProfile(Long userId, UserProfileUpdateDTO profileData) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        user.setUsername(profileData.getUsername());
        user.setPersonalWebsite(profileData.getPersonalWebsite());
        user.setBirthDate(profileData.getBirthDate());
        user.setBio(profileData.getBio());

        // 新增：如果请求中包含 profileImageUrl，则更新它
        if (profileData.getProfileImageUrl() != null && !profileData.getProfileImageUrl().isEmpty()) {
            user.setProfileImageUrl(profileData.getProfileImageUrl());
        }

        User updatedUser = userRepository.save(user);
        return convertToDTO(updatedUser); // convertToDTO 也需要更新以包含新字段
    }

    private UserDTO convertToDTO(User user) {
        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setEmail(user.getEmail());
        return dto;
    }

}
```

# backend\src\main\resources\application.properties

```
server.port=7070
# file.upload-dir=

spring.application.name=backend
spring.devtools.restart.enabled=true
spring.devtools.restart.additional-paths=src/main/java
spring.devtools.restart.exclude=static/**

spring.datasource.url=jdbc:mysql://localhost:3306/movie_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update

# 在控制台打印执行的sql语句
spring.jpa.show-sql=true

# 格式化打印sql
spring.jpa.properties.hibernate.format_sql=true

# 用于签发JWT的密钥，请务必修改为一个更长、更复杂的随机字符串，不要使用这个默认值
jwt.secret=ZDQwZmFiYmE2NzYyNmJhYmJmYTRhMTc3MGEzZGVlNWYyZGFhM2ExMTEyZThjYjI2YjQ0N2U4OTQwMGE5ZGVkZA==
# JWT的过期时间（单位：毫秒）。例如：86400000 毫秒 = 24 小时
jwt.expiration=86400000
```

# backend\src\test\java\com\movie_back\backend\BackendApplicationTests.java

```
package com.movie_back.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BackendApplicationTests {

	@Test
	void contextLoads() {
	}

}
```

# backend\target\classes\application.properties

```
server.port=7070
# file.upload-dir=

spring.application.name=backend
spring.devtools.restart.enabled=true
spring.devtools.restart.additional-paths=src/main/java
spring.devtools.restart.exclude=static/**

spring.datasource.url=jdbc:mysql://localhost:3306/movie_db?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.username=root
spring.datasource.password=root

spring.jpa.hibernate.ddl-auto=update

# 在控制台打印执行的sql语句
spring.jpa.show-sql=true

# 格式化打印sql
spring.jpa.properties.hibernate.format_sql=true

# 用于签发JWT的密钥，请务必修改为一个更长、更复杂的随机字符串，不要使用这个默认值
jwt.secret=ZDQwZmFiYmE2NzYyNmJhYmJmYTRhMTc3MGEzZGVlNWYyZGFhM2ExMTEyZThjYjI2YjQ0N2U4OTQwMGE5ZGVkZA==
# JWT的过期时间（单位：毫秒）。例如：86400000 毫秒 = 24 小时
jwt.expiration=86400000
```
# frontend\public\index.html

```
<!DOCTYPE html>
<html lang="">
  <head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width,initial-scale=1.0">
    <link rel="icon" href="<%= BASE_URL %>favicon.ico">
    <title><%= htmlWebpackPlugin.options.title %></title>
  </head>
  <body>
    <noscript>
      <strong>We're sorry but <%= htmlWebpackPlugin.options.title %> doesn't work properly without JavaScript enabled. Please enable it to continue.</strong>
    </noscript>
    <div id="app"></div>
    <!-- built files will be auto injected -->
  </body>
</html>
```

# frontend\src\App.vue

```
<template>
  <n-config-provider :theme="darkTheme">
    <n-message-provider>
      <n-dialog-provider>
        <n-layout style="min-height: 100vh;">
          <TheHeader />

          <div class="main-content-container">
            <router-view />
            <TheFooter />
          </div>

        </n-layout>
      </n-dialog-provider>
    </n-message-provider>
  </n-config-provider>
</template>

<script setup>
import { NConfigProvider, NMessageProvider, NDialogProvider, NLayout, darkTheme } from 'naive-ui';
import TheHeader from '@/components/TheHeader.vue';
import TheFooter from '@/components/TheFooter.vue';
</script>

<style>
body {
  margin: 0;
  padding: 0;
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial,
    'Noto Sans', sans-serif, 'Apple Color Emoji', 'Segoe UI Emoji', 'Segoe UI Symbol',
    'Noto Color Emoji';
}

/* 新增的容器样式 */
.main-content-container {
  /* 设置内容区域的最大宽度 */
  max-width: 1400px; 
  /* 使用 margin: 0 auto; 实现水平居中 */
  margin: 0 auto;
  /* 确保在小屏幕上也能正常显示，并有一定边距 */
  width: 100%;
  padding: 0 24px;
  box-sizing: border-box;
}
</style>
```

# frontend\src\main.js

```
import { createApp } from 'vue';
import { createPinia } from 'pinia'; // 引入 Pinia
import App from './App.vue';
import router from './router';
import naive from 'naive-ui';

import 'vfonts/Lato.css';
import 'vfonts/FiraCode.css';

const app = createApp(App);
const pinia = createPinia(); // 创建 Pinia 实例 

app.use(pinia); // 使用 Pinia
app.use(router);
app.use(naive);

app.mount('#app');
```

# frontend\src\components\TheFooter.vue

```
<template>
  <footer class="site-footer">
    <div class="footer-container">
      <div class="footer-column">
        <h4>关于我们</h4>
        <p class="footer-about">
          “影评时光”致力于提供最新最全的电影资讯、评分和评论，打造属于影迷的交流社区。
        </p>
        <div class="footer-social-icons">
          <a href="#" title="微博">W</a>
          <a href="#" title="微信">C</a>
          <a href="#" title="GitHub">G</a>
        </div>
      </div>

      <div class="footer-column">
        <h4>快速链接</h4>
        <ul>
          <li><router-link to="/">首页</router-link></li>
          <li>
            <router-link to="/browser?quickFilter=hot">热门电影</router-link>
          </li>
          <li><router-link to="/browser?genre=科幻">科幻专区</router-link></li>
          <li><router-link to="/browser?genre=喜剧">喜剧专区</router-link></li>
        </ul>
      </div>

      <div class="footer-column">
        <h4>客户服务</h4>
        <ul>
          <li><a href="#">帮助中心</a></li>
          <li><a href="#">联系我们</a></li>
          <li><a href="#">服务条款</a></li>
          <li><a href="#">隐私政策</a></li>
        </ul>
      </div>

      <div class="footer-column">
        <h4>联系我们</h4>
        <p>
          客服电话：400-123-4567<br />
          电子邮箱：service@movie-time.com<br />
          工作时间：7x24小时
        </p>
      </div>
    </div>

    <div class="footer-bottom">
      <p>&copy; 2024 影评时光 版权所有</p>
    </div>
  </footer>
</template>

<script setup>
// 目前这个组件没有复杂的逻辑，所以 script 部分为空
</script>

<style scoped>
.site-footer {
  background-color: #18181c; /* 使用 Naive UI 的深色背景 */
  color: rgba(255, 255, 255, 0.52); /* 柔和的文字颜色 */
  padding: 40px 0;
  font-size: 14px;
  border-top: 1px solid rgba(255, 255, 255, 0.09);
}
.footer-container {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-around;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}
.footer-column {
  width: 22%;
  min-width: 200px;
  margin-bottom: 20px;
}
.footer-column h4 {
  color: #ffffff;
  font-size: 16px;
  margin-bottom: 15px;
}
.footer-column ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.footer-column ul li {
  margin-bottom: 10px;
}
.footer-column a,
.footer-column p {
  color: rgba(255, 255, 255, 0.52);
  text-decoration: none;
  transition: color 0.3s;
}
.footer-column a:hover {
  color: #ffffff;
}
.footer-about {
  line-height: 1.6;
}
.footer-social-icons a {
  display: inline-block;
  width: 30px;
  height: 30px;
  line-height: 30px;
  text-align: center;
  border-radius: 50%;
  background-color: rgba(255, 255, 255, 0.09);
  color: #ffffff;
  margin-top: 10px;
  margin-right: 10px;
  font-weight: bold;
  transition: background-color 0.3s;
}
.footer-social-icons a:hover {
  background-color: #36ad6a; /* Naive UI 的主色调 */
  text-decoration: none;
}
.footer-bottom {
  text-align: center;
  border-top: 1px solid rgba(255, 255, 255, 0.09);
  padding-top: 20px;
  margin-top: 20px;
  color: rgba(255, 255, 255, 0.38);
}
</style>
```

# frontend\src\components\TheHeader.vue

```
<template>
  <n-layout-header bordered style="padding: 12px 24px;">
    <n-flex justify="space-between" align="center">
      <router-link to="/">
        <n-text tag="div" style="font-size: 24px; font-weight: bold; cursor: pointer;">
          🎬 影评时光
        </n-text>
      </router-link>

      <n-input-group style="max-width: 400px;">
        <n-input v-model:value="searchTerm" :style="{ width: '80%' }" placeholder="搜索电影、演员、导演..."
          @keyup.enter="handleSearch" :loading="searchStore.isLoading" />
        <n-button type="primary" ghost @click="handleSearch">
          搜索
        </n-button>
      </n-input-group>

      <n-space align="center">
        <router-link to="/login" v-if="!authStore.isAuthenticated">
          <n-avatar round>
            登录
          </n-avatar>
        </router-link>

        <n-dropdown v-else :options="dropdownOptions" @select="handleDropdownSelect">
          <n-avatar round :src="authStore.profileImageUrl" />
        </n-dropdown>
      </n-space>
    </n-flex>
  </n-layout-header>
</template>

<script setup>
import { ref } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import { useSearchStore } from '@/stores/searchStore';
import { NLayoutHeader, NFlex, NText, NInputGroup, NInput, NButton, NSpace, NAvatar, NDropdown, useMessage } from 'naive-ui';

const router = useRouter();
const message = useMessage();
const authStore = useAuthStore();
const searchTerm = ref('');
const searchStore = useSearchStore();

const dropdownOptions = ref([
  // 新增 “我的主页”
  {
    label: '我的主页',
    key: 'profile'
  },
  {
    label: '我的评论',
    key: 'my-reviews'
  },
  {
    label: '账号设置',
    key: 'settings'
  },
  {
    type: 'divider',
    key: 'd1'
  },
  {
    label: '登出',
    key: 'logout'
  }
]);

const handleSearch = () => {
  if (searchTerm.value.trim()) {
    // 页面跳转到关键词搜索页面
    router.push({ name: 'MovieSearch', query: { keyword: searchTerm.value.trim() } });
    searchTerm.value = '';
  }
};

const handleLogout = () => {
  authStore.clearAuth();
  message.success('已成功登出');
  router.push('/');
};

const handleDropdownSelect = (key) => {
  switch (key) {
    case 'profile':
      router.push('/profile');
      break;
    case 'settings':
      router.push('/settings');
      break;
    case 'my-reviews':
      router.push('/my-reviews');
      break;
    case 'logout':
      handleLogout();
      break;
  }
};

</script>

<style scoped>
a {
  text-decoration: none;
}

/* Make the avatar clickable */
.n-avatar {
  cursor: pointer;
}
</style>
```

# frontend\src\router\index.js

```
// 文件: src/router/index.js

import { createRouter, createWebHistory } from 'vue-router';
import { useAuthStore } from '@/stores/authStore';
import HomePage from '@/views/HomePage.vue';

const routes = [
    {
        path: '/',
        name: 'Home',
        component: HomePage,
    },
    {
        path: '/login',
        name: 'LoginView',
        // 确保这里指向的是 AuthView.vue
        component: () => import('@/views/AuthView.vue'),
        props: { defaultTab: 'signin' }
    },
    {
        path: '/register',
        name: 'RegisterView',
        // 确保这里也指向的是 AuthView.vue
        component: () => import('@/views/AuthView.vue'),
        props: { defaultTab: 'signup' }
    },
    {
        path: '/movies/:id',
        name: 'MovieDetail',
        component: () => import('@/views/MovieDetail.vue'),
        props: true
    },
    {
        path: '/search',
        name: 'MovieSearch',
        component: () => import('@/views/MovieSearch.vue'),
        props: route => ({ keyword: route.query.keyword })
    },
    {
        path: '/my-reviews',
        name: 'UserReviews',
        component: () => import('@/views/UserReviews.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/profile',
        name: 'UserProfile',
        component: () => import('@/views/UserProfile.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/settings',
        name: 'AccountSettings',
        component: () => import('@/views/AccountSettings.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/browser',
        name: 'BrowserPage',
        component: () => import('@/views/BrowserPage.vue'),
        // props 设置为 true 可以让组件通过 props 接收路由参数
        props: (route) => ({ query: route.query })
    },
];

const router = createRouter({
    history: createWebHistory(process.env.BASE_URL),
    routes,
});

// 全局前置守卫 (保持不变)
router.beforeEach((to, from, next) => {
    const authStore = useAuthStore();
    if (to.meta.requiresAuth && !authStore.isAuthenticated) {
        next({ name: 'LoginView' });
    } else {
        next();
    }
});

export default router;
```

# frontend\src\services\apiService.js

```
import axios from 'axios';
import { useAuthStore } from '@/stores/authStore';

// 创建一个 Axios 实例
const apiClient = axios.create({
    baseURL: 'http://localhost:7070/api', // 您的后端 API 地址
    headers: {
        'Content-Type': 'application/json'
    }
});

// 添加一个请求拦截器，用于在每个请求的 header 中附加 Token
apiClient.interceptors.request.use(config => {
    const authStore = useAuthStore();
    const token = authStore.token;
    if (token) {
        config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
}, error => {
    return Promise.reject(error);
});

export default {
    // 认证相关
    login(credentials) {
        return apiClient.post('/auth/login', credentials);
    },
    register(userInfo) {
        return apiClient.post('/auth/register', userInfo);
    },

    // 电影相关
    getHotMovies(limit = 10) {
        return apiClient.get(`/movies/hot?limit=${limit}`);
    },
    // 新增：获取最新电影的接口
    getLatestMovies(limit = 10) {
        // 假设后端通过 sortBy=releaseYear&sortDir=desc 来实现
        return apiClient.get(`/movies/search?sortBy=releaseYear&sortDir=desc&size=${limit}`);
    },
    searchMovies(params) {
        // params 可以是 { name: 'keyword' }
        return apiClient.get(`/movies/${params.type}?name=${params.name}`);
    },
    getMovieById(id) {
        return apiClient.get(`/movies/${id}`);
    },

    // 评论相关
    getReviewsForMovie(movieId) {
        return apiClient.get(`/movies/${movieId}/reviews`);
    },
    getReviewsByUserId(userId) {
        return apiClient.get(`/users/${userId}/reviews`);
    },
    addReview(movieId, userId, commentText) {
        return apiClient.post(`/movies/${movieId}/reviews?userId=${userId}`, { commentText });
    },
    updateReview(reviewId, commentText) {
        return apiClient.put(`/reviews/${reviewId}`, { commentText });
    },
    deleteReview(reviewId) {
        return apiClient.delete(`/reviews/${reviewId}`);
    },

    // 评分相关
    rateMovie(movieId, userId, score) {
        return apiClient.post(`/movies/${movieId}/ratings?userId=${userId}`, { score });
    },
    // 新增用户相关接口
    sendEmailVerificationCode(data) {
        return axios.post('/api/send-email-code', data); // 替换为你的后端接口
    },
    sendPhoneVerificationCode(data) {
        return axios.post('/api/send-phone-code', data); // 替换为你的后端接口
    },
    changeEmail(data) {
        return axios.post('/api/change-email', data); // 替换为你的后端接口
    },
    changePhone(data) {
        return axios.post('/api/change-phone', data); // 替换为你的后端接口
    },
    updateUserProfile(data) {
        return axios.put('/api/user/profile', data);
    },
};
```

# frontend\src\stores\authStore.js

```
import { defineStore } from 'pinia';
import { ref, computed } from 'vue';

export const useAuthStore = defineStore('auth', () => {
    // 从 localStorage 初始化 state
    const token = ref(localStorage.getItem('token') || null);
    const user = ref(JSON.parse(localStorage.getItem('user')) || null);

    const isAuthenticated = computed(() => !!token.value);
    const username = computed(() => user.value?.username);
    const userId = computed(() => user.value?.userId);
    const profileImageUrl = computed(() => user.value?.profileImageUrl);


    function setAuth(authData) {
        token.value = authData.token;
        user.value = {
            userId: authData.userId,
            username: authData.username,
            role: authData.role,
            profileImageUrl: authData.profileImageUrl
        };
        // 将认证信息存入 localStorage
        localStorage.setItem('token', authData.token);
        localStorage.setItem('user', JSON.stringify(user.value));
    }

    function clearAuth() {
        token.value = null;
        user.value = null;
        localStorage.removeItem('token');
        localStorage.removeItem('user');
    }

    return { token, user, isAuthenticated, username, userId, profileImageUrl, setAuth, clearAuth };
});
```

# frontend\src\stores\searchStore.js

```
import { defineStore } from 'pinia';
import { ref } from 'vue';

export const useSearchStore = defineStore('search', () => {
    // 定义一个全局的 isLoading 状态
    const isLoading = ref(false);

    // 定义一个 action 来开始加载
    function startLoading() {
        isLoading.value = true;
    }

    // 定义一个 action 来结束加载
    function stopLoading() {
        isLoading.value = false;
    }

    // 将状态和 actions return 出去，以便其他组件使用
    return { isLoading, startLoading, stopLoading };
});
```

# frontend\src\views\AccountSettings.vue

```
<template>
  <n-layout-content content-style="padding: 24px;">
    <n-grid :x-gap="24" :cols="5">
      <n-gi :span="1">
        <n-menu :options="menuOptions" v-model:value="activeKey" />
      </n-gi>
      <n-gi :span="4" style="min-height: 600px">
        <n-card v-if="activeKey === 'basic'" title="基本资料">
          <n-form
            ref="formRef"
            :model="profileForm"
            label-placement="left"
            label-width="80"
          >
            <n-form-item label="头像">
              <n-space vertical>
                <n-space align="baseline">
                  <n-avatar :size="96" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="64" :src="profileForm.profileImageUrl" />
                  <n-avatar :size="48" :src="profileForm.profileImageUrl" />
                </n-space>
                <n-upload
                  :custom-request="customAvatarUploadRequest"
                  :show-file-list="false"
                  :max="1"
                  accept="image/*"
                >
                  <n-button :loading="uploadingToHost">更换头像</n-button>
                </n-upload>
              </n-space>
            </n-form-item>
            <n-form-item label="昵称" path="username">
              <n-input
                v-model:value="profileForm.username"
                placeholder="输入你的昵称"
              />
            </n-form-item>
            <n-form-item label="生日">
              <n-input-group>
                <n-date-picker
                  v-model:formatted-value="profileForm.birthDate"
                  value-format="yyyy-MM-dd"
                  type="date"
                  style="width: 250px"
                />
                <n-button @click="profileForm.birthDate = null">清空</n-button>
              </n-input-group>
            </n-form-item>
            <n-form-item label="个人主页" path="personalWebsite">
              <n-input-group>
                <n-button>http://</n-button>
                <n-input
                  v-model:value="profileForm.personalWebsite"
                  placeholder="你的个人网站"
                />
              </n-input-group>
            </n-form-item>
            <n-form-item label="自我介绍" path="bio">
              <n-input
                v-model:value="profileForm.bio"
                type="textarea"
                placeholder="介绍一下自己吧"
                :autosize="{ minRows: 3, maxRows: 5 }"
              />
            </n-form-item>
          </n-form>
        </n-card>

        <n-card v-if="activeKey === 'security'" title="账号安全">
          <n-form label-placement="left" label-width="80">
            <n-form-item label="邮箱">
              <n-text>{{ profileForm.email || "未绑定" }}</n-text>
              <n-button
                text
                type="primary"
                style="margin-left: 12px"
                @click="openBindingModal('email')"
              >
                {{ profileForm.email ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="手机号">
              <n-text>{{ maskedPhone || "未绑定" }}</n-text>
              <n-button
                text
                type="primary"
                style="margin-left: 12px"
                @click="openBindingModal('phone')"
              >
                {{ profileForm.phone ? "换绑" : "绑定" }}
              </n-button>
            </n-form-item>
            <n-form-item label="密码">
              <n-button @click="showPasswordModal = true">修改密码</n-button>
            </n-form-item>
          </n-form>
        </n-card>

        <n-flex justify="end" style="margin-top: 24px">
          <n-button type="primary" @click="handleSave" :loading="saving"
            >保存修改</n-button
          >
        </n-flex>
      </n-gi>
    </n-grid>
  </n-layout-content>

  <!-- 修改密码 Modal -->
  <n-modal
    v-model:show="showPasswordModal"
    preset="card"
    title="修改密码"
    style="width: 450px"
  >
    <n-form>
      <n-form-item label="旧密码">
        <n-input
          v-model:value="passwordForm.oldPassword"
          type="password"
          show-password-on="click"
          placeholder="请输入旧密码"
        />
      </n-form-item>
      <n-form-item label="新密码">
        <n-input
          v-model:value="passwordForm.newPassword"
          type="password"
          show-password-on="click"
          placeholder="请输入新密码"
        />
      </n-form-item>
      <n-form-item label="确认新密码">
        <n-input
          v-model:value="passwordForm.confirmPassword"
          type="password"
          show-password-on="click"
          placeholder="请再次输入新密码"
        />
      </n-form-item>
    </n-form>
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showPasswordModal = false">取消</n-button>
        <n-button type="primary" @click="handleChangePassword"
          >确认修改</n-button
        >
      </n-flex>
    </template>
  </n-modal>

  <!-- ========== NEW CODE: BINDING MODAL START ========== -->
  <n-modal
    v-model:show="showBindingModal"
    preset="card"
    :title="bindingTitle"
    style="width: 480px"
    @after-leave="countdown = 0"
  >
    <n-form>
      <n-form-item :label="bindingLabel">
        <n-input
          v-model:value="bindingForm.value"
          :placeholder="bindingPlaceholder"
        />
      </n-form-item>
      <n-form-item label="验证码">
        <n-input-group>
          <n-input
            v-model:value="bindingForm.code"
            placeholder="请输入6位验证码"
          />
          <n-button
            @click="sendCode"
            :loading="codeSending"
            :disabled="isCountingDown"
          >
            {{ countdownText }}
          </n-button>
        </n-input-group>
      </n-form-item>
    </n-form>
    <template #footer>
      <n-flex justify="end">
        <n-button @click="showBindingModal = false">取消</n-button>
        <n-button type="primary" @click="submitBinding">确认</n-button>
      </n-flex>
    </template>
  </n-modal>
  <!-- ========== NEW CODE: BINDING MODAL END ========== -->
</template>

<script setup>
import { ref, onMounted, computed } from "vue";
import { useAuthStore } from "@/stores/authStore";
import apiService from "@/services/apiService";
import {
  NLayoutContent,
  NGrid,
  NGi,
  NMenu,
  NCard,
  NForm,
  NFormItem,
  NInput,
  NInputGroup,
  NAvatar,
  NUpload,
  NButton,
  NSpace,
  NFlex,
  NDatePicker,
  NText,
  NModal,
  useMessage,
} from "naive-ui";
import axios from "axios"; // 引入 axios 用于上传到图床

const authStore = useAuthStore();
const message = useMessage();
const activeKey = ref("basic");
const saving = ref(false);
const showPasswordModal = ref(false);
const uploadingToHost = ref(false); // 新增：图床上传状态

const profileForm = ref({
  username: "",
  profileImageUrl: "",
  personalWebsite: "",
  bio: "",
  birthDate: null,
  email: "",
  phone: "",
});

const maskedPhone = computed(() => {
  const phone = profileForm.value.phone;
  if (phone && phone.length === 11) {
    return phone.replace(/(\d{3})\d{4}(\d{4})/, "$1****$2");
  }
  return phone;
});

// --- Start: Logic for the binding modal ---
const showBindingModal = ref(false);
const bindingType = ref(""); // 'email' or 'phone'
const bindingForm = ref({ value: "", code: "" });

const codeSending = ref(false);
const countdown = ref(0);
const isCountingDown = computed(() => countdown.value > 0);

const countdownText = computed(() =>
  isCountingDown.value ? `${countdown.value}s 后重试` : "发送验证码"
);

const bindingTitle = computed(() => {
  const action = profileForm.value[bindingType.value] ? "更换" : "绑定";
  const type = bindingType.value === "email" ? "邮箱" : "手机号";
  return `${action}${type}`;
});

const bindingLabel = computed(
  () => "新" + (bindingType.value === "email" ? "邮箱地址" : "手机号码")
);

const bindingPlaceholder = computed(() => "请输入" + bindingLabel.value);

const passwordForm = ref({
  oldPassword: "",
  newPassword: "",
  confirmPassword: "",
});

function openBindingModal(type) {
  bindingType.value = type;
  bindingForm.value = { value: "", code: "" }; // Reset form
  countdown.value = 0; // Reset countdown
  showBindingModal.value = true;
}

async function sendCode() {
  if (!bindingForm.value.value) {
    message.error(
      `请输入${bindingType.value === "email" ? "邮箱地址" : "手机号码"}`
    );
    return;
  }
  codeSending.value = true;

  try {
    // This is a placeholder for your actual API call
    console.log(`向 ${bindingForm.value.value} 发送验证码...`);
    // Example using your apiService structure:
    // const apiCall = bindingType.value === 'email'
    //   ? apiService.sendEmailVerificationCode({ email: bindingForm.value.value })
    //   : apiService.sendPhoneVerificationCode({ phone: bindingForm.value.value });
    // await apiCall;

    // --- Start: Mock API call for demonstration ---
    await new Promise((resolve) => setTimeout(resolve, 1000));
    // --- End: Mock API call ---

    message.success("验证码已发送，请注意查收");
    countdown.value = 60;
    const interval = setInterval(() => {
      countdown.value--;
      if (countdown.value <= 0) {
        clearInterval(interval);
      }
    }, 1000);
  } catch (error) {
    message.error("发送验证码失败: " + (error.message || "请稍后重试"));
    console.error("发送验证码失败:", error);
  } finally {
    codeSending.value = false;
  }
}

async function submitBinding() {
  if (!bindingForm.value.value || !bindingForm.value.code) {
    message.error("请填写完整信息。");
    return;
  }
  try {
    // This is a placeholder for your actual API call
    console.log(`提交绑定信息:`, bindingForm.value);
    // Example using your apiService structure:
    // const apiCall = bindingType.value === 'email'
    //   ? apiService.changeEmail({ newEmail: bindingForm.value.value, verificationCode: bindingForm.value.code })
    //   : apiService.changePhone({ newPhone: bindingForm.value.value, verificationCode: bindingForm.value.code });
    // await apiCall;

    // --- Start: Mock API call for demonstration ---
    await new Promise((resolve) => setTimeout(resolve, 1000));
    // --- End: Mock API call ---

    message.success(`${bindingTitle.value}成功！`);
    // Update local state
    profileForm.value[bindingType.value] = bindingForm.value.value;
    authStore.user[bindingType.value] = bindingForm.value.value;
    localStorage.setItem("user", JSON.stringify(authStore.user));
    showBindingModal.value = false;
  } catch (error) {
    message.error("操作失败: " + (error.message || "验证码错误或已过期"));
    console.error("绑定失败:", error);
  }
}
// --- End: Logic for the binding modal ---

onMounted(() => {
  if (authStore.user) {
    // For demonstration, I'm keeping your hardcoded phone.
    // In a real app, this would come from authStore.user.phone
    const phone = authStore.user.phone || "18212347833";
    profileForm.value = {
      ...authStore.user,
      phone: phone, // ensure phone is populated for the demo
    };
  }
});

const menuOptions = [
  { label: "基本资料", key: "basic" },
  { label: "账号安全", key: "security" },
];

/**
 * !重要: 此函数需要您根据自己的图床服务进行修改
 * @param {File} file - 用户选择的文件对象
 */
const uploadToImageHost = async (file) => {
  // --- 请在此处替换为您的图床上传逻辑 ---

  // ImgBB API 端点
  const imgbbUploadUrl = "https://api.imgbb.com/1/upload";

  // 重要: 将 'YOUR_IMGBB_API_KEY' 替换为你的实际 ImgBB API 密钥。
  // 你可以在注册并登录 ImgBB 后获取一个密钥：https://imgbb.com/account/api
  const imgbbApiKey = "4312ec520960fe609d17eb3f8a99ca5e";

  const formData = new FormData();
  formData.append("image", file); // ImgBB 要求文件字段名为 'image'
  formData.append("key", imgbbApiKey); // 你的 ImgBB API 密钥

  try {
    const response = await axios.post(imgbbUploadUrl, formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });

    // ImgBB 通常返回的数据格式是 { data: { url: '...' } }
    // 我们通过 response.data.data.url 获取直接的图片 URL
    return response.data.data.url;
  } catch (error) {
    console.error("上传到图床失败:", error);
    // 你可能希望在此处提供一个更友好的错误消息
    throw new Error(
      "上传到图床失败: " +
        (error.response?.data?.error?.message || error.message)
    );
  }
};

const customAvatarUploadRequest = async ({
  file,
  onFinish,
  onError,
}) => {
  uploadingToHost.value = true; // Set loading state
  try {
    // Call your ImgBB upload function
    const imageUrl = await uploadToImageHost(file.file);

    // Update profile image URL
    profileForm.value.profileImageUrl = imageUrl;
    authStore.user.profileImageUrl = imageUrl;
    localStorage.setItem("user", JSON.stringify(authStore.user));

    message.success("头像更新成功");
    onFinish(); // Notify n-upload that the file is finished
  } catch (error) {
    message.error("头像上传失败");
    onError(); // Notify n-upload that an error occurred
  } finally {
    uploadingToHost.value = false; // Reset loading state
  }
};

const handleSave = async () => {
  saving.value = true;
  try {
    const payload = {
      username: profileForm.value.username,
      personalWebsite: profileForm.value.personalWebsite,
      bio: profileForm.value.bio,
      birthDate: profileForm.value.birthDate,
      // Note: email, phone, and password are changed in their own modals
    };
    await apiService.updateUserProfile(payload);

    // Update the store and localStorage with the new data
    Object.assign(authStore.user, payload);
    localStorage.setItem("user", JSON.stringify(authStore.user));

    message.success("个人资料更新成功！");
  } catch (error) {
    message.error("保存失败，请重试。");
    console.error(error);
  } finally {
    saving.value = false;
  }
};

// Placeholder for the actual change password logic
const handleChangePassword = () => {
  message.info("修改密码逻辑待实现");
  console.log("Password form submitted:", passwordForm.value);
  showPasswordModal.value = false;
};
</script>
```

# frontend\src\views\AuthView.vue

```
<template>
    <div class="auth-container">
      <n-card class="auth-card">
        <n-tabs
          :default-value="defaultTab"
          size="large"
          animated
          pane-wrapper-style="margin: 0 -4px"
          pane-style="padding-left: 4px; padding-right: 4px; box-sizing: border-box;"
        >
          <n-tab-pane name="signin" tab="登录">
            <n-form ref="loginFormRef" :model="loginFormValue" :rules="loginRules">
              <n-form-item-row label="用户名" path="username">
                <n-input v-model:value="loginFormValue.username" placeholder="请输入用户名" />
              </n-form-item-row>
              <n-form-item-row label="密码" path="password">
                <n-input
                  v-model:value="loginFormValue.password"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请输入密码"
                  @keyup.enter="handleLogin"
                />
              </n-form-item-row>
            </n-form>
            <n-button type="primary" block strong :loading="loginLoading" @click="handleLogin">
              登录
            </n-button>
             <n-flex justify="center" style="margin-top: 16px;">
                <router-link to="/"><n-button text>返回首页</n-button></router-link>
              </n-flex>
          </n-tab-pane>
          <n-tab-pane name="signup" tab="注册">
            <n-form ref="registerFormRef" :model="registerFormValue" :rules="registerRules">
              <n-form-item-row label="用户名" path="username">
                <n-input v-model:value="registerFormValue.username" placeholder="请输入用户名" />
              </n-form-item-row>
               <n-form-item-row label="邮箱" path="email">
                  <n-input v-model:value="registerFormValue.email" placeholder="请输入邮箱" />
              </n-form-item-row>
              <n-form-item-row label="密码" path="password">
                <n-input
                  v-model:value="registerFormValue.password"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请输入至少6位的密码"
                />
              </n-form-item-row>
              <n-form-item-row label="重复密码" path="reenteredPassword">
                <n-input
                  v-model:value="registerFormValue.reenteredPassword"
                  type="password"
                  show-password-on="mousedown"
                  placeholder="请再次输入密码"
                  @keyup.enter="handleRegister"
                />
              </n-form-item-row>
            </n-form>
            <n-button type="primary" block strong :loading="registerLoading" @click="handleRegister">
              注册
            </n-button>
             <n-flex justify="center" style="margin-top: 16px;">
                <router-link to="/"><n-button text>返回首页</n-button></router-link>
              </n-flex>
          </n-tab-pane>
        </n-tabs>
      </n-card>
    </div>
  </template>
  
  <script setup>
  import { ref } from 'vue';
  import { useRouter } from 'vue-router';
  import {
    // 移除了 NLayout
    NCard, NTabs, NTabPane, NForm, NFormItemRow,
    NInput, NButton, NFlex, useMessage
  } from 'naive-ui';
  import { useAuthStore } from '@/stores/authStore';
  import apiService from '@/services/apiService';
  
//   const props = defineProps({
//       defaultTab: {
//           type: String,
//           default: 'signin'
//       }
//   });
  
  // --- script 部分的登录和注册逻辑保持不变 ---
  const router = useRouter();
  const message = useMessage();
  const authStore = useAuthStore();
  const loginFormRef = ref(null);
  const loginLoading = ref(false);
  const loginFormValue = ref({
    username: '',
    password: '',
  });
  const loginRules = {
    username: { required: true, message: '请输入用户名', trigger: 'blur' },
    password: { required: true, message: '请输入密码', trigger: 'blur' },
  };
  const handleLogin = (e) => {
    e.preventDefault();
    loginFormRef.value?.validate(async (errors) => {
      if (!errors) {
        loginLoading.value = true;
        try {
          const response = await apiService.login(loginFormValue.value);
          authStore.setAuth(response.data);
          message.success('登录成功！');
          router.push('/');
        } catch (error) {
          message.error(error.response?.data?.message || '登录失败，请检查用户名或密码');
        } finally {
          loginLoading.value = false;
        }
      }
    });
  };
  const registerFormRef = ref(null);
  const registerLoading = ref(false);
  const registerFormValue = ref({
    username: '',
    email: '',
    password: '',
    reenteredPassword: '',
  });
  const validatePasswordSame = (rule, value) => {
      if (value !== registerFormValue.value.password) {
          return new Error('两次输入的密码不一致');
      }
      return true;
  };
  const registerRules = {
    username: { required: true, message: '用户名不能为空', trigger: 'blur' },
    email: { required: true, type: 'email', message: '请输入正确的邮箱格式', trigger: ['input', 'blur'] },
    password: { required: true, min: 6, message: '密码长度不能少于6位', trigger: 'blur' },
    reenteredPassword: [
      { required: true, message: '请再次输入密码', trigger: ['input', 'blur'] },
      { validator: validatePasswordSame, trigger: 'blur' }
    ]
  };
  const handleRegister = (e) => {
    e.preventDefault();
    registerFormRef.value?.validate(async (errors) => {
      if (!errors) {
        registerLoading.value = true;
        try {
          const payload = {
              username: registerFormValue.value.username,
              email: registerFormValue.value.email,
              password: registerFormValue.value.password,
          };
          await apiService.register(payload);
          message.success('注册成功，请登录！');
          router.push('/login');
        } catch (error) {
          message.error(error.response?.data?.message || '注册失败');
        } finally {
          registerLoading.value = false;
        }
      }
    });
  };
  
  </script>
  
  <style scoped>
  /* 将 .auth-layout 改为 .auth-container */
  .auth-container {
    display: flex;
    justify-content: center;
    align-items: center;
    flex-grow: 1; /* 让这个容器填满 App.vue 布局中的剩余空间 */
    padding: 40px 0; /* 添加一些垂直内边距 */
    /* background-color: #f0f2f5; <-- 移除这一行，让背景色透明，显示出全局背景色 */
  }
  .auth-card {
    width: 400px;
  }
  a {
    text-decoration: none;
  }
  </style>
```

# frontend\src\views\BrowserPage.vue

```
<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2 prefix="bar">
      <n-text type="primary">选电影</n-text>
    </n-h2>

    <n-space align="center" style="margin-bottom: 20px">
      <n-button
        text
        :type="quickFilter === 'all' ? 'primary' : 'default'"
        @click="handleQuickFilter('all')"
        tag="a"
        >全部</n-button
      >
      <n-divider vertical />
      <n-button
        text
        :type="quickFilter === 'hot' ? 'primary' : 'default'"
        @click="handleQuickFilter('hot')"
        tag="a"
        >热门电影</n-button
      >
      <n-button
        text
        :type="quickFilter === 'latest' ? 'primary' : 'default'"
        @click="handleQuickFilter('latest')"
        tag="a"
        >最新电影</n-button
      >
      <n-button
        text
        :type="quickFilter === 'top_rated' ? 'primary' : 'default'"
        @click="handleQuickFilter('top_rated')"
        tag="a"
        >高分佳片</n-button
      >
    </n-space>

    <n-card size="small">
      <n-space vertical :size="20">
        <n-space align="center">
          <n-text style="width: 50px">类型:</n-text>
          <n-space>
            <n-tag
              v-for="tag in genreTags"
              :key="tag.value"
              checkable
              :checked="filterParams.genre === tag.value"
              @click="selectFilter('genre', tag.value)"
            >
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">地区:</n-text>
          <n-space>
            <n-tag
              v-for="tag in countryTags"
              :key="tag.value"
              checkable
              :checked="filterParams.country === tag.value"
              @click="selectFilter('country', tag.value)"
            >
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
        <n-space align="center">
          <n-text style="width: 50px">年代:</n-text>
          <n-space>
            <n-tag
              v-for="tag in yearTags"
              :key="tag.value"
              checkable
              :checked="filterParams.releaseYear === tag.value"
              @click="selectFilter('releaseYear', tag.value)"
            >
              {{ tag.label }}
            </n-tag>
          </n-space>
        </n-space>
      </n-space>

      <n-divider />

      <n-space align="center">
        <n-text>其他选项:</n-text>
        <n-checkbox disabled>未看过 (功能待定)</n-checkbox>
        <n-checkbox disabled>可播放 (功能待定)</n-checkbox>
      </n-space>
    </n-card>

    <n-spin :show="loading" style="margin-top: 24px">
      <n-grid
        v-if="movies.length > 0"
        :x-gap="16"
        :y-gap="24"
        :cols="'2 s:3 m:4 l:5 xl:6'"
        responsive="true"
      >
        <n-grid-item v-for="movie in movies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" />
              </template>
              <template #footer>
                <n-rate
                  readonly
                  :value="movie.averageRating / 2"
                  allow-half
                  size="small"
                />
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
      <n-empty
        v-else
        description="没有找到符合条件的电影。"
        style="padding: 48px 0"
      />
    </n-spin>

    <n-flex justify="center" style="margin-top: 24px">
      <n-pagination
        v-model:page="pagination.page"
        :page-count="pagination.pageCount"
        @update:page="handlePageChange"
      />
    </n-flex>
  </n-layout-content>
</template>

<script setup>
import { ref, watch } from "vue";
import { useRoute, useRouter } from "vue-router";
import apiService from "@/services/apiService";
import {
  NLayoutContent,
  NGrid,
  NCard,
  NSpace,
  NTag,
  NCheckbox,
  NRate,
  NButton,
  NH2,
  NSpin,
  NEmpty,
  NPagination,
  NFlex,
  NDivider,
  NText,
} from "naive-ui";

const route = useRoute();
const router = useRouter();
const loading = ref(true);
const movies = ref([]);

// --- 筛选数据模型 ---

// 筛选标签数据
const genreTags = [
  { label: "全部", value: null },
  { label: "剧情", value: "剧情" },
  { label: "喜剧", value: "喜剧" },
  { label: "科幻", value: "科幻" },
  { label: "动作", value: "动作" },
  { label: "爱情", value: "爱情" },
  { label: "动画", value: "动画" },
  { label: "悬疑", value: "悬疑" },
  { label: "惊悚", value: "惊悚" },
];
const countryTags = [
  { label: "全部", value: null },
  { label: "中国大陆", value: "中国大陆" },
  { label: "美国", value: "美国" },
  { label: "日本", value: "日本" },
  { label: "韩国", value: "韩国" },
  { label: "英国", value: "英国" },
];
const yearTags = [
  { label: "全部", value: null },
  { label: "2024", value: 2024 },
  { label: "2023", value: 2023 },
  { label: "2022", value: 2022 },
  { label: "2010年代", value: "2010s" },
  { label: "2000年代", value: "2000s" },
];

// 筛选参数
const filterParams = ref({
  genre: null,
  country: null,
  releaseYear: null,
  sortBy: "averageRating", // 默认按热门排序
  sortDir: "desc",
});

// 顶部快速筛选状态
const quickFilter = ref("hot");

// 分页参数
const pagination = ref({
  page: 1,
  pageSize: 20,
  pageCount: 1,
});

// --- 方法 ---

// 核心数据获取函数
const fetchData = async () => {
  loading.value = true;
  const params = {
    ...Object.fromEntries(
      Object.entries(filterParams.value).filter(
        ([, v]) => v !== null && v !== ""
      )
    ),
    page: pagination.value.page - 1,
    size: pagination.value.pageSize,
  };
  try {
    const response = await apiService.getLatestMovies(params);
    movies.value = response.data.content;
    pagination.value.pageCount = response.data.totalPages;
  } catch (error) {
    console.error("获取电影数据失败:", error);
  } finally {
    loading.value = false;
  }
};

// 点击标签进行筛选, 更新路由
const selectFilter = (key, value) => {
  const newQuery = { ...route.query };
  delete newQuery.quickFilter; // 清除快速筛选
  delete newQuery.page; // 重置分页

  // 如果点击的是已选中的标签，则取消选择
  if (filterParams.value[key] === value) {
    delete newQuery[key];
  } else {
    newQuery[key] = value;
  }
  router.push({ query: newQuery });
};

// 点击顶部快速筛选, 更新路由
const handleQuickFilter = (type) => {
  router.push({ query: { quickFilter: type } });
};

// 处理分页变化, 更新路由
const handlePageChange = (currentPage) => {
  router.push({ query: { ...route.query, page: currentPage } });
};

// 监听路由 query 的变化来更新筛选条件并重新获取数据
watch(
  () => route.query,
  (query) => {
    // 1. 从 URL 更新组件内部状态
    pagination.value.page = query.page ? Number(query.page) : 1;
    quickFilter.value = query.quickFilter || "";
    filterParams.value.genre = query.genre || null;
    filterParams.value.country = query.country || null;
    filterParams.value.releaseYear = query.releaseYear || null;

    // 2. 根据状态设置排序规则
    if (quickFilter.value === "all") {
      filterParams.value.sortBy = "title";
      filterParams.value.sortDir = "asc";
    } else if (quickFilter.value === "latest") {
      filterParams.value.sortBy = "releaseYear";
      filterParams.value.sortDir = "desc";
    } else {
      // 默认 (hot, top_rated) 或详细筛选时，都按评分排序
      filterParams.value.sortBy = "averageRating";
      filterParams.value.sortDir = "desc";
    }

    // 3. 处理无任何筛选条件的默认情况
    if (Object.keys(query).length === 0) {
      quickFilter.value = "hot"; // 默认高亮“热门电影”
    }

    // 4. 获取数据
    fetchData();
  },
  {
    immediate: true, // 关键：组件挂载时立即执行一次 watcher
    deep: true,
  }
);
</script>

<style scoped>
.movie-poster-img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}
:deep(.n-card-header__main) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}
a {
  text-decoration: none;
  color: inherit;
}
.n-button[text] {
  font-size: 16px;
  margin: 0 8px;
}
</style>
```

# frontend\src\views\HomePage.vue

```
<template>
  <n-layout-content content-style="padding: 24px;">
    <n-carousel autoplay show-arrow style="margin-bottom: 30px;">
      <img
        v-for="slide in carouselSlides"
        :key="slide.id"
        class="carousel-img"
        :src="slide.image"
      >
    </n-carousel>

    <n-h2 prefix="bar">
      <n-text type="primary">热门电影</n-text>
    </n-h2>
    <n-spin :show="loading">
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in popularMovies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                 <n-rate readonly :value="movie.averageRating / 2" allow-half size="small" />
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
    </n-spin>

    <n-h2 prefix="bar" style="margin-top: 40px;">
      <n-text type="primary">最新电影</n-text>
    </n-h2>
    <n-spin :show="loading">
      <n-grid :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in latestMovies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img" :alt="movie.title">
              </template>
              <template #footer>
                <n-p depth="3" style="font-size: 12px; text-align: right;">
                  {{ movie.releaseYear }}
                </n-p>
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
    </n-spin>
  </n-layout-content>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import {
NLayoutContent, NText, NCarousel,
NH2, NGrid, NGridItem, NCard, NRate, NSpin
} from 'naive-ui';
// 不再需要 import TheHeader
import apiService from '@/services/apiService';

const popularMovies = ref([]);
const latestMovies = ref([]); // 新增 ref
const loading = ref(true);

const carouselSlides = ref([
{ id: 1, image: 'https://picsum.photos/seed/carousel_scene_1/1200/500' },
{ id: 2, image: 'https://picsum.photos/seed/carousel_scene_2/1200/500' },
{ id: 3, image: 'https://picsum.photos/seed/carousel_scene_3/1200/500' },
]);

const fetchHotMovies = async () => {
loading.value = true;
try {
  const response = await apiService.getHotMovies(12);
  popularMovies.value = response.data;
} catch (error) {
  console.error('获取热门电影失败:', error);
} finally {
  loading.value = false;
}
};

onMounted(fetchHotMovies);
</script>

<style scoped>
.carousel-img {
width: 100%;
height: 500px; /* 可以调整轮播图高度 */
object-fit: cover;
}
.movie-poster-img {
width: 100%;
aspect-ratio: 2 / 3;
object-fit: cover;
}
:deep(.n-card-header__main) {
white-space: nowrap;
overflow: hidden;
text-overflow: ellipsis;
}
a {
text-decoration: none;
color: inherit;
}
</style>
```

# frontend\src\views\MovieDetail.vue

```
<template>
    <n-layout-content content-style="padding: 24px;">
      <n-spin :show="loading">
        <div v-if="movie">
          <n-grid :x-gap="24" :cols="4">
            <n-gi :span="1">
              <img :src="movie.posterUrl" class="poster-img" />
            </n-gi>
            <n-gi :span="3">
              <n-h1>{{ movie.title }} <n-text depth="3">({{ movie.releaseYear }})</n-text></n-h1>
              <n-p><strong>导演:</strong> {{ movie.directorNames.join(', ') }}</n-p>
              <n-p><strong>主演:</strong> {{ movie.actorNames.join(', ') }}</n-p>
              <n-p><strong>类型:</strong> {{ movie.genre }}</n-p>
              <n-p><strong>国家/地区:</strong> {{ movie.country }}</n-p>
              <n-p><strong>时长:</strong> {{ movie.duration }} 分钟</n-p>
              <n-p><strong>简介:</strong> {{ movie.synopsis }}</n-p>
              <n-flex align="center">
                <n-text strong>平均评分:</n-text>
                <n-rate readonly :value="movie.averageRating / 2" allow-half />
                <n-text strong>{{ movie.averageRating }}</n-text>
              </n-flex>
            </n-gi>
          </n-grid>
  
          <n-divider />
  
          <n-h2>评论区</n-h2>
          <div v-if="authStore.isAuthenticated">
            <n-h3>发表我的评分和评论</n-h3>
            <n-rate v-model:value="myRating" />
            <n-input
              v-model:value="myComment"
              type="textarea"
              placeholder="写下你的评论..."
              style="margin-top: 12px;"
            />
            <n-button type="primary" @click="submitReview" style="margin-top: 12px;">提交</n-button>
          </div>
          <n-alert v-else title="提示" type="info">
            请<router-link to="/login">登录</router-link>后发表评论。
          </n-alert>
  
          <n-list bordered style="margin-top: 24px;">
            <n-list-item v-for="review in reviews" :key="review.id">
              <n-thing :title="review.username" :description="review.commentText">
                <template #header-extra>
                  <n-text depth="3">{{ new Date(review.createdAt).toLocaleString() }}</n-text>
                </template>
              </n-thing>
            </n-list-item>
             <n-list-item v-if="reviews.length === 0">
                <n-empty description="暂无评论" />
             </n-list-item>
          </n-list>
        </div>
        <n-empty v-else description="电影信息加载失败" />
      </n-spin>
    </n-layout-content>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  // import { useRoute } from 'vue-router'; // <--- 移除这一行
  import apiService from '@/services/apiService';
  import { useAuthStore } from '@/stores/authStore';
  import { 
      NLayoutContent, NSpin, NGrid, NGi, NH1, NH2, NH3, NP, NText, NRate, NDivider, 
      NInput, NButton, NList, NListItem, NThing, NEmpty, NFlex, NAlert, useMessage
  } from 'naive-ui';
  // import TheHeader from '@/components/TheHeader.vue';
  
  // defineProps 是编译器宏，直接使用即可
  const props = defineProps({
    id: String
  });
  
  // const route = useRoute(); // <--- 移除这一行
  const message = useMessage();
  const authStore = useAuthStore();
  
  const movie = ref(null);
  const reviews = ref([]);
  const loading = ref(true);
  const myRating = ref(0);
  const myComment = ref('');
  
  const fetchMovieData = async () => {
    loading.value = true;
    try {
      const movieResponse = await apiService.getMovieById(props.id);
      movie.value = movieResponse.data;
      const reviewsResponse = await apiService.getReviewsForMovie(props.id);
      reviews.value = reviewsResponse.data;
    } catch (error) {
      message.error('加载电影数据失败');
      console.error(error);
    } finally {
      loading.value = false;
    }
  };
  
  const submitReview = async () => {
      if (!myComment.value) {
          message.warning('评论内容不能为空');
          return;
      }
      if (myRating.value > 0) {
          // 先提交评分
          await apiService.rateMovie(props.id, authStore.userId, myRating.value * 2);
      }
      // 再提交评论
      await apiService.addReview(props.id, authStore.userId, myComment.value);
  
      message.success('评论成功！');
      myComment.value = '';
      myRating.value = 0;
      fetchMovieData(); // 重新加载数据
  };
  
  onMounted(fetchMovieData);
  </script>
  
  <style scoped>
  .poster-img {
    width: 100%;
    border-radius: 8px;
  }
  </style>
```

# frontend\src\views\MovieSearch.vue

```
<template>
  <n-layout-content content-style="padding: 24px;">
    <n-h2>
      搜索“<n-text type="primary">{{ keyword }}</n-text>”的结果
    </n-h2>

    <n-spin :show="searchStore.isLoading">
      <n-grid v-if="movies.length > 0" :x-gap="16" :y-gap="24" :cols="'2 s:3 m:4 l:5 xl:6'" responsive="true">
        <n-grid-item v-for="movie in movies" :key="movie.id">
          <router-link :to="{ name: 'MovieDetail', params: { id: movie.id } }">
            <n-card :title="movie.title" hoverable content-style="padding:0;">
              <template #cover>
                <img :src="movie.posterUrl" class="movie-poster-img">
              </template>
            </n-card>
          </router-link>
        </n-grid-item>
      </n-grid>
      <n-empty v-else description="没有找到相关的电影。">
      </n-empty>
    </n-spin>

  </n-layout-content>
</template>

<script setup>
import { ref, onMounted, watch } from 'vue';
import apiService from '@/services/apiService';
import { NLayoutContent, NH2, NText, NSpin, NGrid, NGridItem, NCard, NEmpty } from 'naive-ui';

// 1. 引入我们新创建的 store
import { useSearchStore } from '@/stores/searchStore';

const props = defineProps({
  keyword: String,
});

const movies = ref([]);
// 2. 移除此组件内部的 loading 状态，因为它现在由 store 统一管理
// const loading = ref(false); 

// 3. 初始化 store
const searchStore = useSearchStore();

const fetchMovies = async (name) => {
  if (!name) return;

  // 4. 开始获取数据前，调用 store 的方法，将全局加载状态设为 true
  searchStore.startLoading();
  try {
    const actorRes = await apiService.searchMovies({ type: 'by-actor', name });
    const directorRes = await apiService.searchMovies({ type: 'by-director', name });

    const allMovies = [...actorRes.data, ...directorRes.data];
    movies.value = Array.from(new Map(allMovies.map(m => [m.id, m])).values());

  } catch (error) {
    console.error('搜索电影时出错:', error);
  } finally {
    // 5. 不论成功或失败，获取数据结束后，都调用 store 的方法，将全局加载状态设为 false
    searchStore.stopLoading();
  }
};

onMounted(() => {
  fetchMovies(props.keyword);
});

watch(() => props.keyword, (newKeyword) => {
  fetchMovies(newKeyword);
});
</script>

<style scoped>
.movie-poster-img {
  width: 100%;
  aspect-ratio: 2 / 3;
  object-fit: cover;
}

:deep(.n-card-header__main) {
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

a {
  text-decoration: none;
  color: inherit;
}
</style>
```

# frontend\src\views\UserProfile.vue

```
<template>
  <n-layout-content content-style="padding: 24px; max-width: 900px; margin: auto;">
    <n-card>
      <n-space align="center">
        <n-avatar :size="120" :src="user.profileImageUrl" />
        <n-space vertical :size="12">
          <n-h1 style="margin: 0;">{{ user.username }}</n-h1>

          <div v-if="!isEditingBio" @click="startEditingBio" style="cursor: pointer; min-height: 24px;"
            title="点击编辑签名">
            <n-p depth="3">{{ user.bio || '这位用户很神秘，什么都没留下...' }}</n-p>
          </div>
          <div v-else>
            <n-input-group>
              <n-input ref="bioInputRef" type="textarea" v-model:value="editingBioText" placeholder="输入您的新签名..."
                :autosize="{ minRows: 1, maxRows: 3 }" />
              <n-button type="primary" @click="saveBio" :loading="isSavingBio">保存</n-button>
              <n-button @click="cancelEditingBio">取消</n-button>
            </n-input-group>
          </div>
          <n-space>
            <n-button tag="a" :href="user.personalWebsite" target="_blank" v-if="user.personalWebsite" text
              type="primary">
              <template #icon>
                <n-icon :component="LinkIcon" />
              </template>
              访问个人网站
            </n-button>
          </n-space>
        </n-space>
      </n-space>
    </n-card>

    <n-h2 prefix="bar">
      <n-text type="primary">我的影评</n-text>
    </n-h2>
    <n-spin :show="loadingReviews">
      <n-list bordered>
        <n-list-item v-for="review in myReviews" :key="review.id">
          <n-thing>
            <template #header>
              对电影
              <router-link :to="{ name: 'MovieDetail', params: { id: review.movieId } }">
                《{{ review.movieTitle }}》
              </router-link>
              的评论
            </template>
            <template #description>
              <n-text depth="3">发表于: {{ new Date(review.createdAt).toLocaleString() }}</n-text>
            </template>
            {{ review.commentText }}
          </n-thing>
        </n-list-item>
        <n-list-item v-if="myReviews.length === 0">
          <n-empty description="还没有发表过任何评论。" />
        </n-list-item>
      </n-list>
    </n-spin>

  </n-layout-content>
</template>

<script setup>
import { ref, onMounted, nextTick } from 'vue';
import { useAuthStore } from '@/stores/authStore';
import apiService from '@/services/apiService';
import { NLayoutContent, NCard, NSpace, NAvatar, NH1, NH2, NP, NText, NButton, NIcon, NList, NListItem, NThing, NSpin, NEmpty, NInput, NInputGroup, useMessage } from 'naive-ui';
import { Link as LinkIcon } from '@vicons/ionicons5';

const authStore = useAuthStore();
const message = useMessage();
const user = ref({});
const myReviews = ref([]);
const loadingReviews = ref(true);

// --- 新增：用于原地编辑签名的状态 ---
const isEditingBio = ref(false);
const isSavingBio = ref(false);
const editingBioText = ref('');
const bioInputRef = ref(null); // 用于在编辑时自动聚焦输入框

// 开始编辑
const startEditingBio = async () => {
  isEditingBio.value = true;
  editingBioText.value = user.value.bio || '';
  // 等待 DOM 更新后，聚焦到输入框
  await nextTick();
  bioInputRef.value?.focus();
};

// 取消编辑
const cancelEditingBio = () => {
  isEditingBio.value = false;
};

// 保存编辑
const saveBio = async () => {
  isSavingBio.value = true;
  try {
    // 准备提交给后端的数据包，包含所有可编辑字段
    const payload = {
      username: user.value.username,
      personalWebsite: user.value.personalWebsite,
      birthDate: user.value.birthDate,
      bio: editingBioText.value, // 使用编辑后的新签名
    };
    await apiService.updateUserProfile(payload);

    // 更新成功后，同步本地状态
    user.value.bio = editingBioText.value;
    authStore.user.bio = editingBioText.value;
    localStorage.setItem('user', JSON.stringify(authStore.user));
    
    message.success('签名更新成功！');
    isEditingBio.value = false; // 退出编辑状态
  } catch (error) {
    message.error('保存失败，请重试');
    console.error(error);
  } finally {
    isSavingBio.value = false;
  }
};
// --- 新增逻辑结束 ---

onMounted(async () => {
  if (authStore.user) {
    user.value = { ...authStore.user }; // 使用副本以避免意外修改
  }
  
  loadingReviews.value = true;
  try {
    const response = await apiService.getReviewsByUserId(authStore.userId);
    myReviews.value = response.data;
  } catch (error) {
    console.error('加载评论失败:', error);
  } finally {
    loadingReviews.value = false;
  }
});
</script>

<style scoped>
a {
  text-decoration: none;
  color: #36ad6a;
  font-weight: bold;
}
a:hover {
  text-decoration: underline;
}
</style>
```

# frontend\src\views\UserReviews.vue

```
<template>
    <n-layout-content content-style="padding: 24px;">
      <n-h2>我的评论</n-h2>
      <n-spin :show="loading">
        <n-list bordered>
          <n-list-item v-for="review in myReviews" :key="review.id">
            <n-thing>
              <template #header>
                对电影《{{ review.movieTitle }}》的评论
              </template>
              <template #header-extra>
                <n-space>
                  <n-button size="small" type="primary" ghost @click="openEditModal(review)">编辑</n-button>
                  <n-popconfirm @positive-click="handleDelete(review.id)">
                    <template #trigger>
                      <n-button size="small" type="error" ghost>删除</n-button>
                    </template>
                    确定要删除这条评论吗？
                  </n-popconfirm>
                </n-space>
              </template>
              <template #description>
                <n-text depth="3">发表于: {{ new Date(review.createdAt).toLocaleString() }}</n-text>
              </template>
              {{ review.commentText }}
            </n-thing>
          </n-list-item>
          <n-list-item v-if="myReviews.length === 0">
            <n-empty description="您还没有发表过任何评论。" />
          </n-list-item>
        </n-list>
      </n-spin>
    </n-layout-content>
  
    <n-modal v-model:show="showEditModal" preset="card" style="width: 600px" title="编辑评论">
      <n-input
          v-model:value="editingCommentText"
          type="textarea"
          placeholder="输入新的评论内容..."
          :autosize="{ minRows: 5 }"
      />
      <template #footer>
        <n-flex justify="end">
          <n-button @click="showEditModal = false">取消</n-button>
          <n-button type="primary" @click="handleUpdate">保存修改</n-button>
        </n-flex>
      </template>
    </n-modal>
  </template>
  
  <script setup>
  import { ref, onMounted } from 'vue';
  import { useAuthStore } from '@/stores/authStore';
  import apiService from '@/services/apiService';
  // import TheHeader from '@/components/TheHeader.vue';
  import { 
      NLayoutContent, NH2, NList, NListItem, NThing, NButton, NSpace, 
      NPopconfirm, NSpin, NEmpty, NModal, NInput, NFlex, NText, useMessage 
  } from 'naive-ui';
  
  const authStore = useAuthStore();
  const message = useMessage();
  const myReviews = ref([]);
  const loading = ref(true);
  
  // 编辑模态框相关
  const showEditModal = ref(false);
  const editingReviewId = ref(null);
  const editingCommentText = ref('');
  
  const fetchMyReviews = async () => {
    loading.value = true;
    try {
      const response = await apiService.getReviewsByUserId(authStore.userId);
      myReviews.value = response.data;
    } catch (error) {
      message.error('加载我的评论失败');
      console.error(error);
    } finally {
      loading.value = false;
    }
  };
  
  const openEditModal = (review) => {
    editingReviewId.value = review.id;
    editingCommentText.value = review.commentText;
    showEditModal.value = true;
  };
  
  const handleUpdate = async () => {
    if (!editingCommentText.value.trim()) {
      message.warning('评论内容不能为空');
      return;
    }
    try {
      await apiService.updateReview(editingReviewId.value, editingCommentText.value);
      message.success('评论更新成功');
      showEditModal.value = false;
      fetchMyReviews(); // 重新加载列表
    } catch (error) {
      message.error('更新失败');
    }
  };
  
  const handleDelete = async (reviewId) => {
    try {
      await apiService.deleteReview(reviewId);
      message.success('删除成功');
      fetchMyReviews(); // 重新加载列表
    } catch (error) {
      message.error('删除失败');
    }
  };
  
  onMounted(() => {
    if (authStore.isAuthenticated) {
      fetchMyReviews();
    }
  });
  </script>
```

