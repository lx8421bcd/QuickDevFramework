# QuickDevFramework
这个框架可以通过框架模块的形式导入，包含了两个部分：一部分是为App开发提供一个基础的分层和模块化框架；
另一部分则是将常见问题的解决方案进行封装，成为一个基础支持库。


## Description
这是一个基于我设计的Android开发框架的开发库合集，包含大部分常用功能的封装，诸如应用内的消息通知、权限管理、网络调用等；提升了可靠性，简化开发流程。
这一系列开发框架最终的目的是希望能够在导入框架模块之后，开发者不用再关心绝大部分常用功能的处理，只要实现服务端接口，开发UI，接入第三方SDK即可。  

基于此框架的整个Android项目工程结构图如下：  
![project-architecture](https://github.com/ShonLin/QuickDevFramework/blob/master/architecture-images/project-architecture.png)  

## Usage
将项目下载下来将framework模块导入你的app工程即可。  
详细集成步骤，请参考 [Wiki](https://github.com/ShonLin/QuickDevFramework/wiki)  

update：为了方便小项目快速接入，以及工具类和关联资源的维护，目前将原有的retrofit-rx分支与mvvm-rx分支合并到master分支，统一管理。

#### 项目有以下分支：  
##### master
基于RxJava实现异步调用链的framework模块，网络库基于Retrofit，整体上采取UI-DataManager-Support的分层架构，也包含了MVVM基类和示例。 

有关MVVM架构的实现在app module内 [mvvm package](https://github.com/ShonLin/QuickDevFramework/tree/master/app/src/main/java/com/linxiao/quickdevframework/sample/mvvm)，
包含了MVVM基类和基于RxJava的MVVM验证码交互简单实现示例，在想要使用MVVM架构时可以参考，也可以将基类放在Framework模块中

##### retrofit-base
基于Retrofit和基本框架实现的framework模块，相当于master上的内容去除了RxJava之后的实现。  


## LICENSE
    Copyright 2016 linxiao

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
