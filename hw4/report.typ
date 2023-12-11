#let song = ("Times New Roman", "SimSun")
#let hei = "SimHei"

#let cover(
  academic_year,
  semester,
  title,
  id,
  name,
  major,
  class,
) = {

  let info-key(body) = {
    rect(
      width: 100%,
      height: 20pt,
      inset: (x: 0pt, bottom: 1pt),
      stroke: none,
      text(
        font: hei,
        size: 14pt,
        body,
      ),
    )
  }

  let info-value(body) = {
    rect(
      width: 100%,
      height: 20pt,
      inset: (x: 0pt, bottom: 1pt),
      stroke: (bottom: 0.5pt),
      text(
        font: hei,
        size: 14pt,
        body,
      ),
    )
  }

  pagebreak(weak: true)

  set align(center)

  v(100pt)
  
  text(
    font: hei,
    size: 22pt,
  )[浙江大学计算机学院]

  v(22pt)

  text(
    font: song,
    size: 16pt,
  )[Java 程序设计课程报告]

  v(16pt)

  text(
    font: song,
    size: 14pt,
  )[#academic_year 学年 #semester 学期]

  v(120pt)

  set text(font: hei, size: 15pt)
  table(
    columns: (75pt, 300pt),
    row-gutter: 10pt,
    stroke: none,
    info-key("题目"), info-value(title),
    info-key("学号"), info-value(id),
    info-key("学生姓名"), info-value(name),
    info-key("所在专业"), info-value(major),
    info-key("所在班级"), info-value(class),
  )

  pagebreak(weak: true)
}

#let outline-page() = {
  pagebreak(weak: true)

  set align(center)

  v(14pt)

  text(
    font: hei,
    size: 14pt,
  )[#{"目    录"}]

  v(14pt)

  set par(leading: 12pt)

  set text(font: song, size: 12pt)

  outline(
    title: none,
  )

  pagebreak(weak: true)
}

#let fake-par = {
  v(-1em)
  box()
}

#set heading(numbering: "1.1 ")

#show heading.where(
  level: 1
) : it => {
  pagebreak(weak: true)
  text(
    font: hei,
    size: 14pt,
    weight: "bold",
  )[#it]
  v(18pt)
  fake-par
}

#show heading: it => {
  v(4pt)
  text(
    font: hei,
    size: 12pt,
    weight: "bold",
  )[#it]
  v(12pt)
  fake-par
}

#set text(
  font: song,
  size: 12pt,
)

#set par(
  leading: 10pt,
  first-line-indent: 2em,
  justify: true
)

#show parbreak: {
  v(-4pt)
}

#set page(paper: "a4", numbering: (..numbers) => {
  if numbers.pos().at(0) > 2 {
    numbering("1", numbers.pos().at(0) - 2)
  }
})

#set enum(indent: 2em)
#set list(indent: 2em)

#show enum : it => {
  it
  v(-2pt)
  fake-par
}

#set figure(
  supplement: "图",
  gap: 1em
)
#set figure.caption(separator: "  ")
#show figure.caption: it => {
  text(
    font: song,
    size: 10.5pt,
    weight: "bold",
  )[#it]
}

#show figure: it => {
  it
  fake-par
}

#show raw: it => {
  text(
    font: "Lucida Sans Typewriter",
  )[#it]
}

#cover(
  "2023 - 2024",
  "秋冬",
  "Scholar Search Engine",
  "3210106360",
  "杨沛山",
  "计算机科学与技术",
  "计科2104",
)

#outline-page()


= 引言

随着科技的快速发展和学术研究的不断深入，获取和处理大量学术文献成为学者和研究人员的一项重要任务。传统的文献检索方式往往耗时且效率低下，这促使了对更为高效、智能的学术搜索引擎的需求。本项目旨在开发一个学术搜索引擎，能够爬取、索引和检索学术文献。通过这个搜索引擎，用户可以便捷地查找相关论文，从而支持学术研究和文献回顾工作。这是一个综合性的项目，需要涉及到爬虫、数据库、检索算法、前端等多个方面的知识，可以让我们对 Java 语言的应用有一个更加深入的了解，提高我们的编程能力。

== 设计目的

本项目旨在开发一个高效的学术搜索引擎，能够自动爬取、索引和检索 arxiv 上的学术文献。通过这个搜索引擎，用户可以快速准确地找到所需的学术资源，大大提高学术研究的效率。具体而言，我们需要实现的功能如下：
+ 写一个 Web 爬虫，能够自动爬取 arxiv 上的学术文献的网站以及 PDF 文件。
+ 解析网页内容，提取出论文的标题、作者、摘要、关键词、引用等信息。
+ 解析 PDF 文件，提取出论文的正文内容。
+ 将提取到的信息建立索引
+ 通过命令行进行文件内容检索，并展示内容列表
  - 可通过作者、标题、摘要、会议来检索论文

== 设计说明

本程序采用 Java 程序设计语言，在 IntelliJ IDEA 开发环境下进行开发。具体程序由报告作者独立开发完成。

= 总体设计

== 功能模块设计

本程序需实现的主要功能有：
+ 用户可以通过本程序爬取 arxiv 上的最新论文来更新本地的索引。
+ 用户可以通过本程序对本地的索引进行检索，以查找所需的论文。
+ 用户在检索时可以通过作者、标题、摘要、会议等信息来检索论文。

本项目根据用户需求，主要包括以下几个模块：
+ 爬虫模块：负责爬取 arxiv 上的论文网页和 PDF 文件。
+ 解析模块：负责解析爬取到的网页和 PDF 文件，提取出论文的标题、作者、摘要、关键词、引用等信息。
+ 索引模块：负责将解析到的信息建立索引。
+ 检索模块：负责通过命令行进行文件内容检索，并展示内容列表。

程序的主要模块如@系统模块架构图 所示。

#figure(
  image("assets/structure.png", width: 80%),
  caption: "系统模块架构图"
) <系统模块架构图>

== 流程图设计

程序总体流程图如@总体流程图 所示。

#figure(
  image("assets/flowchart.png", width: 80%),
  caption: "总体流程图"
) <总体流程图>

= 详细设计

== 爬虫模块设计

本项目的爬虫模块主要负责爬取 arxiv 上的论文网页和 PDF 文件。

本项目的网页爬虫基于 `crawler4j` 框架开发，用于爬取 arxiv 上的论文网页。爬虫配置 `CrawlerConfig` 设定了爬虫的一些基本参数，如爬取的网页数量、爬取的网页深度、爬虫的储存路径等。

爬虫类 `ArxivCrawler` 继承自 `WebCrawler` 类，重写了 `shouldVisit` 和 `visit` 方法。`shouldVisit` 方法用于判断当前网页是否应该被爬取，在该方法中，我们通过正则表达式，排除了与论文无关的网页，如 CSS 文件、图片文件等等，并通过字符串匹配前缀确保爬虫只爬取论文网页。

`visit` 方法用于处理爬取到的网页，使用 `Jsoup` 解析网页内容，提取出论文的标题、作者、摘要以及 PDF 文件的下载链接。爬取到的网页内容会被保存到 `PDFDoc` 类中，该类包含了上述提取出的信息。为了防止重复，我们使用 `HashSet` 来保存已经爬取过的网页链接，用于后续的 PDF 内容解析与索引建立。

爬虫模块流程如@爬虫模块流程图 所示。

#figure(
  image("assets/crawler.png", height: 45%),
  caption: "爬虫模块流程图"
) <爬虫模块流程图>

== 解析模块设计

PDF 文件解析模块主要负责解析爬取到的 PDF 文件，提取出论文的正文内容。

该模块使用了 `Apache PDFBox` 库来解析 PDF 文件。在 `PDFDoc` 类中，我们使用方法 `parse` 来下载并解析类内储存的 `url` 指向的 PDF 文件。解析过程包括使用 HTTP 连接下载 PDF 文件并将其加载到 `PDDocument` 对象中。然后，使用 `PDFTextStripper` 类提取PDF文档的文本内容。最后，将提取到的文本内容保存到 `PDFDoc` 类中的 `text` 字段中。

解析模块流程如@解析模块流程图 所示。

#figure(
  image("assets/parser.png", height: 35%),
  caption: "解析模块流程图"
) <解析模块流程图>

== 索引模块设计

索引模块主要负责将解析到的信息建立索引。

索引构建使用 `Apache Lucene` 库。`PDFIndexer` 类负责将解析的PDF文档转换为Lucene文档（ `Document` ），并将其添加到索引中。每个文档包括标题、作者、摘要、文本内容和 PDF url 等字段。在该类中有以下几个方法：
+ 构造函数：初始化索引目录和分词器和 `IndexWriter`。我们使用了 `StandardAnalyzer` 对文本进行分析，包括标记化、去除停用词等处理，以优化搜索的准确性和效率。
+ `addDocument` 方法：将解析的 PDF 文档添加到索引中，包括标题、作者、摘要、文本内容和 PDF url 五个字段。
+ `close` 方法：关闭 `IndexWriter` 对象，释放资源。

索引构建过程考虑了批量处理的需求，能够有效地处理大量文档，并确保索引构建的性能和稳定性。

索引模块流程如@索引模块流程图 所示。

#figure(
  image("assets/indexer.png", height: 35%),
  caption: "索引模块流程图"
) <索引模块流程图>

== 检索模块设计

检索模块主要负责通过命令行进行文件内容检索，并展示内容列表。

检索系统同样基于 `Apache Lucene` 库。`PDFSearcher` 类实现了对已构建索引的查询功能。使用 `StandardAnalyzer` 对用户的查询进行分析，并创建了Lucene查询（ `Query` ）。查询结果通过 `TopDocs` 和 `ScoreDoc` 对象返回。之后，再将查询结果高亮处理后打印到命令行中。

尽管是命令行程序，为了提高用户体验，使用了 `HighLighter` 类对查询结果进行高亮处理，标注查询关键词在文本中的位置，便于用户快速定位信息。同时，对每个匹配的文档显示了标题、作者和PDF链接，以及匹配的文本片段。

在 `PDFSearcher` 类中有以下几个方法：
+ 构造函数：初始化索引目录和分词器和 `IndexSearcher`。同样地，我们使用了 `StandardAnalyzer` 对文本进行分析。
+ `searchText` 方法：查询文本内容，将查询结果打印到命令行中。查询结果包括标题、作者、PDF链接和匹配的文本片段。其中，匹配的文本片段使用 `HighLighter` 类进行高亮处理。
+ `searchAuthor` 方法：查询作者，将查询结果打印到命令行中。查询结果包括标题、作者、PDF链接。同样地，作者中相匹配的字段会进行高亮处理。
+ `searchTitle` 方法：查询标题，将查询结果打印到命令行中。查询结果包括标题、作者、PDF链接。同样地，标题中相匹配的字段会进行高亮处理。
+ `searchAbstract` 方法：查询摘要，将查询结果打印到命令行中。查询结果包括标题、作者、PDF链接以及匹配的摘要中的文本片段。同样地，摘要中相匹配的字段会进行高亮处理。

检索模块流程如@检索模块流程图 所示。

#figure(
  image("assets/searcher.png", height: 40%),
  caption: "检索模块流程图"
) <检索模块流程图>

= 测试与运行

== 程序测试

在完成学术搜索引擎的核心代码开发后，进行了一系列的测试来确保程序的稳定性和功能性。首先对网络爬虫模块进行了测试，验证了其能够有效地爬取指定学术网站的文献页面和PDF文件。在测试中发现了一些小问题，例如若在 https://arxiv.org/list 网页尝试爬取摘要会导致解析错误，因此我们只能在https://arxiv.org/abs 爬取需要的信息。经过调整和优化后，爬虫模块表现稳定。

PDF解析和索引构建模块也经过了严格测试。测试中发现PDF解析时无法提取到作者标题，通过调整爬虫模块，从网页中获取论文标题、作者和摘要来解决该问题。索引构建模块能够高效地处理大量文档，并支持快速检索。

检索系统模块在用户界面和检索功能上都进行了综合测试。初期测试中发现了 Lucene Highlighter 默认不支持命令行的高亮。经过修改后，通过在高亮部分前后加上 ANSI 转义字符，最终实现了一个既直观又高效的用户检索体验。

总体来说，尽管在测试过程中遇到了一些挑战和问题，但通过团队的共同努力，大部分问题都得到了解决，使得整个系统在功能和性能上都达到了预期目标。但还有一些细节需要在未来的迭代中进一步完善和优化。

== 程序运行

由于索引和检索模块通常不一起运行，因此我们采用命令行参数来区分两个模块。在命令行中，用户可以通过 `index` 和 `search` 来分别运行索引和检索模块。

=== 索引模块

使用 `java -jar ScholarSearchEngine.jar index` 命令来运行索引模块。在运行时，程序会提示用户输入爬取的网页数量和爬取的网页深度。程序会自动爬取 arxiv 上的论文网页和 PDF 文件，并将解析到的信息建立索引。索引文件会保存在 `data/index` 目录下。在创建索引过程中，程序会自动在控制台输出一些日志。在索引结束时，程序会提示用户索引的文档数量。

例如，在本次作业一起提交的索引后的文件使用的是 `java -jar ScholarSearchEngine.jar index` 命令生成的，最终打印的日志中说明了总共索引的文档数量为 956，如@索引模块运行示例 所示。

#figure(
  image("assets/2023-12-11-22-13-35.png", width: 80%),
  caption: "索引模块运行示例"
) <索引模块运行示例>

=== 检索模块

使用 `java -jar ScholarSearchEngine.jar search` 命令来运行检索模块。在运行时，程序会提示用户输入检索的关键词。程序会自动在索引中检索关键词，并将检索结果打印到命令行中。

在打开程序后，程序会在控制台输出以下欢迎信息：

#figure(
  image("assets/2023-12-11-22-13-15.png", width: 80%),
  caption: "",
)

用户可以通过输入 `help` 来查看帮助信息，如@帮助信息 所示。

#figure(
  image("assets/2023-12-11-22-15-32.png", width: 60%),
  caption: "帮助信息"
) <帮助信息>

用户可以通过输入 `author`、`title`、`abstract`、`text` 来指定检索的字段。例如，用户可以通过输入 `author Yann` 来检索作者中含有 `Yann` 的论文，如@检索作者运行示例 所示。

#figure(
  image("assets/2023-12-11-20-47-47.png", width: 95%),
  caption: "检索作者运行示例"
) <检索作者运行示例>

用户可以通过输入 `title 4d` 来检索标题中含有 `4d` 的论文，如@检索标题运行示例 所示。

#figure(
  image("assets/2023-12-11-20-49-42.png", width: 80%),
  caption: "检索标题运行示例"
) <检索标题运行示例>

用户可以通过输入 `abstract nerf` 来检索摘要中含有 `nerf` 的论文，如@检索摘要运行示例 所示。

#figure(
  image("assets/2023-12-11-20-53-17.png", width: 100%),
  caption: "检索摘要运行示例"
) <检索摘要运行示例>

用户可以通过输入 `text "3d gaussian"` 来检索正文中含有 `3d gaussian` 的论文，如@检索正文运行示例 所示。

#figure(
  image("assets/2023-12-11-22-00-28.png", width: 80%),
  caption: "检索正文运行示例"
) <检索正文运行示例>

除此之外，Apache Lucene 还支持更多的检索功能，例如模糊检索、通配符检索、范围检索等等#footnote[https://lucene.apache.org/core/9_9_0/queryparser/org/apache/lucene/queryparser/classic/package-summary.html]，我们可以利用这些功能达到一些更加复杂的检索需求，例如想要检索标题中含有 editable 或 3d gaussian 的 Guosheng Lin 的论文，如@检索标题复杂运行示例 所示。

#figure(
  image("assets/2023-12-11-21-56-50.png", width: 100%),
  caption: "检索标题复杂运行示例"
) <检索标题复杂运行示例>

由于 Apache Lucene 的检索功能十分强大，我们可以通过不同的检索方式来满足不同的检索需求。而用户输入的第一个提示词只决定了默认的检索字段，用户可以通过输入其他提示词来指定检索字段，从而实现更加精准的检索。通过输入 `tips`，用户可以查看 Apache Lucene 支持的部分检索规则，如@检索提示词运行示例 所示。

#figure(
  image("assets/2023-12-11-22-20-03.png", width: 85%),
  caption: "检索提示词运行示例"
) <检索提示词运行示例>

= 总结

这个学术搜索引擎的主要实现都是通过调用相关库来实现，因此难度较小，但也考验了我们使用他人开发的库的能力。在编程过程中，由于库的版本等原因，遇到了许多磕绊。遇到这样的问题，我们需要仔细检查库的官方文档，找到每个方法对应的参数、作用和返回值，经过自己的不断调试，这个学术搜索引擎终于大功告成。

通过这个学术搜索引擎项目的开发，我深刻理解了网络爬虫、PDF解析、索引构建和用户检索系统的设计和实现。项目的开发过程中遇到了不少挑战，但这些挑战最终转化为了宝贵的经验。我认识到，注重细节的重要性，细小的错误可能会导致整个系统的不稳定。同时，这个项目也强化了我们作为程序员的严谨性和解决问题的能力。

这个项目不仅仅是对我们编程技能的锻炼，更是一次理论与实践结合的深刻体验。它让我们从实践中更好地理解了计算机科学的基本原理，同时也让我们意识到了自己的不足之处，比如在算法的应用和系统设计方面还有提升的空间。

在 Java 课程设计中，这个项目是一个宝贵的学习机会，不仅提高了我们的技术能力，也增强了我们团队合作和问题解决的能力。我们相信，通过这次经验的积累，我们将能够在未来的学习和工作中做得更好。