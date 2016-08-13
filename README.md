# Analyser

####这里全部是个人测试专用项目

####请注意

这里有内存泄露的分析，分析方法，这里有详细demo  [你使用Rxjava时，内存泄漏了吗？](http://www.jianshu.com/p/c720ec2b5383)

这里有一些比较有意思的 surfaceView 绘图测试

这里有aop方案实现动态申请权限   文章在这里 [这里有aop方案实现动态申请权限](http://www.jianshu.com/p/ea4cc77bf984)

这里有aidl的demo，作者在aidl实践中发现新手需要注意的几点：

* 在AS中，AIDL中如果要使用自己定义的类model，需要实现parcelable，且model所在package要和项目的对应上。
* 注意参数In 的使用
* 注意远程callback调用前后，beginBroadcast和finishBroadcast。


**注意---自己更改工程的`build.gradle`,因为我是用的公司的仓库，外部是访问不到的。**


