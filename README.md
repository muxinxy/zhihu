# zhihu

## 知乎日报api

api来源为[知乎日报api分析][zhihu_api]

## 活动

### 1.MainActivity  
>13. 栏目总览  
>请注意！ 此 API 仍可访问，但是其内容未出现在最新的『知乎日报』 App 中。  
>URL: http://news-at.zhihu.com/api/3/sections  
>响应实例:  
>>*  {  
>>*      data: [  
>>*          {  
>>*              id: 1,  
>>*              thumbnail: "http://p2.zhimg.com/10/b8/10b8193dd6a3404d31b2c50e1e232c87.jpg",  
>>*              name: "深夜食堂",  
>>*              description: "睡前宵夜，用别人的故事下酒"  
>>*          },  
>>*      ...  
>>*      ]  
>>*  } 

>同样，注意使用 thumbnail 获取图像的地址

### 2.MessageActivity  
>14. 栏目具体消息查看  
>请注意！ 此 API 仍可访问，但是其内容未出现在最新的『知乎日报』 App 中。  
>URL: http://news-at.zhihu.com/api/3/section/1  
>URL 最后的数字见『栏目总览』中相应栏目的 id 属性  
>响应实例：  
>>  {  
>>      news: [  
>>          {  
>>              date: "20140522",  
>>              display_date: "5 月 22 日"  
>>          },  
>>      ...  
>>      ],  
>>      name: "深夜食堂",  
>>      timestamp: 1398780001  
>>  } 

>往前：http://news-at.zhihu.com/api/3/section/1/before/1398780001  
>在 URL 最后加上一个时间戳，时间戳详见 JSON 数据末端的 timestamp 属性

*  *  *  *
## 控件

### 下拉刷新  
*swipelayout*

*  *  *  *

[知乎][zhihu]

[知乎日报][zhihu_daily]

[zhihu]:www.zhihu.com "知乎"

[zhihu_api]:https://github.com/izzyleung/ZhihuDailyPurify/wiki/%E7%9F%A5%E4%B9%8E%E6%97%A5%E6%8A%A5-API-%E5%88%86%E6%9E%90"知乎日报api分析"

[zhihu_daily]:https://daily.zhihu.com/"知乎日报"
