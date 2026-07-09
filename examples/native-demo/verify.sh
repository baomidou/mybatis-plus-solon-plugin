#!/usr/bin/env bash
#
# mybatis-plus-solon-native-image 黑盒验收脚本
#
# 流程：GraalVM 实编 native 二进制 -> 启动 -> 对每个端点做内容断言 -> 汇总结果
# 用法：./verify.sh            （完整流程）
#       ./verify.sh --skip-build（跳过编译，直接验证已有 target/native-demo）
#
# 依赖：GRAALVM_HOME 指向含 native-image 的 GraalVM（jdk17+），未设置时使用默认路径
#
set -uo pipefail
cd "$(dirname "$0")"

PORT=18081
BASE="http://localhost:${PORT}"
BIN="target/native-demo"

# ---------- 1. 实编 ----------
if [[ "${1:-}" != "--skip-build" ]]; then
    : "${GRAALVM_HOME:=$HOME/Library/Java/JavaVirtualMachines/graalvm-community-openjdk-21.0.2+13.1/Contents/Home}"
    if [[ ! -x "$GRAALVM_HOME/bin/native-image" ]]; then
        echo "ERROR: 未找到 native-image，请设置 GRAALVM_HOME (当前: ${GRAALVM_HOME})" >&2
        exit 1
    fi
    echo "==> native 编译 (GRAALVM_HOME=${GRAALVM_HOME}) ..."
    JAVA_HOME="$GRAALVM_HOME" mvn -Pnative clean native:compile -DskipTests -q || {
        echo "ERROR: native 编译失败" >&2; exit 1; }
fi

[[ -x "$BIN" ]] || { echo "ERROR: 二进制不存在：$BIN" >&2; exit 1; }

# ---------- 2. 启动 ----------
if lsof -nP -iTCP:${PORT} -sTCP:LISTEN >/dev/null 2>&1; then
    echo "ERROR: 端口 ${PORT} 已被占用" >&2; exit 1
fi

"$BIN" > target/native-verify.log 2>&1 &
APP_PID=$!
trap 'kill $APP_PID 2>/dev/null' EXIT

for i in $(seq 1 50); do
    curl -s -o /dev/null --max-time 1 "$BASE/user/list" && break
    kill -0 $APP_PID 2>/dev/null || { echo "ERROR: 进程启动即退出，日志："; cat target/native-verify.log; exit 1; }
    sleep 0.2
done

# ---------- 3. 端点断言 ----------
PASS=0; TOTAL=0; FAILED=()

check() {  # check <路径> <期望包含的内容> <说明>
    TOTAL=$((TOTAL+1))
    local resp
    resp=$(curl -s --max-time 5 "$BASE/$1")
    if echo "$resp" | grep -q "$2"; then
        PASS=$((PASS+1)); echo "PASS  /$1  ($3)"
    else
        FAILED+=("/$1"); echo "FAIL  /$1  ($3)"
        echo "      期望包含: $2"
        echo "      实际响应: ${resp:0:200}"
    fi
}

check "user/list"                    '"name":"solon"'                 "LambdaQueryWrapper / SFunction 序列化"
check "user/page?current=1&size=2"   '"total":3'                      "分页插件 + jsqlparser"
check "user/adults"                  '"name":"native"'                "XML mapper 资源加载"
check "user/add?name=verify&age=99"  '"name":"verify","age":99'       "插入 + 自增主键回填"
check "user/get?id=4"                '"name":"verify"'                "getById 回读"
check "user/list"                    '"verify"'                       "数据一致性"

# ---------- 4. 汇总 ----------
echo
echo "启动耗时: $(grep -oE 'elapsed=[0-9]+ms' target/native-verify.log | head -1 || echo '未知')"
echo "断言通过: $PASS/$TOTAL"
if [[ $PASS -ne $TOTAL ]]; then
    echo "失败端点: ${FAILED[*]}"
    exit 1
fi
echo "native 黑盒验收通过 ✔"
