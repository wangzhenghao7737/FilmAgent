# 电影只能体项目
使用spring ai 和 阿里巴巴Dashscope模型。
涉及的ai内容：advisor，工具调用，rag，智能体（manus）
## src代码
### java/com/xiaosa/filmagent
#### advisor
自定义的advisor
#### agent
模仿manus的智能体
#### aitools
自定义的ai工具
#### chatmemory
自定义的chatmemory（基于文件存储）
#### component
#### configuration
#### constant
#### controller
#### entity
#### exception
#### properties
#### rag
自定义的rag实现
#### service
#### utils
确定有穷自动机，用于敏感词过滤
### resources
#### data
用于天气查询的省市区代码表
#### prompt
自定义提示词
## tmp
存放临时文件，可根据具体需求更改
### chat_memory
对话记忆存储位置
### document
用于rag的测试文件
### download
下载的文件存放位置
### file
普通文件存放位置
### pdf
pdf文件存放位置
### test
存放一些测试结果截图
## web_page
文件上传，简单对话，rag对话的简单页面