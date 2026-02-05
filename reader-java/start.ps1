# Reader Java版启动脚本

# 设置Java 17环境
$env:JAVA_HOME="C:\Users\92872\.jdks\corretto-17.0.17"

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Reader - Java版本" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Java版本: " -NoNewline
java -version 2>&1 | Select-Object -First 1
Write-Host ""

# 检查参数
$action = $args[0]

if ($action -eq "compile") {
    Write-Host "正在编译项目..." -ForegroundColor Yellow
    mvn clean compile
} elseif ($action -eq "run") {
    Write-Host "正在启动应用..." -ForegroundColor Yellow
    mvn spring-boot:run
} elseif ($action -eq "package") {
    Write-Host "正在打包项目..." -ForegroundColor Yellow
    mvn clean package
} elseif ($action -eq "test") {
    Write-Host "正在运行测试..." -ForegroundColor Yellow
    mvn test
} else {
    Write-Host "使用方法:" -ForegroundColor Green
    Write-Host "  .\start.ps1 compile  - 编译项目" -ForegroundColor White
    Write-Host "  .\start.ps1 run      - 运行项目" -ForegroundColor White
    Write-Host "  .\start.ps1 package  - 打包项目" -ForegroundColor White
    Write-Host "  .\start.ps1 test     - 运行测试" -ForegroundColor White
    Write-Host ""
    Write-Host "示例:" -ForegroundColor Green
    Write-Host "  .\start.ps1 run" -ForegroundColor White
}
