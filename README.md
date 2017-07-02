# QuickDevFramework
An android development library based on android basic architecture

## Retrofit-Base
基本版应用框架库，网络模块基于Retrofit构建，采用传统的监听回调模式，不使用RxJava的事件流  

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

#### 框架主要功能 
##### 三层架构
在这个架构中，DataManager负责绝大部分数据的处理和缓存控制，应用内所有的DataManager应该继承BaseDataManager  
所有的Activity和Fragment继承于BaseActivity和BaseFragment，以便使用框架提供的功能。 
在UI层不应该直接操作Retrofit的Call。数据库，文件操作等也应尽量交由DataManager处理以保证分层结构的清晰，另一方面也方便数据的重用和维护  

##### Retrofit请求绑定至组件生命周期 
在Manager中使用bindCall()将Retrofit的Call绑定至Manager，在Activity和Fragment中使用BindDataManager()方法将DataManager绑定组件生命周期，这样在组件生命销毁时取消Http请求，避免隐含的回调空指针异常等情况。

##### 基于Retrofit的网络层封装
当前版本的框架选择使用Retrofit作为异步调用库实现，Retrofit拥有高度封装的特性，一方面这方便了开发，另一方面也限制了扩展，框架在不改动Retrofit源码的基础上实现了以下扩展：  

1. Retrofit构建类RetrofitApiBuilder，在原有Retrofit构建方式的基础上封装了更为简便的Builder类，将添加自定义Headers等细节交由RetrofitApiBuilder处理，免去了自己写Interceptor的麻烦，同时设置了一些Retrofit构建所必须参数的默认实现，如ConvertFactory。大幅简化Retrofit Api的构建过程  
2. 提供了针对接口的Cookie管理模式，可以通过注解决定接口是否需要在请求时向Header中加入缓存Cookie
3. 快速支持Https,只需传入Https必要的参数即可实现对Https声明接口的支持  
4. 提供了Http请求信息抓取Interceptor类，可以抓取到详细的http请求信息，包含请求耗时等参数  

##### Android 6.0 以后的权限管理适配
在supprot包中提供了PermissionManager类，封装了Android 6.0以后的动态权限申请，主要功能是：  

1. checkPermissionsGranted() 检查应用是否拥有权限  
2. performWithPermission() 执行需要动态申请权限的代码，会在执行前检查是否拥有权限，如果没有则申请并提供申请成功/失败的回调  
3. requestSystemAlertWindowPermission() 申请 SYSTEM_ALERT_WINDOW 权限，并提供回调  
4. requestWriteSystemSettingsPermission() 申请 WRITE_SYSTEM_SETTINGS 权限，并提供回调  

_注意这个封装使用的是BaseActivity中设置的申请权限回调，如果使用者的Activity没有继承框架的Activity，需要将在onRequestPermissionsResult()中实现权限封装类的回调，否则权限申请的回调不会触发_  

##### SharedPreferences封装
此功能位于support包中的PreferencesWrapper, 可以切换不同权限等级的SharedPreference实现，并提供了存储double，Serializable对象等功能

##### AlertDialog封装
由于某些情况下声明弹出AlertDialog回调方法的UI组件已经被销毁，异步线程回调的AlertDialog不能正确弹出，亦或者需要从一些不持有Context的组件中发起弹出AlertDialog的请求，因此框架对这一需求进行了封装。封装类为support包中的AlertDialogManager，分为应用内的Dialog和全局级别的Dialog。  
应用内的Dialog功能比较多，可以设置确认和取消按钮的回调等，由EventBus负责派发Dialog消息至当前位于前台的Activity。  
全局级别的TopDialog由Activity承载，可以从Service弹出，但只能做消息通知，无法设置按钮回调。

##### Notification封装
很多商业应用在通知方面一般是如果应用开启，则用户点击通知时直接导航到目标页面，如果应用未开启，先启动应用再导航至目标页面。框架对于此功能的封装在support包中的NotificationManager类中。
SimpleNofiticationBuilder对一个Notification的必须参数进行了封装，也封装了常见的Style，如BigPicture, BigText, InboxMessages, 在构建时只需传入必要参数即可。
目前对于一些复杂的Notification以及自定义Notification还未做过处理，需要使用者自己构建，但是如果实现根据应用状态判断启动目标页面方式的功能，只需将Notification的Intent设置为NotifiactionWrapper()中getBroadcastIntent()方法获取到的Intent即可，详情请参考代码及注释。  

##### 文件管理封装
FileCopyTask,FileDeleteTask 通过AsyncTask的封装提供了对于文件操作的异步调用，并提供了按复制文件数和按复制总大小监听的两种模式

##### Log封装
主要目的是实现框架和业务模块对于Log的统一管理，同时对Log进行格式化输出，信息更详细，结构更更清晰。  
框架的Log管理类是位于support包中的Logger类，使用LogInterface接口切换不同的Log实现，框架提供了两套Log实现，分别为FrameworkLogImpl和SimpleLogImpl，在正式环境下可以将LogInterface实现切换到最简单的SimpleLogImpl，提升性能，详情请参考代码和注释。

##### 底部弹出DialogFragment封装
封装了从底部弹出Dialog这一常见Dialog样式，如需使用只需继承BaseBottomDialogFragment类即可

##### RecyclerView的Adapter封装
封装了一个功能强大的RecyclerView基类，提供了数据源管理，Header、Footer；提供Empty、Loading、Error等不同状态显示对应View的功能；  
目前正在扩展功能中......


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
