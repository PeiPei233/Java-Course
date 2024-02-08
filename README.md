# Java-Course

笔者是 23-24 秋冬上的鲁伟明老师的 Java。感觉是整个学期最轻松的一节课。这个学期 lwm 一次都没有点过名，而且 lwm 的 ppt 非常的详细，由于期末考时间安排过于逆天，考 Java 前有整整两天半复习时间，刚好过一遍 lwm 的 ppt，刷了几套题就考试了。

考试题目也都挺水的，刷过历年卷的话，应该不会有太大问题。另外虽然是开卷，笔者带的是卷 1，但是考试的时候压根没用上，而且不会的都是卷 2 上的，主要是和 stream 相关的题目。还好考的 stream 都是历年卷有的，建议把 stream 的 api 抄到自己带的书上。

另外 IDEA yyds，这次考试前允许我们装 IDEA，体验飞升。

## 平时作业

lwm 平时课程只有 5 个作业，总的来说难度都不大。

### Homework 1

不知道如何评价，总之就是送分的，要求配个 Java 环境，写个配环境的文档，写个类似于 Hello World 的程序。

### Homework 2

写个数独的生成和求解程序，主要是用到了递归，不难。

### Homework 3

要求分析 Java 库中的不变类，对比 String，StringBuilder 和 StringBuffer，然后自己实现一个不变类。

感觉这个作业挺好的，也是期末考爱考的。

### Homework 4

写一个 Web 爬虫，爬论文，解析 PDF 内容建立索引，然后提供搜索功能。

这个作业主要是调库。有个小坑坑了笔者一会儿。笔者一开始做的时候所有库装的都是最新的版本，结果有个库的新版的 api 变了，导致 ChatGPT 不会新的，而且笔者自己查了 api 文档发现新版文档甚至还在用老 api。

### Homework 5

写一个简单的 Web 应用，要求前后端都用 Java 写。

笔者也是写了个聊天室，技术栈是 SQLite -> Swing -> Socket -> MySQL。lwm 的 ddl 非常宽松，笔者期末考前先把前端 Swing 界面写完了，考试之后再写的其他部分。不过由于时间关系没有实现预想中的全部功能（支持传文件和视频），问题不大。笔者实现方式都想好了，可惜来不及了。