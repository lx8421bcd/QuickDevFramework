# QuickDevFramework
An android development library based on android basic architecture

<<<<<<< HEAD
## Retrofit-Base
基本版应用框架库，网络模块基于Retrofit构建    

### Usage
将project中的framework module导入你的项目中使用即可:)  
各个组件的使用说明详见framework module中的注释  


### Description
网络模块由 Retrofit 2.0 构建；framework模块的结构和app工程的建议结构如图  
![retrofit-base architecture](https://github.com/ShonLin/QuickDevFramework/blob/master/architecture-images/retrofit-base.png)  

应用整体分为4层，各层概要介绍如下：

* UI层：应用UI相关的模块，比如Activity、Fragment、Dialog、自定义View等，Activity、Fragment、DialogFragment除非是有特殊用途，否则建议整体继承自       framework包的BaseActivity、BaseFragment等基类，享受框架提供的功能。

* DataManager层：负责应用整体的数据管理，UI层与其交互通过该层提供的ActionCallbackListener接口，在回调中返回数据。UI层不可直接处理缓存，网络调用，这   些功能应该全部由DataManager负责。具体的DataManagers建议继承自framework的BaseManager，享受框架提供的功能。

* DataStore层：本层主要由各种数据管理类和声明的Server API组成，提供实例给上层调用

* Support层：本层包含各种工具类、系统功能的封装和第三方SDK模块，为整个App提供支持，从严格意义上来讲并不属于“层”更像是App的Support Library，整个App内   各层皆可使用本模块提供的功能，但是同理，跟UI相关的封装和工具类仍然建议只在UI层中使用。
=======

## Description
这是一个基于我设计的Android开发框架的开发库合集，包含大部分常用功能的封装，诸如应用内的消息通知、权限管理、网络调用等；提升了可靠性，简化开发流程。
这一系列开发框架最终的目的是希望能够在导入框架模块之后，开发者不用再关心绝大部分常用功能的处理，只要实现服务端接口，开发UI，接入第三方SDK即可。  

基于此框架的整个Android项目工程结构图如下：  
![project-architecture](https://github.com/ShonLin/QuickDevFramework/blob/master/architecture-images/project-architecture.png)  

其中CommonDevLibrary是一个项目无关的公共代码库，我在Github上也有共享 
[CommonDevLibrary](https://github.com/ShonLin/CommonDevLibrary)

## Project Branches
* 基于Retrofit实现网络模块基本版：[retrofit-base] (https://github.com/ShonLin/QuickDevFramework/tree/retrofit-base)  

后面将陆续支持RxJava、MVP、MVVM等方案


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
>>>>>>> master
