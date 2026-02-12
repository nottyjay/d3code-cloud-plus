@echo off
chcp 65001 > nul
setlocal enabledelayedexpansion

REM ========================================
REM D3code-Cloud-Plus Docker 镜像构建脚本
REM ========================================
REM 用法:
REM   build.bat                    # 使用默认版本号
REM   build.bat 1.0.0             # 指定版本号
REM   build.bat 1.0.0 dev         # 指定版本号和环境
REM ========================================

echo.
echo ========================================
echo   D3code-Cloud-Plus Docker 镜像构建工具
echo ========================================
echo.

REM 获取版本号参数
set VERSION=%1
if "%VERSION%"=="" (
    echo [INFO] 未指定版本号，使用默认版本: 1.0.0
    set VERSION=1.0.0
) else (
    echo [INFO] 使用指定版本号: %VERSION%
)

REM 获取环境参数（可选）
set PROFILE=%2
if "%PROFILE%"=="" (
    echo [INFO] 未指定环境，默认使用: prod
    set PROFILE=prod
) else (
    echo [INFO] 使用指定环境: %PROFILE%
)

echo.
echo [INFO] 构建参数:
echo   - 版本号: %VERSION%
echo   - 环境:   %PROFILE%
echo.

REM ========================================
REM 第一步: Maven 打包
REM ========================================
echo.
echo [STEP 1/3] 开始 Maven 打包...
echo.

if not exist "mvnw.cmd" (
    echo [ERROR] 找不到 mvnw.cmd，请确保在项目根目录执行此脚本
    goto :error_exit
)

call mvnw.cmd clean package -P%PROFILE% -DskipTests=true

if %ERRORLEVEL% neq 0 (
    echo.
    echo [ERROR] Maven 打包失败，错误代码: %ERRORLEVEL%
    goto :error
) else (
    echo.
    echo [SUCCESS] Maven 打包完成
)

REM ========================================
REM 第二步: 检查构建产物
REM ========================================
echo.
echo [STEP 2/3] 检查构建产物...
echo.

REM 检查各个模块的 jar 文件
set MISSING_JARS=0

if not exist "d3code-auth\target\d3code-auth.jar" (
    echo [WARN] d3code-auth.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-gateway\target\d3code-gateway.jar" (
    echo [WARN] d3code-gateway.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-modules\d3code-system\target\d3code-system.jar" (
    echo [WARN] d3code-system.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-modules\d3code-job\target\d3code-job.jar" (
    echo [WARN] d3code-job.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-modules\d3code-resource\target\d3code-resource.jar" (
    echo [WARN] d3code-resource.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-visual\d3code-monitor\target\d3code-monitor.jar" (
    echo [WARN] d3code-monitor.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-visual\d3code-sentinel-dashboard\target\d3code-sentinel-dashboard.jar" (
    echo [WARN] d3code-sentinel-dashboard.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-visual\d3code-seata-server\target\d3code-seata-server.jar" (
    echo [WARN] d3code-seata-server.jar 不存在
    set /a MISSING_JARS+=1
)
if not exist "d3code-visual\d3code-snailjob-server\target\d3code-snailjob-server.jar" (
    echo [WARN] d3code-snailjob-server.jar 不存在
    set /a MISSING_JARS+=1
)

if %MISSING_JARS% gtr 0 (
    echo.
    echo [WARN] 发现 %MISSING_JARS% 个 jar 文件不存在，但继续构建 Docker 镜像...
) else (
    echo [SUCCESS] 所有 jar 文件检查通过
)

REM ========================================
REM 第三步: 构建 Docker 镜像
REM ========================================
echo.
echo [STEP 3/3] 开始构建 Docker 镜像...
echo.

set BUILD_SUCCESS=0
set BUILD_FAILED=0

REM 构建 d3code-auth
echo.
echo [BUILD] 构建镜像: d3code-auth:%VERSION%
docker build -f d3code-auth\Dockerfile -t d3code/d3code-auth:%VERSION% d3code-auth
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-auth:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-auth:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-gateway
echo.
echo [BUILD] 构建镜像: d3code-gateway:%VERSION%
docker build -f d3code-gateway\Dockerfile -t d3code/d3code-gateway:%VERSION% d3code-gateway
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-gateway:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-gateway:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-system
echo.
echo [BUILD] 构建镜像: d3code-system:%VERSION%
docker build -f d3code-modules\d3code-system\Dockerfile -t d3code/d3code-system:%VERSION% d3code-modules\d3code-system
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-system:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-system:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-job
echo.
echo [BUILD] 构建镜像: d3code-job:%VERSION%
docker build -f d3code-modules\d3code-job\Dockerfile -t d3code/d3code-job:%VERSION% d3code-modules\d3code-job
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-job:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-job:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-resource
echo.
echo [BUILD] 构建镜像: d3code-resource:%VERSION%
docker build -f d3code-modules\d3code-resource\Dockerfile -t d3code/d3code-resource:%VERSION% d3code-modules\d3code-resource
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-resource:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-resource:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-monitor
echo.
echo [BUILD] 构建镜像: d3code-monitor:%VERSION%
docker build -f d3code-visual\d3code-monitor\Dockerfile -t d3code/d3code-monitor:%VERSION% d3code-visual\d3code-monitor
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-monitor:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-monitor:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-sentinel-dashboard
echo.
echo [BUILD] 构建镜像: d3code-sentinel-dashboard:%VERSION%
docker build -f d3code-visual\d3code-sentinel-dashboard\Dockerfile -t d3code/d3code-sentinel-dashboard:%VERSION% d3code-visual\d3code-sentinel-dashboard
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-sentinel-dashboard:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-sentinel-dashboard:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-seata-server
echo.
echo [BUILD] 构建镜像: d3code-seata-server:%VERSION%
docker build -f d3code-visual\d3code-seata-server\Dockerfile -t d3code/d3code-seata-server:%VERSION% d3code-visual\d3code-seata-server
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-seata-server:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-seata-server:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM 构建 d3code-snailjob-server
echo.
echo [BUILD] 构建镜像: d3code-snailjob-server:%VERSION%
docker build -f d3code-visual\d3code-snailjob-server\Dockerfile -t d3code/d3code-snailjob-server:%VERSION% d3code-visual\d3code-snailjob-server
if %ERRORLEVEL% equ 0 (
    echo [SUCCESS] d3code-snailjob-server:%VERSION% 构建成功
    set /a BUILD_SUCCESS+=1
) else (
    echo [ERROR] d3code-snailjob-server:%VERSION% 构建失败
    set /a BUILD_FAILED+=1
)

REM ========================================
REM 构建结果汇总
REM ========================================
echo.
echo ========================================
echo   构建结果汇总
echo ========================================
echo.
echo   成功: %BUILD_SUCCESS% 个
echo   失败: %BUILD_FAILED% 个
echo.

if %BUILD_FAILED% equ 0 (
    echo [SUCCESS] 所有镜像构建成功！
    echo.
    echo 构建的镜像列表:
    echo   - d3code/d3code-auth:%VERSION%
    echo   - d3code/d3code-gateway:%VERSION%
    echo   - d3code/d3code-system:%VERSION%
    echo   - d3code/d3code-job:%VERSION%
    echo   - d3code/d3code-resource:%VERSION%
    echo   - d3code/d3code-monitor:%VERSION%
    echo   - d3code/d3code-sentinel-dashboard:%VERSION%
    echo   - d3code/d3code-seata-server:%VERSION%
    echo   - d3code/d3code-snailjob-server:%VERSION%
    echo.
    echo 推送到私有仓库命令示例:
    echo   docker push d3code/d3code-auth:%VERSION%
    echo   docker push d3code/d3code-gateway:%VERSION%
    echo   docker push d3code/d3code-system:%VERSION%
    echo   docker push d3code/d3code-job:%VERSION%
    echo   docker push d3code/d3code-resource:%VERSION%
    echo   docker push d3code/d3code-monitor:%VERSION%
    echo   docker push d3code/d3code-sentinel-dashboard:%VERSION%
    echo   docker push d3code/d3code-seata-server:%VERSION%
    echo   docker push d3code/d3code-snailjob-server:%VERSION%
    echo.
) else (
    echo [ERROR] 有 %BUILD_FAILED% 个镜像构建失败，请检查错误信息
    goto :error_exit
)

goto :end

:error
echo.
echo [ERROR] 构建过程中出现错误，脚本终止
goto :error_exit

:error_exit
echo.
echo [ERROR] 构建失败
exit /b 1

:end
echo.
echo [INFO] 构建脚本执行完毕
exit /b 0
