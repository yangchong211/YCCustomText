#### 目录介绍
- 01.文本TextView了解
- 02.如何文本对齐
- 03.文本对齐方案
- 04.遇到问题汇总


### 01.文本TextView了解
- TextView绘制一行的原理
    - TextView已经手动计算出了一行能显示多少的字符
    - 计算这一行显示不下一个单词的时候，就进行回车换行
- TextView如何计算一行显示多少字符
    - 看TextView中onDraw方法，定位源码到(6884行或者6890行)：layout.draw(canvas, highlight, mHighlightPaint, cursorOffsetVertical);
    - 交给layout处理，看Layout中的draw源码，做了两步：drawBackground绘制背景；drawText绘制文本内容
    - 然后看一下setText方法 -->  checkForRelayout()  --> autoSizeText() --> findLargestTextSizeWhichFits -->  suggestedSizeFitsInSpace  --> layoutBuilder.build()
    - layout是抽象类，它来处理文字排版工作。在单纯地使用TextView来展示静态文本的时候，这件事情则是由Layout的子类StaticLayout来完成的。看具体实现类StaticLayout的代码。
    - StaticLayout接收到字符串后，for循环叠加每个字符串长度，当字符串叠加长度大于屏幕的宽度时，对字符串进行拆分，拆分后的字符串内容放到下一行
- 文本的绘制工作流程
    - 简单地总结就是：分析整体文本—>拆分为段落—>计算整体段落的文本包括Span的测量信息—>对文本进行折行—>根据最终行数把文本测量信息保存—>绘制文本的行背景—>绘制最终的文本和图像。


### 02.如何文本对齐
- 两端对齐原理
    - 注意：左边是自动对齐，也可以理解为只需要处理右端对齐
    - 将一行剩余的宽度平分给当前单词的间距，这样来达到左右对齐的效果


### 03.文本对齐方案
- 第一种方案
    - TextView已经计算出了一行能显示多少的字符，那么只需要通过计算剩余的宽度再进行绘制即可；
- 第二种方案
    - 通过手动计算一行能显示多少个字符，然后再计算剩余的宽度进行绘制；
    - 中文还好说，英文单词可能会被拆分
- 第三种方案
    - 在第二种上进行优化
    - 参考：微信读书是对这个过长的单词进行截断处理，然后使用“-”符号将这两个截断的单词连接起来
    - 通过手动计算一行能显示多少个单词，如果一行最后一个单词显示不下，则进行截断处理，中文则不不存在该问题
- 第三种方案步骤
    -


### 04.遇到问题汇总



