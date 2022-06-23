# Change Log

## 3.0.0

- 支持start.spring.io不再支持的spring boot版本
- kotlin代码转回java
- 支持start.aliyun.com
- 请求一次后保留缓存

## 2.4.0

- 完善scope在不同build工具下的对应关系
- 重构项目结构

## 2.3.0

- 修复maven添加bom时dependencyManagement和dependencies标签弄反的错误
- 修复新版本中依赖描述不换行的错误，修改错误提示模板
- 添加对gradle kotlin dsl中maven标签新语法的支持
- 修改boot版本是否支持的逻辑：按boot三位数字版本号请求，只要正常返回即认为是支持版本