#!/bin/bash
# ========================================
# D3code-Cloud-Plus Docker 镜像构建脚本
# ========================================
# 用法:
#   ./build.sh                  # 使用默认版本号
#   ./build.sh 1.0.0           # 指定版本号
#   ./build.sh 1.0.0 dev       # 指定版本号和环境
# ========================================

set -e

# 获取版本号参数
VERSION=${1:-"1.0.0"}
PROFILE=${2:-"prod"}

echo ""
echo "========================================"
echo "  D3code-Cloud-Plus Docker 镜像构建工具"
echo "========================================"
echo ""
echo "[INFO] 构建参数:"
echo "  - 版本号: $VERSION"
echo "  - 环境:   $PROFILE"
echo ""

# ========================================
# 第一步: Maven 打包
# ========================================
echo ""
echo "[STEP 1/3] 开始 Maven 打包..."
echo ""

if [ ! -f "mvnw" ]; then
    echo "[ERROR] 找不到 mvnw，请确保在项目根目录执行此脚本"
    exit 1
fi

./mvnw clean package -P${PROFILE} -DskipTests=true

echo ""
echo "[SUCCESS] Maven 打包完成"

# ========================================
# 第二步: 检查构建产物
# ========================================
echo ""
echo "[STEP 2/3] 检查构建产物..."
echo ""

MISSING_JARS=0

for jar in \
    "d3code-auth/target/d3code-auth.jar" \
    "d3code-gateway/target/d3code-gateway.jar" \
    "d3code-modules/d3code-system/target/d3code-system.jar" \
    "d3code-modules/d3code-job/target/d3code-job.jar" \
    "d3code-modules/d3code-resource/target/d3code-resource.jar" \
    "d3code-visual/d3code-monitor/target/d3code-monitor.jar" \
    "d3code-visual/d3code-sentinel-dashboard/target/d3code-sentinel-dashboard.jar" \
    "d3code-visual/d3code-seata-server/target/d3code-seata-server.jar" \
    "d3code-visual/d3code-snailjob-server/target/d3code-snailjob-server.jar"
do
    if [ ! -f "$jar" ]; then
        echo "[WARN] $(basename $jar) 不存在"
        ((MISSING_JARS++))
    fi
done

if [ $MISSING_JARS -gt 0 ]; then
    echo ""
    echo "[WARN] 发现 $MISSING_JARS 个 jar 文件不存在，但继续构建 Docker 镜像..."
else
    echo "[SUCCESS] 所有 jar 文件检查通过"
fi

# ========================================
# 第三步: 构建 Docker 镜像
# ========================================
echo ""
echo "[STEP 3/3] 开始构建 Docker 镜像..."
echo ""

BUILD_SUCCESS=0
BUILD_FAILED=0

# 定义模块列表
MODULES=(
    "d3code-auth:d3code-auth"
    "d3code-gateway:d3code-gateway"
    "d3code-modules/d3code-system:d3code-system"
    "d3code-modules/d3code-job:d3code-job"
    "d3code-modules/d3code-resource:d3code-resource"
    "d3code-visual/d3code-monitor:d3code-monitor"
    "d3code-visual/d3code-sentinel-dashboard:d3code-sentinel-dashboard"
    "d3code-visual/d3code-seata-server:d3code-seata-server"
    "d3code-visual/d3code-snailjob-server:d3code-snailjob-server"
)

# 构建镜像
for module in "${MODULES[@]}"; do
    IFS=':' read -r path name <<< "$module"
    echo ""
    echo "[BUILD] 构建镜像: d3code/$name:$VERSION"

    if docker build -f "$path/Dockerfile" -t "d3code/$name:$VERSION" "$path"; then
        echo "[SUCCESS] d3code/$name:$VERSION 构建成功"
        ((BUILD_SUCCESS++))
    else
        echo "[ERROR] d3code/$name:$VERSION 构建失败"
        ((BUILD_FAILED++))
    fi
done

# ========================================
# 构建结果汇总
# ========================================
echo ""
echo "========================================"
echo "  构建结果汇总"
echo "========================================"
echo ""
echo "  成功: $BUILD_SUCCESS 个"
echo "  失败: $BUILD_FAILED 个"
echo ""

if [ $BUILD_FAILED -eq 0 ]; then
    echo "[SUCCESS] 所有镜像构建成功！"
    echo ""
    echo "构建的镜像列表:"
    for module in "${MODULES[@]}"; do
        IFS=':' read -r path name <<< "$module"
        echo "  - d3code/$name:$VERSION"
    done
    echo ""
    echo "推送到私有仓库命令示例:"
    for module in "${MODULES[@]}"; do
        IFS=':' read -r path name <<< "$module"
        echo "  docker push d3code/$name:$VERSION"
    done
    echo ""
else
    echo "[ERROR] 有 $BUILD_FAILED 个镜像构建失败，请检查错误信息"
    exit 1
fi

echo ""
echo "[INFO] 构建脚本执行完毕"
exit 0
