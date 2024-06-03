# Change Log

## 4.0.0

- 新版本重构UI流程
- 随着jdk版本升高，删除lombok

## 3.3.0

- 对新版本兼容已经捉襟见肘,提升最低版本为231

## 3.2.2

- 临时兼容23.3

## 3.2.1

- 优化代码，优化UI

## 3.2.0

- 升级依赖
- 优化代码，优化UI
- 修改到io.github.hdzitao包名下
- OHub功能，支持spring boot旧版本

## 3.1.0

- 修改到com.github.hdzitao包名下(功能未变,未发布)

## 3.0.0

- 最低版本为201
- 支持spring boot旧版本(预览)
- 本地缓存

## 2.4.0

- 完善scope在不同build工具下的对应关系

## 2.3.0

- 修复maven添加bom时dependencyManagement和dependencies标签弄反的错误
- 修复新版本中依赖描述不换行的错误，修改错误提示模板
- 添加对gradle kotlin dsl中maven标签新语法的支持
- 修改boot版本是否支持的逻辑：按boot三位数字版本号请求，只要正常返回即认为是支持版本