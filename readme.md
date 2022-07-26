## CN行政区划2021

- url:      http://www.stats.gov.cn/tjsj/tjbz/tjyqhdmhcxhfdm/2021/
  https://baike.baidu.com/item/%E6%96%B0%E7%96%86%E7%94%9F%E4%BA%A7%E5%BB%BA%E8%AE%BE%E5%85%B5%E5%9B%A2
- language: JAVA8 [ https://www.oracle.com/java/technologies/downloads/#java8 ]
- lib:      Jsoup [ https://mvnrepository.com/artifact/org.jsoup/jsoup/1.15.2 ]

2021年全国统计用区划代码（12位）、城乡分类代码（3位）

更新时点为2021年10月31日

北京市、天津市、河北省、山西省、内蒙古自治区、辽宁省、吉林省、黑龙江省、上海市、江苏省、浙江省、安徽省、福建省、江西省、山东省、河南省、湖北省、湖南省、广东省、广西壮族自治区、海南省、重庆市、四川省、贵州省、云南省、西藏自治区、陕西省、甘肃省、青海省、宁夏回族自治区、新疆维吾尔自治区、新疆生产建设兵团

未包括我国台湾省、香港特别行政区和澳门特别行政区。

## 区划层级结构
- 1 国家
  - 2 省、直辖市、自治区、兵团
    - 3 市、直辖县、自治州、地区、师
      - 4 县、县级市、地区、团、农场、牧场
        - 5 镇、街道、乡、村
          - 6 社区、居委会、村委会

## 调用api

- 查询行政区划树，限制向下钻取2层
  - API: https://rilirili.top/t/area
  - Method: GET
  - Parameter:
    - code 行政区划编码（非必填）
    - mod 向下层级（非必填，数字）


- 根据编码查询行政区划树，向上查询
  - API: https://rilirili.top/t/area/{code:\d+}
  - Method: GET
  - Parameter:
    - code 行政区划编码（必填）
    - mod 向上层级（非必填，数字）
