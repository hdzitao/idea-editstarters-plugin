# Change Log

## 2.4（doing）
* 完善scope在不同build工具下的对应关系
* 重构build文件写入代码

## 2.3
* 修复maven添加bom时dependencyManagement和dependencies标签弄反的错误
* 修复新版本中依赖描述不换行的错误，修改错误提示模板
* 添加对gradle kotlin dsl中maven标签新语法的支持
* 修改boot版本是否支持的逻辑：按boot三位数字版本号请求，只要正常返回即认为是支持版本