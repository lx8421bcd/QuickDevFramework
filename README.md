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
项目有以下分支：  
#### master
主要是合并提交记录，README更新等，__framework模块请按需求从其它分支选取__  

#### retrofit-base
基于Retrofit和基本框架实现的framework模块  

#### retrofit-rx
基于RxJava实现异步调用链的framework模块，网络库仍然基于Retrofit，整体上仍采取UI-DataManager-Support的分层架构

#### mvvm-rx
在retrofit-rx基础上引入Google的android-architecture-components组件作为MVVM实现方案  

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
