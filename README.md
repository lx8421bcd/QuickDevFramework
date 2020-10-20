# QuickDevFramework
个人长期维护的以基础分层架构为核心实现的的Android框架library，代码库。

* [项目简介](#项目简介) 
* [项目概况](#项目概况)
  * [已经实现/封装较为完善的功能](#已经实现/封装较为完善的功能)
  * [开发中/规划内的功能](#开发中/规划内的功能)
  * [项目分支](#项目分支)
* [如何使用](#如何使用)
  * [搭建新项目](#搭建新项目)
  * [现有项目重构](#现有项目重构)
* [FAQ](#FAQ)
* [LICENSE](#LICENSE)

## 项目简介
此项目为本人在工作和个人项目中积累的开发经验凝结而成的代码库，长期维护，项目主要有以下几块构成：

1. 以个人构思的UI-DataManager-Support分层架构为项目的基础骨架，整理出一个业务无关的基础框架module，以便在有新项目时通过此库快速完成框架搭建。
2. 研究Android开发过程中遇到的问题以及解决方案，将能够提炼出来的代码以工具类、自定义控件、框架基类的形式存储在此项目中，以便在遇到类似问题时能够快速拿出解决方案
3. 将此项目作为一个新技术的实验平台，测试新的Gradle版本，SDK版本兼容性；此项目将会尽量保持使用最新的Gradle和targetSdkVersion。

此项目的目标是构建一个任何Android项目都可以使用的module，新项目导入此module之后只需实现部分基类即可完成框架搭建，开发者不用再关心绝大部分常用功能的处理，只要实现服务端接口，开发UI，接入第三方SDK即可。



## 项目概况
目前项目已经基本达到设计目的，以分层架构为基础，集成了常用的工具类和自定义控件，并对任何Android原生应用都会使用到的常见功能进行了一定封装。
基于此框架的整个Android项目工程结构图如下：

<p align="center">
  <img width="495" height="290" src="https://github.com/lx8421bcd/QuickDevFramework/blob/master/architecture-images/project-architecture.png">
</p>


#### 已经实现/封装较为完善的功能
* __ViewBinding集成__，集成ViewBinding功能，并提供基于ViewBinding封装的相关简化工具。
* __RxJava2集成__，应用内集成了RxJava、RxAndroid、RxLifeCycle等内容，大量组件以RxJava为异步实现基础。
* __分层框架基类__（Activity、Fragment、DataManager、Dialog等），为整个项目的骨架，为需要使用Android组件相关功能和生命周期的SDK和模块提供支持，同时为派生类统一提供大量便利的方法。
* __网络层封装__，基于OkHttp3和Retrofit2，提供快速构建RetrofitApi的方法、统一处理网络异常和数据解析、网络调用抓包、Cookie管理、SSL配置等功能。
* __权限管理__，封装自Android 6.0以来的运行时权限管理，简化权限申请和回调配置。
* __通知栏通知构建封装__，封装Notification构建，保证必须参数在构建时填入，同时封装Notification点击事件，提供唤起应用之前台，先启动应用再打开指定页面等功能。
* __SharedPreferences__，封装SharedPreferences常用存取操作，简化调用，为不同等级的SharedPreferences对象提供构建方法。
* __RecyclerViewAdapter封装__，封装了一个可以快速添加Header、Footer、LoadingView、EmptyView的RecyclerViewAdapter，提供了RecyclerView分页加载、Item等边距等常用功能的实现。
* __Dialog封装__，提供透明Activity包裹的AlertDialog实现，使Dialog不会因Activity销毁而销毁。
* __异步文件操作封装__，封装以File操作+AsyncTask为基础，封装剪切、复制、删除等文件异步操作，方便批量文件操作时使用。
* __工具类合集__，开发过程中经常需要使用的工具方法归纳整理，也包含对于框架内一些SDK的优化方法，比如Gson反序列化优化。
* __自定义控件合集__，整理应用开发中常用的自定义控件，比如下拉刷新、小红点、高亮引导等。
* __MVVM架构演示示例__，[mvvm package](https://github.com/lx8421bcd/QuickDevFramework/tree/master/app/src/main/java/com/linxiao/quickdevframework/sample/mvvm)，包含了MVVM基类和基于RxJava的MVVM验证码交互简单实现示例，在想要使用MVVM架构时可以参考，也可以将基类放在Framework模块中。
* __简易图片上传工具__，鉴于[Retrofit和OkHttp上传文件必带Content-Size导致某些服务端接收失败](https://github.com/square/okhttp/issues/2138)， 基于RxJava用HttpUrlConnection封装一个简易的图片上传工具，以应对应用中普遍存在的上传图片需求。
* __简易下载工具__，基于DownloadManager开发，封装权限检查，文件检查，下载进度回调等功能，用于应对一般应用的简易下载需求，比如下载更新包。
* __简易Logger__, 支持log输出超过单行最大值自动换行，提供打印当前线程信息，堆栈信息等工具方法。

#### 开发中/规划内的功能
* 升级RxJava至RxJava3
* 重构AlertDialog工具组件，降低复杂度
* 重构文件管理相关工具类
* 基于当前项目框架，添加常用UI模板，如单列表界面等
* 待续


#### 项目分支
* master - 项目主要分支，目前主要维护的分支
* retrofit-base，没有RxJava的分支，目前已停止维护



## 如何使用
**作为一个应用业务无关的应用框架/工具资源合集module，在任何情况下都推荐使用源码集成的方式将此项目整合进你的Android项目，以便修改和扩展**。

如果你将此项目当作一个代码库，想从中获取对你项目中有用的代码部分集成，那么直接将工具类、工具组件复制进你项目使用即可。

如果你准备集成此项目作为资源库和框架module，将framework module 导入你的项目，并在app的build.gradle中添加
 ```gradle
 implementation project(':framework')
 ```
这样即可完成绝大部分功能集成，但是框架继承以及一些基础配置根据集成目的可能会略有不同。


#### 搭建新项目
如果你是全新搭建项目，使用QuickDevFramework的分层架构，则需要进行以下步骤

##### 继承基类
1. **app中的Activity继承BaseActivity**，注意BaseActivity中实现了对PermissionManager的回调处理，如果子类Activity不继承BaseActivity则有可能无法正确收到权限申请回调。
2. **Fragment继承BaseFragment**。
3. **DialogFragment继承BaseDialogFragment**，如果你使用DialogFragment来管理你APP内的dialog。
4. 如果你的项目中有底部弹出Dialog的需求，可以考虑将此类继承BaseBottomDialogFragment，直接实现底部弹出Dialog效果，如果不使用DialogFragment，框架也提供了BottomDialog供业务模块使用
5. 数据管理的DataManager继承BaseDataManger，可以在BaseDataManager中实现通用功能

2、3、4的继承主要是为了在框架模块中提供一个基本的业务无关的组件基类，以此向子类提供共有的功能，可以继承也可以不继承，如果这些基类并不符合你的业务功能，请在framework模块中任意删改

##### 继承Style
出于简化开发的目的，我在framework模块的style.xml中添加了基础style并缓存了开发中常见的style配置，如果你需要使用这些配置，请在app的AndroidManifest.xml中直接使用。但是考虑到不同App module可能有不同的配置，推荐的集成方式还是在app的style.xml中配置基础style，继承framework中的style。
当然了，这个只属于简化开发的代码，集成与否对其它代码没有任何影响，统一样式是一个比较好的Android开发实践，个人推荐按照此方法管理应用中的style配置。

```xml
<!-- Base application theme. -->
<style name="AppTheme" parent="AppTheme.Base">
    <!-- Customize your theme here. -->
    <item name="colorPrimary">@color/colorPrimary</item>
    <item name="colorPrimaryDark">@color/colorPrimaryDark</item>
    <item name="colorAccent">@color/colorAccent</item>
    <item name="android:textAllCaps">false</item>
</style>
```


#### 现有项目重构
如果你是想将QuickDevFramework集成进现有的项目以优化项目架构，则需在集成framework module之后，进行以下配置

##### 修改基类
现有项目的组件基类（Activity、Fragment等）如果继承了某些SDK内特定的基类，比如皮肤包等，就无法直接继承framework module中的框架基类了，此时的应对办法有2种
1. 修改framework module中的框架基类，让其继承现有项目Base类的基类，此办法适用于SDK特定基类本身也属于业务无关，适用范围很广的情况
2. 将framework module中基类内部的实现复制到业务module的Base类中。这样做的话一个module中要维护一套基类代码，不太推荐，但这是应对继承冲突行之有效的解决方法之一，特别是SDK特定基类所属SDK本身不适合迁移到framework中的情况下。

##### Style统一
整合你业务module中的Style与framework中的Style，方法与上面的全新集成基本一致。

##### 业务无关组件/SDK迁移
将业务无关的SDK和工具组件迁移到framework module中，此步骤非必须，且优先级很低，但如果你是为公司项目集成此框架，完成此步骤可以将framework module变成一个为公司定制的Android平台的AppSDK，极大降低公司其他Android项目的开发成本。

## FAQ
#### Q：我不想使用Gson或者项目里的其他什么SDK，作者能否封装一层方便替换？
**A：封装一层不在项目规划之内，如果项目里有什么SDK你不想用，建议直接将其移除替换为自己要用的SDK，并将标红代码修改为对应实现**  

以JSON框架为例，一个Android项目里一般只需要一个就够了，而我们并没有频繁替换JSON解析框架的需求，把所有功能都封装一层代理出去，对于我们的项目来说并没有什么好处，反而提升了项目复杂度，以及框架的侵入性(侵入性简而言之就是，假如你哪天想要移除这个解析实现，需要删改很多东西)。

#### Q：我想做组件化/插件化，怎么实现？

**A：直接集成相关SDK构建对应的工程结构即可，不要在framework module中实现**

组件化/插件化跟这个framework module并不是一个层面的东西。framework的定位类似于一个业务module的骨架和支持工具类库，在后续的开发过程中务必要保持framework的定位（业务无关），这一点非常重要。一个巨无霸framework将会是后续维护的噩梦。

#### Q：我需要集成Tinker或者其他热修复，怎么做？

**A：如果你只是在某个平台（如手机）需要热更新，直接在业务module里面集成tinker就行了**

对于类似的SDK集成需求，本着一个思路去集成就好：

* 无论什么地方都会用 → framework
* 只在某个地方用 → 业务module
* 根据业务不同选择 → 单独构建一个library module，业务module选择集成

#### Q：希望/应该添加某些功能或者判断

**A：欢迎向本项目提交Pull Request，不过请遵循以下守则**

* **提交功能尽可能的遵循业务无关原则，功能应尽量适用于绝大多数Android项目**

* **提交class请遵循以下格式在class添加javadoc，方便使用者了其概况**

  ```java
  /**
   * 一句话简介
   * <p>
   * 类功能简要介绍，包括使用目的，注意事项等
   * </p>
   *
   * @author 作者昵称
   * @since 提交日期 yyyy-MM-dd 格式
   */
  public final class SomeUtils {
      
     /**
       * 对外方法简介，private方法如有必要也应添加注释
       *
       * <p>
       * 一些额外注释，如补充说明，注意事项等
       * </p>
       * @param param 入参说明
       * @return 如果返回参数不能明显判断出意义，请添加此项补充说明
       */
      public static SomeObject boolean someMethods(SomeParam param) {
          return someInstance;
      }
  }
  ```

* **请勿提交为了封装而封装的代码**  
我见过有人写的工具类，versionCode判断Android版本都要封装成方法，外面调用从```if(versionCode >= XXX)``` 变成```if(VersionUtil.isXXX())```…… 还有更奇葩的连启动Service都要封装一下，变成```OSUtils.startService(context);```。这种封装除了增强框架的侵入性，提升修改成本之外还有其他意义吗？这种只是把调用方式换成“我看着舒服”的封装，在开发中要尽量避免。  


这个项目最初立意是代码库+简易框架合集，一个目的是收集Android常见的业务无关的需求封装好拿来就用，另外一个目的依托提炼出来的分层框架方便做外包。我不希望framework变成一个无所不包的巨无霸module，内容应尽量精简，所以在往此项目里添加代码时十分谨慎。添加为了封装而封装的代码，将会严重破坏项目的价值。  
如果有普遍适用于某一行业的相关代码集合，建议以library module的形式放进codebase project，按需取用。  
之后可能会根据业务需求添加一些特定的module，如视频处理、直播、支付相关的SDK封装。可以根据业务需求集成，提升特定项目的开发速度，欢迎大家提出意见。



## LICENSE

    Copyright 2016 linxiao
    
    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at
    
       http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES or CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
