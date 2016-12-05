# QuickDevFramework
An android development library based on android basic architecture

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
