# Change Log

## 2.3
* 修复maven添加bom时dependencyManagement和dependencies标签弄反的错误
* 修复新版本中依赖描述不换行的错误，修改错误提示模板
* 添加对gradle kotlin dsl中maven标签新语法的支持
* 修改boot版本是否支持的逻辑：按boot三位数字版本号请求，只要正常返回即认为是支持版本

## v2.2.1
* 2020/8/13 update error message

## v2.2
* 2019/2/20 Convert all code to Kotlin
* 2019/3/5  support idea version since 181.*

## v2.1
* 2019/1/23 fix the bug that "Edit Starters" is in all "Generate" meun
* 2019/1/23 maven's "Edit Starters" move to "Generate"

## v2.0
* 2019/1/16 support gradle project
* 2019/1/18 support gradle project with kotlin dsl

## v1.2
* 2019/1/11 support maven project
* 2019/1/14 support search