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
  show par: set block(spacing: 0pt)
  par("")
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
  v(28pt, weak: true)
  fake-par
}

#show heading: it => {
  v(12pt)
  text(
    font: hei,
    size: 12pt,
    weight: "bold",
  )[#it]
  v(24pt)
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

#set enum(indent: 2em, body-indent: 0.5em, numbering: "(1)(i)")
#set list(indent: 2em, body-indent: 0.5em)

#show enum : it => {
  v(4pt)
  it
  v(8pt)
  fake-par
}

#show list : it => {
  v(4pt)
  it
  v(8pt)
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
  v(24pt)
  fake-par
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

#show raw.where(block: true): it => {
  it
  fake-par
  v(4pt)
}

#cover(
  "2023 - 2024",
  "秋冬",
  "即使聊天应用程序设计",
  "3210106360",
  "杨沛山",
  "计算机科学与技术",
  "计科2104",
)

#outline-page()


= 引言

在当今信息技术高速发展的时代，互联网已经成为人们日常生活中不可或缺的一部分。随着社交网络和即时通信技术的日益成熟，人们之间的沟通方式也随之发生了翻天覆地的变化。在这样的背景下，我们开发了一款基于Java语言的聊天应用，旨在提供一个简洁、高效的在线交流平台。本应用不仅仅是一个简单的通信工具，它集成了前沿的网络技术，以及高效的数据处理能力，为用户提供了一个全新的沟通体验。

== 设计目的

我们的聊天应用使用Java Swing作为前端开发框架，在前端使用 SQLite 进行聊天消息本地存储，在服务端使用 MySQL 进行用户信息和用户离线消息的存储。我们的聊天应用具有以下功能特点：

1. 用户在在起始窗口中可以进行登录或注册操作，登录成功后进入主界面。
2. 主界面参考了现代化的大部分即时聊天软件的设计，左侧为好友列表，右侧为聊天窗口。
3. 用户可以在主界面中进行添加私聊、添加或创建群聊等操作。
4. 对于群聊，用户可以查看群成员列表，也可以退出群聊。
5. 在聊天窗口中，用户可以自由的进行复制、粘贴、剪切等操作。
6. 支持离线消息，用户可以在登录后查看离线消息。
7. 在后端，我们使用了多线程技术，保证了服务器的高并发性能。

== 设计说明

本程序采用 Java 程序设计语言，使用Java Swing作为前端开发框架，以其优秀的跨平台特性和灵活的界面设计，为用户带来了舒适和直观的操作体验。同时，应用后端通过Socket进行网络通信，保证了数据传输的实时性和稳定性。更为突出的是，该应用支持离线消息功能，即使在用户离线的情况下，消息也会被妥善存储，待到用户上线时即刻接收，这一点在提高用户体验上起到了关键作用。此外，消息的本地存储功能也极大地增强了应用的可用性和便捷性。

本次应用设计在 ItelliJ IDEA 开发平台下编辑、编译与调试，由笔者独立完成。

= 总体设计

本程序采用C/S程序设计架构，即客户端/服务器架构。客户端采用Java Swing作为前端开发框架，通过Socket与服务器进行通信，服务器采用Java Socket编程，通过多线程技术实现高并发性能。

== 客户端设计

=== 客户端功能模块设计

客户端需要实现的主要功能有：

1. 登录、注册功能
2. 添加私聊、添加或创建群聊功能
3. 查看群成员列表、退出群聊功能
4. 发送消息、接收消息功能
5. 消息本地持久化功能

客户端的总体功能如@客户端总体功能设计 所示：

#figure(
  image("assets/客户端总体功能设计.drawio.png", width: 70%),
  caption: "客户端总体功能设计",
) <客户端总体功能设计>

=== 客户端流程图设计

客户端总体流程如@客户端总体流程设计 所示：

#figure(
  image("assets/客户端流程图设计.drawio.png", height: 60%),
  caption: "客户端总体流程设计",
) <客户端总体流程设计>

== 服务端设计

=== 服务端功能模块设计

服务端需要实现的主要功能有：

1. 用户登录、注册功能
2. 群组用户管理功能
3. 接收客户端消息、发送消息功能
4. 离线消息存储功能

=== 服务端流程图设计

服务端总体流程如@服务端总体流程设计 所示：

#figure(
  image("assets/服务端流程图设计.drawio.png", height: 30%),
  caption: "服务端总体流程设计",
) <服务端总体流程设计>

= 详细设计

== 客户端详细设计

=== 客户端类设计

客户端的主要包括以下类：

1. `Main`：程序入口，启动客户端。
2. `Style`：定义了客户端的样式。
3. `Index`：起始窗口，用户可以在此进行登录或注册操作。
4. `Chat`：主界面，用户可以在此进行添加私聊、添加或创建群聊等操作。
5. `Client`：客户端，负责与服务器进行通信。
6. `Mapper`：消息本地持久化类，负责消息的本地存储，与本地数据库进行交互。
7. `model`：数据模型子包，包含了消息、客户端请求、服务器响应等数据模型。
8. `component`：组件子包，包含了自定义的客户端的各种组件，如按钮、输入框、列表等。

=== 起始界面设计

包含 `loginPanel` 和 `registerPanel` 两个面板，分别用于登录和注册操作。程序根据用户的选择，显示相应的面板。两个面板均采用了 `BoxLayout` 布局，以实现简洁的界面设计。

登陆面板的组件组成如@登陆面板设计 所示：

#figure(
  image("assets/登陆面板设计.png", width: 50%),
  caption: "登陆面板设计",
) <登陆面板设计>

注册面板的组成与登录面板基本一致，只是多了一个确认密码的输入框，此处不再赘述。

在登陆界面，还包含了一个 `ActionListener`，用于执行登陆操作。当用户在输入框中回车或点击登录按钮时，程序会调用该监听器，执行登陆操作。

=== 主界面设计

主界面的设计及其的复杂，其组件的组成以及包含关系如下列表所示：

#set list(indent: 0.5em, body-indent: 0.5em)
#v(5pt)

- `contentSplitPane`：为主界面的内容面板，用于分隔消息联系人列表与消息界面
  - `contactListPanel`：联系人列表面板，用于显示联系人列表
    - `addContactPanel`：添加联系人面板，用于添加私聊或群聊
      - `contactComboBox`：联系人下拉框，用于选择联系人类型，为私聊或群聊
      - `contactTextField`：联系人文本框，用于输入联系人名称
      - `addContactButton`：添加联系人按钮，用于添加联系人
    - `contactListScrollPane`：联系人列表滚动面板，用于显示联系人列表
      - `contactList`：联系人列表，用于显示联系人。对于每一个列表项，由以下部分组成：
        - `cellPanel`：列表项面板，用于显示列表项
          - `topPanel`：列表项顶部面板，用于显示列表项的名称和时间
            - `nameLabel`：列表项名称标签，用于显示列表项的名称
            - `timeLabel`：列表项时间标签，用于显示列表项的时间
          - `messagePanel`：列表项消息面板，用于显示列表项的消息
            - `messageLabel`：列表项消息标签，用于显示列表项的消息  
  - `messagePanel`：消息面板，用于显示消息
    - `titlePanel`：消息标题面板，用于显示消息的标题
      - `titleLabel`：消息标题标签，用于显示消息的标题
      - `quitRoomButton`：退出群聊按钮，用于退出群聊
    - `roomSplitPanel`：用于分隔消息面板和群成员列表面板
      - `messageSplitPanel`：用于分隔消息展示和发送面板
        - `messageScrollPane`：消息滚动面板，用于显示消息
          - `messagePane`：消息面板，用于显示消息
        - `inputPanel`：输入面板，用于输入消息
          - `inputScrollPane`：输入滚动面板，用于显示输入框
            - `inputPane`：输入文本框，用于输入消息
          - `sendButtonPanel`：发送按钮面板，用于发送消息
            - `sendButton`：发送按钮，用于发送消息
      - `roomMemberListPanel`：群成员列表面板，用于显示群成员列表
        - `roomMemberListLabel`：群成员列表标签，用于显示群成员列表的标题
        - `roomMemberListScrollPane`：群成员列表滚动面板，用于显示群成员列表
          - `roomMemberList`：群成员列表，用于显示群成员列表。对于每一个列表项，由以下部分组成：
            - `roomMemberCellPanel`：群成员列表项面板，用于显示群成员列表项
              - `roomMemberNameLabel`：群成员列表项名称标签，用于显示群成员列表项的名称

效果展示如下：

#figure(
  image("assets/主界面设计.png", width: 80%),
  caption: "主界面设计",
) <主界面设计>

=== 客户端数据库设计

客户端使用 SQLite 进行消息的本地存储，数据库中保存了用户的登录信息和用户的聊天消息。

#align(center)[#table(
    columns: 5,
    align: (col, row) => (auto, auto, auto, auto, auto,).at(col),
    inset: 6pt,
    [字段名],[类型],[是否为空],[是否唯一],[备注],
    [from],[TEXT],[否],[否],[发送者],
    [to],[TEXT],[否],[否],[接收者],
    [content],[TEXT],[否],[否],[内容],
    [isRoom],[INTEGER],[否],[否],[是否为群聊],
    [timestamp],[INTEGER],[否],[否],[时间戳],
  )
]

该表的建表语句如下：

```sql
CREATE TABLE IF NOT EXISTS messages
(
    "from"    TEXT    not null,
    "to"      TEXT    not null,
    content   TEXT    not null,
    isRoom    INTEGER not null,
    timestamp INTEGER not null
);
```

与 `model` 包中的 `Message` 类对应，包含了消息的发送者、接收者、内容、是否为群聊、时间戳等信息。

为了避免用户的聊天记录丢失，在程序运行中，我们使用 `ScheduledExecutorService` 定时执行消息的本地存储操作，保证了消息的实时性和可靠性。

== 服务端详细设计

=== 服务端类设计

服务端的主要包括以下类：

1. `Main`：程序入口，启动服务器。
2. `Controller`：控制器，在创建 Socket 连接后，会创建一个线程运行该类，用于接收客户端的消息、发送回调消息，并将消息转发给其他客户端。
3. `service`：服务子包，包含了 `AccountService` `MessageService` `SessionService` 等服务类，分别用于处理用户和群组账户、消息、用户会话等。
4. `model`：数据模型子包，包含了消息、客户端请求、服务器响应等数据模型。
5. `Mapper`：数据库访问类，负责与数据库进行交互。
6. `ConnectionPool`：数据库连接池，负责管理数据库连接。

=== 服务端数据库设计

服务端使用 MySQL 进行用户信息和用户离线消息的存储，数据库中保存了三张表，分别对应用户信息、群组信息和用户离线消息。

==== 用户信息表

用户信息表用于存储用户的登录信息，包含了用户的用户名、密码和邮箱等信息。

#align(center)[#table(
    columns: 5,
    align: (col, row) => (auto, auto, auto, auto, auto,).at(col),
    inset: 6pt,
    [字段名],[类型],[是否为空],[是否唯一],[备注],
    [username],[VARCHAR(50)],[否],[是],[用户名],
    [password],[VARCHAR(50)],[否],[否],[密码],
    [email],[VARCHAR(50)],[否],[是],[邮箱],
  )
]

该表的建表语句如下：


```sql
create table if not exists user
(
    username varchar(50) not null
        primary key,
    password varchar(50) not null,
    email    varchar(50) not null,
    constraint table_name_pk_2
        unique (email)
);
```

==== 群组信息表

群组信息表用于存储群组的信息，包含了群组的名称和群组成员等信息。

#align(center)[#table(
    columns: 5,
    align: (col, row) => (auto, auto, auto, auto, auto,).at(col),
    inset: 6pt,
    [字段名],[类型],[是否为空],[是否唯一],[备注],
    [name],[VARCHAR(50)],[否],[否],[群组名称],
    [member],[VARCHAR(50)],[否],[否],[外键，群组成员，与用户表关联],
  )
]

该表的建表语句如下：

```sql
create table if not exists room
(
    name   varchar(50) not null,
    member varchar(50) not null,
    primary key (name, member),
    constraint table_name_user_username_fk
        foreign key (member) references user (username)
            on update cascade on delete cascade
);
```

==== 用户离线消息表

用户离线消息表用于存储用户的离线消息，包含了消息的发送者、接收者、内容、是否为群聊、时间戳等信息。

#align(center)[#table(
    columns: 5,
    align: (col, row) => (auto, auto, auto, auto, auto,).at(col),
    inset: 6pt,
    [字段名],[类型],[是否为空],[是否唯一],[备注],
    [username],[VARCHAR(50)],[否],[否],[用户名],
    [from],[VARCHAR(50)],[否],[否],[发送者],
    [to],[VARCHAR(50)],[否],[否],[接收者],
    [content],[TEXT],[否],[否],[内容，编码为UTF-8],
    [isRoom],[TINYINT(1)],[否],[否],[是否为群聊],
    [timestamp],[MEDIUMTEXT],[否],[否],[时间戳],
  )
]

该表的建表语句如下：

```sql
create table if not exists save_messages
(
    username  text                          not null,
    `from`    text                          not null,
    `to`      text                          not null,
    content   text collate utf32_unicode_ci not null,
    isRoom    tinyint(1)                    not null,
    timestamp mediumtext                    not null
);
```

=== 服务端数据库连接池设计

服务端使用了数据库连接池技术，以提高数据库的访问效率。数据库连接池的设计流程图如@数据库连接池设计 所示：

#figure(
  image("assets/数据库连接池设计.drawio.png", height: 25%),
  caption: "数据库连接池设计",
) <数据库连接池设计>

=== 服务端数据库访问类设计

服务端使用了 `JDBC` 技术，通过 `PreparedStatement` 对象执行 `SQL` 语句，以实现对数据库的访问。数据库访问类具有以下方法：

1. `validateUser(String username, String password)`：验证用户登录信息，返回用户是否存在。
2. `register(String username, String password, String email)`：注册用户，将用户信息插入到数据库中。
3. `getRoomMembers(String roomName)`：获取群组成员列表。
4. `quitRoom(String roomName, String username)`：退出群组。
5. `joinRoom(String roomName, String username)`：加入群组。即向群组成员列表中插入一条记录 `roomName, username`。
6. `checkRoom(String roomName)`：检查群组是否存在。
7. `checkUser(String username)`：检查用户是否存在。
8. `saveMessage(String username, Message message)`：保存消息。即将消息插入到用户离线消息表中。
9. `getMessages(String username)`：获取用户的离线消息，并从用户离线消息表中删除该用户的离线消息。

=== 服务端服务类设计

服务端的服务类主要包括以下类：

1. `AccountService`：用户账户服务类，负责处理用户账户相关的操作。包括用户登录、注册、退出群组、加入群组、创建群组、获取群组成员列表等操作。
2. `MessageService`：消息服务类，负责处理消息相关的操作。包括获取离线消息、将消息转发给其他客户端等操作。
3. `SessionService`：用户会话服务类，负责处理用户会话相关的操作。包括用户登录后，将用户信息保存到会话中，用户退出后，将用户信息从会话中删除等操作，以及获取某个用户对应的会话、判断某个用户是否在线等操作。

=== 服务端控制器设计

服务端的控制器类 `Controller` 负责与客户端进行通信。详细设计见@服务端与客户端通信设计 部分。

== 服务端与客户端通信设计 <服务端与客户端通信设计>

客户端与服务器的通信采用了Socket编程，客户端通过Socket与服务器建立连接，然后通过Socket进行数据传输。

=== 数据传输协议设计

客户端与服务器之间的数据传输采用了自定义的数据传输协议。其中，所有的数据传输均以 Java 对象序列化的形式进行，即客户端和服务器之间传输的数据均为 Java 对象。数据传输协议的设计如下：

客户端发送的请求为 `ChatRequest` 对象，该类包含了请求的类型和请求的参数和相关数据。服务器接收到客户端的请求后，会根据请求的类型，执行相应的操作，并返回一个 `ChatResponse` 对象，该类包含了响应的类型、状态以及相关数据。客户端接收到服务器的响应后，会根据响应的类型，执行相应的操作。由于我们的聊天应用设计中所有的请求都是阻塞式的，即客户端发送请求后，会一直等待服务器的响应，因此我们不需要使用序列号来标识请求和响应的对应关系，只需要在响应中包含请求的类型即可。若服务器执行成功，则响应的状态为 `success`，否则为 `fail`，且附带的数据为错误信息。

`ChatRequest` 类的设计如下：

```java
    public class ChatRequest implements Serializable {
        @Serial
        private static final long serialVersionUID = 101L;
        private String command;
        private Object payload;
    }
```

`ChatResponse` 类的设计如下：

```java
    public class ChatResponse implements Serializable {
        @Serial
        private static final long serialVersionUID = 102L;
        private String command;
        private String status;
        private Object payload;
    }
```

请求的类型和响应的类型均为字符串，具体的请求类型和响应类型如下表所示：

#align(center)[#table(
    columns: 4,
    align: (col, row) => (auto, auto, auto, auto,).at(col),
    inset: 6pt,
    [类型],[作用],[参数],[请求成功响应],
    [login],[登录],[UserInfoBody，包括username和password],[Vector\<Message> 用户离线消息],
    [register],[注册],[UserInfoBody，包括username、password和email],[null],
    [send],[发送消息],[Message，包括from、to、content、isRoom和timestamp],[null],
    [getRoomMembers],[获取群组成员列表],[String，群组名称],[Vector\<String> 群组成员列表],
    [quitRoom],[退出群组],[String，群组名称],[null],
    [joinRoom],[加入群组],[String，群组名称],[null],
    [createRoom],[创建群组],[String，群组名称],[null],
    [checkUser],[检查用户是否存在],[String，用户名],[null],
  )
]

=== 服务端与客户端通信流程设计

在客户端中，创建 Socket 连接后，会创建一个线程，用于接收服务器的消息。主线程与子线程之间通过 `CompletableFuture` 进行通信，每当子线程收到服务器的回调消息后，会将消息通过 `CompletableFuture` 传递给主线程，主线程的调用者可以通过 `CompletableFuture` 获取到子线程的消息。

在服务器中，创建 Socket 连接后，会创建一个线程，用于处理该客户端会话的请求，并给出响应。在接收到请求后，会调用响应的服务类，执行相应的操作，并将操作的结果封装成响应，发送给客户端。

客户端与服务器通信时，每一次用户操作需要通信时的流程如@客户端与服务器通信流程设计 所示：

#figure(
  image("assets/客户端与服务器通信流程设计.drawio.png", height: 30%),
  caption: "客户端与服务器通信流程设计",
) <客户端与服务器通信流程设计>

= 测试与运行

== 程序测试

在经过反复的编码和细致的调试之后，我们的聊天应用已经基本完成了预期功能，并在多个测试环境下进行了全面的验证。程序在核心聊天功能、界面布局以及用户交互体验方面均表现良好，与现代流行的聊天软件在操作感和视觉效果上持平，未发现重大的错误或缺陷。然而，在细节和附加功能方面，我们的应用仍有改进和增强的空间。

在测试过程中，笔者也尝试了使用 IntelliJ IDEA 的 Run with Coverage 功能，对程序的测试覆盖率进行了测试。在对程序进行了近乎 100% 的测试覆盖后，未发现存在任何 bug。可以说，我们的程序在已有的测试环境下，已经具备了较高的稳定性和可用性。

然而，我们的程序还十分的简陋，仍有许多功能有待完善。例如，我们的程序仅支持文本消息，不支持图片、视频等多媒体消息，也不支持文件传输功能。此外，我们的程序也没有实现消息的撤回功能，也没有实现消息的多端同步功能。这些功能都是现代聊天软件的标配，我们的程序在这些方面还有很大的提升空间。

== 程序运行

程序打开时进入的是起始界面，如@起始界面 所示：

#figure(
  image("assets/起始界面.png", width: 50%),
  caption: "起始界面",
) <起始界面>

用户可以在起始界面进行登录或注册操作。在登录界面，用户需要输入用户名和密码，若登陆失败，则会弹出错误提示框，如@登陆失败提示框 所示：

#figure(
  image("assets/登陆失败提示框.png", width: 50%),
  caption: "登陆失败提示框",
) <登陆失败提示框>

点击注册按钮后，会进入注册界面，如@注册界面 所示：

#figure(
  image("assets/注册界面.png", width: 50%),
  caption: "注册界面",
) <注册界面>

用户需要输入用户名、密码和邮箱，若注册失败，则会弹出错误提示框，如@注册失败提示框 所示：

#figure(
  image("assets/注册失败提示框.png", width: 50%),
  caption: "注册失败提示框",
) <注册失败提示框>

用户登录成功后，会进入主界面，如@主界面 所示：

#figure(
  image("assets/主界面.png", width: 80%),
  caption: "主界面",
) <主界面>

主界面的左侧为联系人列表，右侧为消息界面。左侧的联系人列表中可以添加私聊、添加或创建群聊。在消息界面中可以复制、粘贴、剪切，以及发送消息。点击左侧的联系人列表项，可以进入私聊界面，如@私聊界面 所示：

#figure(
  image("assets/私聊界面.png", width: 80%),
  caption: "私聊界面",
) <私聊界面>

点击列表项中的群组名称，可以进入群聊界面，如@群聊界面 所示：

#figure(
  image("assets/群聊界面.png", width: 80%),
  caption: "群聊界面",
) <群聊界面>

点击群聊界面右上角的退出群聊按钮，可以退出群聊。双击群成员的列表项，可以与该成员进行私聊。

在左上角的输入框中，可以输入想要添加的私聊或群聊对话，通过下拉框选择私聊或群聊，然后点击添加按钮，即可添加私聊或群聊。添加私聊时，若用户不存在，则会弹出错误提示框，如@添加私聊失败提示框 所示：

#figure(
  image("assets/添加私聊失败提示框.png", width: 80%),
  caption: "添加私聊失败提示框",
) <添加私聊失败提示框>

添加群聊时，若群组不存在，则会弹出错误提示框，提示用户可以创建群聊，如@添加群聊失败提示框 所示：

#figure(
  image("assets/添加群聊失败提示框.png", width: 80%),
  caption: "添加群聊失败提示框",
) <添加群聊失败提示框>

添加成功后，该私聊或群聊会出现在左侧的联系人列表中。

= 总结与展望

== 总结

本次设计和实现的即时聊天应用程序是一次对Java编程语言及其相关技术的深入探索和实践。通过这个项目，我们不仅学习了Java Swing库和Socket编程的基础知识，还学习了多线程技术、网络通信原理、数据库操作等高级编程技术。这款应用程序的成功开发，展示了Java语言在网络应用开发领域的强大潜力和灵活性。这款聊天应用程序，不仅满足了基础的即时通讯需求，而且提供了一个平台，促进了信息的流通和人际交流的便捷性。我们的努力使这个应用变得不仅仅是一个软件产品，更是一种连接人与人之间沟通的桥梁。这让我似乎回到了几十年前，当时的互联网还处于萌芽阶段，人们通过电子邮件进行沟通，而我们的聊天应用，正是这种沟通方式的现代化延续，仿佛带着我回到了第一次使用 QQ 的时候。

在技术实现方面，我们精心打造了一个稳定、可靠的通信环境。我们的聊天应用涵盖了登录、注册、私聊、群聊、消息存储、用户和群组管理等核心功能，满足了基本的即时通讯需求。界面设计方面，我们注重用户体验，利用Java Swing构建的用户界面简洁明了，为用户带来了愉悦的视觉体验。同时，我们在安全性和稳定性上也下了不少功夫，确保了应用的可靠性。通过这个项目，我们展示了技术实现与用户体验之间的和谐结合，为软件开发树立了新的标准。

然而，我们也意识到应用在功能和性能上还有待提升。例如，多媒体消息、文件传输、消息撤回和多端同步等功能的缺失，限制了应用的实用性。此外，尽管我们进行了多轮测试，但在不同设备和网络环境下应用的表现仍需要进一步验证。我们清楚地认识到，技术的进步是永无止境的。在未来的发展道路上，我们将继续探索，不断创新。

最重要的是，我们能够在设计、编写这个应用程序的过程中，不断精进自己的技术水平，为以后的人生道路打下更加坚实的基础。这是我们最大的收获。

== 展望

面对未来，我们满怀信心。我们计划将这款聊天应用推向新的高度，对这款聊天应用进行一系列的优化和扩展，如支持发送图片、视频、文件等多媒体消息，增加消息撤回和编辑功能，实现消息的多端同步、云端备份等，甚至集成人工智能技术以提供智能助手服务。这些功能的加入将大大增强应用的互动性和用户体验。

在添加新功能的同时，我们将继续优化应用的性能和稳定性，特别是在网络连接不稳定的环境下。这包括优化数据传输效率，提高程序的响应速度，以及增强应用的容错能力。在安全性方面，我们也将不断强化应用的数据保护机制，确保用户的通信安全和隐私保护。我们计划引入更加严格的数据加密技术，以及更为完善的用户认证和授权机制。

当然，我们十分注重用户体验，以人为本。我们希望能够持续收集用户反馈，根据用户的实际需求和使用体验，不断调整和改进应用功能。我们期待这款聊天应用不仅能满足基本的沟通需求，更能成为推动社交互动和信息交流的重要平台。

当然，新功能意味着新挑战，新挑战意味着新收获。相信我们在不断打磨、优化这个即时聊天工具的同时，也能够不断提升自己的应用开发能力。我们的水平，必将与时俱进，与日俱增。