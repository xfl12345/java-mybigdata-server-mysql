# 注意事项

+ 编译
  - 如果要编译 javadoc ，系统环境需要设置 JAVA_TOOL_OPTIONS 为 "-Dfile.encoding=UTF-8"
  - 然而 javadoc 并没有按要求完善，所以是编译不了的

+ 限制
  - 由于 JAVA 语言自身限制，数组 和 对象 的成员都不能超过 2^30 个（前期暂时不解决这个问题）
